
/*--------------------------
 *      Tigris 0.3.0
 * 	Mesopotamia Client v1
 * (C) Niklas Weissner 2014
 *-------------------------- 
 */

//TODO: Implement better error handling all over the script
//TODO: Sort some code fragments so the source can be easier understood
//TODO: Comment on stuff
//TODO: Localization maybe?
//TODO: Type safety in packets

//Configure this to your Euphrates installation
//If your endpoint is absolute, make sure you include the full URI (including protocol etc.)
var ENDPOINT_IS_RELATIVE = false;
var MESO_ENDPOINT = "ws://localhost:8080/mesopotamia/TIG_TEST_END"; //Link to Euphrates


//Constants
var TIGRIS_VERSION = "0.3.0";
var TIGRIS_SESSION_COOKIE = "2324-tigris-session";

var GENERAL_TIMEOUT = 2000; //Default timeout in milliseconds (may be overridden by some packets)

var PTYPE =
{
	HANDSHAKE: 	1,
	ACCEPT:		2,
	LOGIN: 		10,
	AUTH: 		11,
	RELOG: 		12,
	REAUTH: 	13,
	LOGOUT: 	14,
	QUERY: 		20,
	DATA: 		21,
	ACK: 		200,
	NACK: 		201,
	ERROR: 		242
};

var ERRORCODE =
{
	UNKNOWN:			0,
	INVALID_PACKET:		1,
	SESSION_EXPIRED:	2,
	INTERNAL_EXCEPTION:	3,
	INVALID_RESPONSE:	4,
	WRONG_VERSION:		5,
	NOT_ALLOWED:		6
};

var LOGOUTREASON =
{
	UNKNOWN:			0,
	CLOSED_BY_USER:		1,
	SESSION_EXPIRED:	2,
	INTERNAL_ERROR:		3,
	REFUSED:			4
};

var socket;
var sentPacketMap = {};

var currentTab = null; //The data screen tab currently displayed TODO: Make this a bit fancier

var dashboard = null;

$(document).ready(
function()
{
	
	ui_init();
	
	Network.init();
});


//-------------UI stuff---------------

function ui_init()
{

	ui_showStatus("Connecting to the server..."); //Initially show messeage on connection status
	
	//Set up events
	$("#loginform").submit(
			function(e) 
			{
				e.preventDefault();
				
				f_tryLogin();
			});
	
	$("#logout").click(
			function(e)
			{
				f_tryLogout();
			});
	
	$(".sidebar-item").click(
			function(e)
			{
				ui_sidebarItemClicked(e.target);
			});
	
	$("#sidebar-handle").mouseover(
			function(e)
			{
				ui_slideOutSidebar();
			});
	
	$("#sidebar").mouseout(function(e)
			{
				if(!$(e.relatedTarget).hasClass("sidebar-item") && (e.relatedTarget != document.getElementById("sidebar")))
				{
					ui_slideAwaySidebar();
				}
			});
	
	/*$(".sidebar-item").mouseover(function(e)
	{
		$(e.target).addClass("sidebar-item-hover", 200);
	});
	
	$(".sidebar-item").mouseleave(function(e)
	{
		$(e.target).removeClass("sidebar-item-hover", 200);
	});*/
	
	ui_setUpDashboard(); //Set up dashboard TODO: Review if this is really needed (currently only for testing)
}



/**
 * Show screen with given name without any animation.
 * 
 * @param name The name of the screen to be displayed
 */
function ui_showScreen(name)
{
	$(".screen").hide(); //Hide all screens...
	$("#screen-" + name).show(); //and show only disered one
}

/**
 * Display data screen with fading animation. Used for the login->dataview-transition.
 */
function ui_showDataScreen()
{
	//Play nice fading animation
	$("#screen-login").hide("puff", {}, 600, 
			function()
			{ 
				$("#screen-data").fadeIn();
			});
	
	$(".data-frame-tab").hide(); //Initially hide all dashboard tabs...
	ui_data_showTab("dashboard"); //and show dashboard
}

function ui_showStatus(msg, error)
{
	$("#statusbox").html(msg);
	if(error)
	{
		$("#statusbox").addClass("error");
	}else
	{
		$("#statusbox").removeClass("error");
	}
	
	ui_showScreen("status");
}

function ui_slideOutSidebar()
{	
	//FIXME: Sidebar not dissappearing if user moves cursor out of handle while sliding out
	$("#sidebar-handle").fadeOut(100,
			function()
			{
				$("#sidebar").show("slide",300);
			});
}

function ui_slideAwaySidebar()
{	
	$("#sidebar").hide("slide", 300,
			function()
			{
				$("#sidebar-handle").fadeIn(100);
			});
}

function ui_data_showTab(name)
{
	if(currentTab != null)
	{
		currentTab.fadeOut(function()
				{
					$("#tab-" + name).fadeIn();
				});
		
	}else
	{
		$("#tab-" + name).fadeIn();
	}
	
	currentTab = $("#tab-" + name);
}

function ui_showError(msg)
{
	console.error(msg);
	
	ui_showStatus(msg, true);
}

function ui_login_showError(msg)
{
	$("#loginerror").html(msg);
	
	$("#loginerror").show();
	
	$("#loginerror").effect("shake");
}

function ui_sidebarItemClicked(eventTarget)
{	
	var item = $(eventTarget);
	
	if(!item.hasClass("sidebar-item-active"))
	{
		//Animate sidebar buttons
		$(".sidebar-item-active").each(function(index)
				{
					$(this).removeClass("sidebar-item-active", 350);
				});
		item.addClass("sidebar-item-active", 350);
		
		
		var target = item.attr("data-target");
		
		ui_data_showTab(target);
	}
}

function ui_setUpDashboard()
{
	
	
	//TODO: Put this to data screen init
	$(".data-frame-tab").hide(); //Hide all tabs
	ui_data_showTab("dashboard"); //Initially show dashboard
		
	dashboard = new Dashboard();
	
	var tile1 = new TileMachine(false, {id: 12345, name: "asdfg", speed:500});
	dashboard.addTile(tile1);
	
	//TODO: Do this in the dashboard constructor
	$(".dashboard-column").sortable(
			{
				connectWith: ".dashboard-column",
				placeholder: "tile-placeholder",
				
				update: ui_dsh_resort,
				start: ui_dsh_dragStart
			});
}

function ui_dsh_resort(e,ui)
{
	
}

function ui_dsh_dragStart(e,ui)
{
	//As jQuery UIs sortable does not support dynamic sized placeholders,
	//we must change them manually
	if(ui.item.hasClass("tile"))
	{
		//Only change the placeholder for actual tiles beeing dragged
		
		$(".tile-placeholder").css("height",dashboard.tileWidth); //Height is always the same
		$(".tile-placeholder").css("width", ui.item.hasClass("tile-half") ? dashboard.tileWidth : dashboard.tileWidth*2);
		
	}
	
}


//-------------Functional stuff------------

function f_tryLogin()
{
	var username = $("#loginform_username").val();
	
	//Encrypt password in accordance with specification of MCP 1.2.1
	var passwordHashObject = CryptoJS.SHA256($("#loginform_password").val());
	$("#loginform_password").val("");
	var saltedPasswordHash = passwordHashObject.toString(CryptoJS.enc.Hex) + connectionData.salt; //TODO: replace hex encoder with self written one. plain ridicolous to use a library for that
	var finalPasswordHash = CryptoJS.SHA256(saltedPasswordHash).toString(CryptoJS.enc.Hex);
	delete passwordHashObject; //Delete unneeded vars containing sensible data. For safety :)
	delete saltedPasswordHash;
	
	var packet = new Packet_Login(username, finalPasswordHash);
	packet.onResponse = 
		function(pk)
		{
			if(pk.typeID == PTYPE.AUTH)
			{
				//Login successful -> store session data and open data view
				var sessionID = pk.data.sessionID;
				
				connectionData.username = username;
				$("#username").html(connectionData.username); //TODO: Put this somewhere it belongs
				
				f_setUpSession(sessionID);
				
			}else if(pk.typeID == PTYPE.NACK)
			{
				ui_login_showError("Your login data is incorrect"); //MSG_LOGIN_BADLOGINDATA
			}
		};
		
	Network.sendPacket(packet);
}

function f_tryRelog()
{
	var cook = util_getCookie(TIGRIS_SESSION_COOKIE);
	
	if(cook != null)
	{
		//There was a session ID stored -> ask server if it is still valid
		
		var packet = new Packet_Relog(cook);
		packet.onResponse = 
			function(pk)
			{
				if(pk.typeID == PTYPE.REAUTH)
				{
					//The old session ID was still valid -> use echoed (maybe changed) session ID
					Network.connectionData.sessionID = pk.data.sessionID;
					
					//Store the new session ID to cookie TODO: maybe integrate this into f_setUpSession()
					util_setCookie(TIGRIS_SESSION_COOKIE, pk.data.sessionID);
					
					//As there was no login, the server has to tell us who we are
					Network.connectionData.username = pk.data.username;
					$("#username").html(Network.connectionData.username); //TODO: Put this somewhere it belongs
					
					//As the session was still valid, we can directly display the data view
					ui_showScreen("data");
					$("#data-frame > .data-frame-tab").hide(); //Initially hide all data tabs...
					ui_data_showTab("dashboard"); //and show dashboard
					
				}else if(pk.typeID == PTYPE.NACK)
				{
					//The old session ID has expired -> user has to login again
					ui_showScreen("login");
				}
			};
			
		Network.sendPacket(packet);
		
	}else
	{
		//No stored session -> user has to login
		
		ui_showScreen("login");
	}
}

function f_tryLogout()
{
	
	var packet = new Packet_Logout(LOGOUTREASON.CLOSED_BY_USER);
	
	packet.onResponse = 
		function(pk)
		{
			if(pk.typeID == PTYPE.ACK)
			{
				//Server has acknowledged logout -> Remove old session cookie and do another HANDSHAKE before showing login form again
				util_setCookie(TIGRIS_SESSION_COOKIE, "", -9999); //This cookie expired years ago! Spit it out!
				
				Network.handshake();
			}
		};
		
	packet.onTimeout =
		function()
		{
			//The server did not confirm the logout. This may have unforeseen consequences, Dr. Freeman. So better not show the login page again. 
			//And smell the ashes. Don't forget to smell the ashes.
			
			//We force the user to reload the page, so if there is really a problem with the server, he will notice on the next handshake
			ui_showError("The server did not confirm the last logout. You can start another session by reloading Tigris.");
		};
	
	Network.sendPacket(packet);
}

function f_setUpSession(sessionID)
{
	connectionData.sessionID = sessionID;
	
	util_setCookie(TIGRIS_SESSION_COOKIE,sessionID);
	
	ui_showDataScreen();
}

/**
 * Updates all tiles on the dashboard.
 * 
 * @param category String containing the category of data sent
 * @param data Array of data objects sent by the server
 */
function f_updateTiles(category, data)
{
	jQuery.each(data, 
			function(index, item)
			{
				var dataUnitIdent = category + "-" + item.id;
				
				if(dataUnitIdent in dashboard.tiles)
				{
					//There is a tile linked to the current data unit
					
					dashboard.tiles[dataUnitIdent].onUpdate(item);
				}
				
			});
}

/**
 * Namespace for network related functions
 */
var Network = {};

Network.socket = null;
Network.sentPacketMap = {};

Network.connectionData = 
{
	salt: null,
		
	sessionID: null,
	username: ""
};

Network.init = function()
{
	//Set up socket
	var wsURI;
	
	if(ENDPOINT_IS_RELATIVE)
	{
		var loc = window.location;
		
		if(loc.protocol === "https:")
		{
			wsURI = "wss:";
			
		}else{
			wsURI = "ws:";
		}
		
		wsURI += "//" + loc.host;
		wsURI += loc.pathname + MESO_ENDPOINT;
	}else
	{
		wsURI = MESO_ENDPOINT;
	}
	
	try
	{
		Network.socket = new WebSocket(wsURI);
	}catch(e)
	{
		ui_showError("Could not create socket.");
		return;
	}
	
	Network.socket.onopen = Network.ws_onOpen;
	
	Network.socket.onmessage = Network.ws_onMessage;
	
	Network.socket.onclose = Network.ws_onClose;
	
	Network.socket.onerror = Network.ws_onError;
};

Network.ws_onOpen = function()
{
	Network.handshake(); //Only start handshaking after connection has been established
};

Network.ws_onMessage = function(msg)
{
	console.log("I received this: " + msg.data);
	
	var packet;

	try
	{
		packet = jQuery.parseJSON(msg.data);
		
	}catch(e)
	{	
		console.error("Message was not in JSON format or shit. \n" + e);
		
		ui_showError("A fatal communication error occurred: A message could not be parsed into a packet.");
		
		return;
	}
	
	if(packet.uid == "undefined" || packet.typeID == "undefined" || packet.data == "undefined")
	{
		console.error("Packet had missing fields.");
		
		ui_showError("A fatal communication error occurred: Bad packet and shit.");
		
		return;
	}
	
	if(("uid" + packet.uid) in Network.sentPacketMap) //UID is registered -> Packet is an answer
	{
		
		var requestingPacket = Network.sentPacketMap["uid" + packet.uid]; //Get the packet that requested this answer
		
		//Remove the timeout before it goes off in our hands (if there is any)
		if(requestingPacket.timeoutID != null)
		{
			window.clearTimeout(requestingPacket.timeoutID);
		}
		
		delete Network.sentPacketMap["uid" + packet.uid];
		
		if($.inArray(packet.typeID , requestingPacket.allowedResponses) == -1)
		{
			//This packet type is not allowed as a response to the requesting packet
			// -> send INVALID_ANSWER ERROR packet
			
			//TODO: Send packet
			console.error("Received invalid response packet: " + packet.typeID + " is not allowed as a response to " + requestingPacket.typeID);
			
			ui_showError("Received invalid response packet: " + packet.typeID + " is not allowed as a response to " + requestingPacket.typeID);
			
		}else
		{
		
			requestingPacket.onResponse(packet);
			
		}
			
	}else // -> Packet is a request
	{
		Network.request(packet);
	}
};

Network.ws_onClose = function()
{
	console.log("The connection was closed");
};

Network.ws_onError = function(e)
{
	console.error("Network error");
	
	ui_showError("A network error occurred. Please contact the system admin you do not know."); //ERR_COMM_NETWORK
};

Network.handshake = function()
{

	var packet = new Packet_Handshake();
	packet.onResponse = 
	function(pk)
	{
		if(pk.typeID == PTYPE.ACCEPT)
		{
			console.log("Handshake successful");
			
			Network.connectionData.salt = pk.data.salt;
			
			//Connection to server is OK -> The user may log in
			f_tryRelog(); //First look if there is already a session to be restored. TODO: This is confusing. Make it better
			
		}else if(pk.typeID == PTYPE.ERROR)
		{
			//TODO: This one can be omitted after error packets are bypassed to general error processing
			//TODO: Maybe not. Review this later
			
			if(pk.data.errorCode == ERRORCODE.WRONG_VERSION)
			{
				console.error("Handshake refused: Client version is incompatible with server");
			
				ui_showError("Connection refused by server: This version of Tigris is too old/new"); //ERR_COMM_CLIENTINCOMPATIBLE
			}else
			{
				console.error("Handshake refused: Unknown reason");
				
				ui_showError("Connection refused by server for unknown reason");
			}
		}
	};
	
	Network.sendPacket(packet);
};

Network.generateUID = function()
{
	var uid = 0;
	var rounds = 0;
	
	while(("uid" + uid) in sentPacketMap)
	{
		uid += 2; //Generate only even UIDs
		
		if(rounds++ > 256) //Don't mess around too long in this unperformant routine
		{
			console.error("Packet queue overloaded!");
			return -1;
		}
	}
	
	return uid;
};

Network.sendPacket = function(packet, uid)
{
	var packetToSend = {};
	
	packetToSend.typeID = packet.typeID;
	packetToSend.data = packet.data;
	packetToSend.uid = uid || Network.generateUID();
	
	var jsonPacket = JSON.stringify(packetToSend);
	
	if(Network.socket.readyState == 1)
	{
		Network.socket.send(jsonPacket);
		
		//Set a timeout for each packet that is removed upon response in the n_ws_onMessage methode (only if timeout is > 0)
		if(packet.timeout > 0)
		{
			packet.timeoutID = window.setTimeout(packet.onTimeout, packet.timeout);
		}
		
		Network.sentPacketMap["uid" + packetToSend.uid] = packet;
		
		console.log("I sent this: " + jsonPacket);
		
	}else if(Network.socket.readyState == 2)
	{
		console.error("Tried to send after the connection was lost");
		
		ui_showError("Tried to send packet after the connection was lost. This is most likely the programmers fault.");
	}else if(Network.socket.readyState == 0)
	{
		console.error("Tried to send while the socket was still connecting");
		
		ui_showError("Tried to send packet while the socket was still connecting. This is most likely the programmers fault.");
	}
};

Network.request = function(pk)
{
	
	if(pk.typeID == PTYPE.DATA)
	{
		f_updateTiles(pk.data.category, pk.data.result);
	}
	
};

//------screen constructors----------

function Dashboard()
{
	this.tiles = {}; //Map of tiles on the dashboard indexed with data unit identifier (category-ident)
	
	this.root = $("#tab-dashboard");
	
	//The jQuery width method seems not to work with a hidden object thats css defines only percentages.
	//As the dashboard has the width of the document with absolute margins, we can calculate the tile sizes using the documents width
	this.width = $(document).width() - 10;
	this.height = $(document).height() - 47;
	
	this.columnCount = 5; //Work with a fixed column count for now
	this.columns = new Array();
	this.columnWidth = this.width / this.columnCount;
	
	this.tileWidth = (this.columnWidth - 10) / 2; //Width of a half tile (including the 5px margin on each side)
	
	//Create columns
	for(var i = 0; i < this.columnCount ; i++)
	{
		var col = $("<div>");
		col.attr("id","dash-col-" + i);
		col.addClass("dashboard-column");
		col.css("width",this.columnWidth);
		
		this.root.append(col);
		
		this.columns[i] = col;
	}
	
	
	this.addTile = 
		function(tile)
		{
			var dataUnitIdent = tile.dataUnitCategory + "-" + tile.dataUnitID;
		
			if(dataUnitIdent in this.tiles)
			{
				//Tile linking to the same data source is already on the dashboard -> refuse adding
				return;
			}
			
			this.tiles[dataUnitIdent] = tile;
		
			this.columns[0].append(tile.base); //For testing: append tile 0 to dashboard TODO: Add parameter for desired column
		};
	
	
}

//-------tile constructors------------

/**
 * @constructor
 */
function TileMachine(full, machine)
{
	//Create the basic tile stuff every tile gets
	this.dataUnitCategory = "machine";
	this.dataUnitID = machine.id;
	this.dataUnitIdent = this.dataUnitCategory + "-" + this.dataUnitID;
	
	this.base = $("<div>");
	this.base.attr("id","tile_" + this.dataUnitIdent);
	this.base.addClass("tile");
	this.base.addClass(full ? "tile-full" : "tile-half");
	this.base.css("height", dashboard.tileWidth); //TODO: Maybe do this without global access
	this.base.css("width", full ? dashboard.tileWidth*2 : dashboard.tileWidth);
	
	
	this.title = $("<h2>");
	this.base.append(this.title);
	
	//create gauge for production speed
	this.speedGaugeCanvas = $("<canvas>");
	this.speedGaugeCanvas.css("width", dashboard.tileWidth);
	this.speedGaugeCanvas.css("height", dashboard.tileWidth * 0.88);
	
	this.speedGauge = this.speedGaugeCanvas.gauge(
		{
			lines: 12, // The number of lines to draw
			angle: 0.15, // The length of each line
			lineWidth: 0.44, // The line thickness
			pointer: 
			{
				length: 0.85, // The radius of the inner circle
				strokeWidth: 0.02, // The rotation offset
				color: '#000000' // Fill color
			},
			limitMax: 'false',   // If true, the pointer will not go past the end of the gauge
			colorStart: '#DA0404',   // Colors
			colorStop: '#DA0404',    // just experiment with them
			strokeColor: '#DA0404',   // to see which ones work best for you
			generateGradient: false
		}).data().gauge;
	
	this.base.append(this.speedGaugeCanvas);
	
	this.onUpdate = 
		function(machine)
		{
			this.title.html("Machine '" + machine.name + "'");
			
			this.speedGauge.maxValue = 1000; //TODO: Get max speed from somewhere
			this.speedGauge.set(machine.speed);
		};
		
		
	//When finished creating tile, update its information (when there is some)
	if(machine)
	{
		this.onUpdate(machine);
	}
}


//--------packet contructors-----------
/**
 *
 * @constructor
 */
function Packet_Handshake()
{
	this.typeID = PTYPE.HANDSHAKE;
	
	this.data = {};
	this.data.clientVersion = TIGRIS_VERSION;
	
	this.timeout = GENERAL_TIMEOUT; //TODO: Find something like superconstructor to init the stuff every packet has
	this.timeoutID = null;
	this.allowedResponses = [PTYPE.ACCEPT, PTYPE.ERROR];
	
	this.onResponse = function(){};
	this.onTimeout = function(){ui_showError("The server took to long to respond.");};
}

/**
 *
 * @constructor
 */
function Packet_Login(usr,pwrdHash)
{
	this.typeID = PTYPE.LOGIN;
	
	this.data = {};
	this.data.username = usr;
	this.data.passwordHash = pwrdHash;
	
	this.timeout = GENERAL_TIMEOUT;
	this.timeoutID = null;
	this.allowedResponses = [PTYPE.AUTH, PTYPE.NACK];
	
	this.onResponse = function(){};
	this.onTimeout = function(){ui_showError("The server took to long to respond.");};
}

/**
 * 
 * @constructor
 */
function Packet_Relog(sessionID)
{
	this.typeID = PTYPE.RELOG;

	this.data = {};
	this.data.sessionID = sessionID;
	
	this.timeout = GENERAL_TIMEOUT;
	this.timeoutID = null;
	this.allowedResponses = [PTYPE.REAUTH, PTYPE.NACK];
	
	this.onResponse = function(){};
	this.onTimeout = function(){ui_showError("The server took to long to respond.");};
}

/**
 * 
 * @constructor
 */
function Packet_Logout(reason)
{
	this.typeID = PTYPE.LOGOUT;
	
	this.data = {};
	this.data.reasonCode = reason || LOGOUTREASON.UNKNOWN;
	
	this.timeout = GENERAL_TIMEOUT;
	this.timeoutID = null;
	this.allowedResponses = [PTYPE.ACK];
	
	this.onResponse = function(){};
	this.onTimeout = function(){ui_showError("The server took to long to respond.");};
}

/**
 * 
 * @constructor
 */
function Packet_Error(code, message)
{
	this.typeID = PTYPE.ERROR;
	
	this.data = {};
	this.data.errorCode = code;
	this.data.errorMessage = message;
	
	this.timeout = 0;
	this.timeoutID = null;
	this.allowedResponses = [];
	
	this.onResponse = function(){};
	this.onTimeout = function(){};
}

//---------------utility stuff----------------

/**
 * Sets cookie with specific name, value and time-to-live.
 * 
 * @param name Name of the cookie
 * @param value Value of the cookie
 * @param ttl Lifespan of the cookie in seconds
 */
function util_setCookie(name, value, ttl)
{
	var expires = new Date();
	expires.setSeconds(expires.getSeconds() + ttl);

	var valueFormat = escape(value) + ((expires==null) ? "" : "; expires=" + expires.toUTCString());

	document.cookie = name + "=" + valueFormat;
}

/**
 * Returns the value of a cookie with a specific name. Undefined cookies
 * will be returned as null.
 * 
 * @param name Name of the cookie to be returned
 * 
 * @returns Value of cookie or null if undefined
 */
function util_getCookie(name)
{
	var i, x, y;
	var ARRcookies = document.cookie.split(";");

	for (i = 0; i<ARRcookies.length; i++)
	{
		x = ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
		y = ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
		x = x.replace(/^\s+|\s+$/g,"");
		if (x == name)
		{
			return unescape(y);
		}
	}	
}
