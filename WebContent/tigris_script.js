
/*--------------------------
 *      Tigris 0.3.1
 * 	Mesopotamia Client v1
 * (C) Niklas Weissner 2014
 *-------------------------- 
 */

//TODO: Sort some code fragments so the source can be easier understood
//TODO: Comment on stuff
//TODO: Localization maybe?
//TODO: Maybe check for type specific fields?

//Configure this to your Euphrates installation
//If your endpoint is absolute, make sure you include the full URI (including protocol etc.)
var ENDPOINT_IS_RELATIVE = true;
var MESO_ENDPOINT = "TIG_TEST_END"; //Link to Euphrates


//Constants
var TIGRIS_VERSION = "0.3.1";
var TIGRIS_SESSION_COOKIE = "2324-tigris-session";

var GENERAL_TIMEOUT = 5000; //Default timeout in milliseconds (may be overridden by some packets)

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

var DCAT =
{
	MACHINE:	1,
	JOB:		2
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
	
	UI.init();
	
	Network.init();
});


//-------------UI stuff---------------

/**
 * Namespace for user interface related functions
 */
var UI = {};

UI.currentScreen = "status"; //Status message for noscript is displayed by default

UI.init = function()
{	
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
				UI.sidebarItemClicked(e.target);
			});
	
	$("#sidebar-handle").mouseover(
			function(e)
			{
				UI.slideOutSidebar();
			});
	
	$("#sidebar").mouseout(function(e)
			{
				if(!$(e.relatedTarget).hasClass("sidebar-item") && (e.relatedTarget != document.getElementById("sidebar")))
				{
					UI.slideAwaySidebar();
				}
			});
	
	//Create tables
	//Status values decoded for testing
	UI.statusTest = {};
	UI.statusTest[1] = "Running";
	UI.statusTest[2] = "Error";
	UI.statusTest[3] = "Repair";
	UI.statusTest[4] = "Modification";
	UI.statusTest[5] = "Cleaning";
	 
	UI.machineTable = $("#table-machine").dataTable({
		"aoColumns": [
		              { "mData": "id" },
                      { "mData": "name" },
                      { "mData": "job" },
                      { "mData": "speed" },
                      { "mData": function(data, type, val) { return UI.statusTest[data.status]; }}
                    ]});
	
	$("#table-job").dataTable({
		"aoColumns": [
		              { "mData": "id" },
                      { "mData": "target" },
                      { "mData": "startTime" },
                      { "mData": "productType" }
                    ]});
	
	UI.showStatus("Connecting to the server..."); //Initially show message on connection status
};



/**
 * Show screen with given name without any animation.
 * 
 * @param name The name of the screen to be displayed
 */
UI.showScreen = function(name)
{
	$(".screen").hide(); //Hide all screens...
	$("#screen-" + name).show(); //and show only disered one
	
	UI.currentScreen = name;
};

/**
 * Displays login screen and hides any error message in the login dialog.
 */
UI.showLogin = function()
{
	UI.showScreen("login");
	
	$("#loginerror").hide();
};

/**
 * Displays data screen. Plays fading animation when transiting from login to data screen.
 */
UI.showData = function()
{
	if(UI.currentScreen == "login")
	{
		//Play nice fading animation
		$("#screen-login").hide("puff", {}, 600, 
				function()
				{ 
					$("#screen-data").fadeIn();
				});
	}else
	{
		UI.showScreen("data");
	}
	
	//Set up user information
	$("#username").html(Network.connectionData.username);
	
	
	$(".data-frame-tab").hide(); //Initially hide all dashboard tabs...
	UI.data_showTab("dashboard"); //and show dashboard
};

/**
 * Displays status screen with optional error coloring for the error message.
 */
UI.showStatus = function(msg, error)
{
	$("#statusbox").html(msg);
	if(error)
	{
		$("#statusbox").addClass("error");
	}else
	{
		$("#statusbox").removeClass("error");
	}
	
	UI.showScreen("status");
};

/**
 * Displays status screen with error message and logs it to console.error for debugging.
 */
UI.showError = function(msg)
{
	console.error(msg);
	
	UI.showStatus(msg, true);
};


UI.slideOutSidebar = function()
{	
	//FIXME: Sidebar not dissappearing if user moves cursor out of handle while sliding out
	$("#sidebar-handle").fadeOut(100,
			function()
			{
				$("#sidebar").show("slide",300);
			});
};

UI.slideAwaySidebar = function()
{	
	$("#sidebar").hide("slide", 300,
			function()
			{
				$("#sidebar-handle").fadeIn(100);
			});
};

UI.sidebarItemClicked = function(eventTarget)
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
		
		var targetTabTable = $("#tab-" +target + " table");//TODO: Find a fancier way to do this
		if(targetTabTable)
		{
			//Target tab is table view -> Load table
			
			UI.data_updateTable(targetTabTable);
		}
		
		UI.data_showTab(target);
	}
};


UI.login_showError = function(msg)
{
	$("#loginerror").html(msg);
	
	$("#loginerror").show();
	
	$("#loginerror").effect("shake");
};


UI.data_showTab = function(name)
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
};

/**
 * Updates a data view table.
 * 
 * @param table A jQuery object of the table to be updated
 */
UI.data_updateTable = function(table)
{	
	
	//Update machine table for testing
	var tab = UI.machineTable;
	
	var pkq = new Packet_Query(DCAT.MACHINE, "*");
	pkq.onResponse = function(pk)
	{
		tab.fnClearTable();
		
		if(pk.typeID == PTYPE.DATA)
		{
			tab.fnAddData(pk.data.result);
		}
	};
	Network.sendPacket(pkq);
	
};


//-------------Functional stuff------------

function f_tryLogin()
{
	var username = $("#loginform_username").val();
	var remember = $("#loginform_remember").is(":checked");
	
	//Encrypt password in accordance with specification of MCP 1.2.1
	var passwordHashObject = CryptoJS.SHA256($("#loginform_password").val());
	$("#loginform_password").val("");
	var saltedPasswordHash = passwordHashObject.toString(CryptoJS.enc.Hex) + Network.connectionData.salt; //TODO: replace hex encoder with self written one. plain ridicolous to use a library for that
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
				Network.connectionData.sessionID = pk.data.sessionID;
				
				Network.connectionData.username = username;
				
				//Store remember state for this session
				Network.connectionData.remember = remember;
				if(remember) //Store session ID in cookie if session is to be remembered
				{
					util_setCookie(TIGRIS_SESSION_COOKIE, Network.connectionData.sessionID);
				}
				
				UI.showData(); //Everything is set up -> Show data screen
				
			}else if(pk.typeID == PTYPE.NACK)
			{
				UI.login_showError("Your login data is incorrect"); //MSG_LOGIN_BADLOGINDATA
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
					//Since this cookie was created in a session the user wanted to keep, we can assume the user wants still to keep it
					util_setCookie(TIGRIS_SESSION_COOKIE, pk.data.sessionID);
					
					//As there was no login, the server has to tell us who we are
					Network.connectionData.username = pk.data.username;
					$("#username").html(Network.connectionData.username); //TODO: Put this somewhere it belongs
					
					//As the session was still valid, we can directly display the data view
					UI.showData();
					
				}else if(pk.typeID == PTYPE.NACK)
				{
					//The old session ID has expired -> user has to login again
					UI.showLogin();
				}
			};
			
		Network.sendPacket(packet);
		
	}else
	{
		//No stored session -> user has to login
		
		UI.showLogin();
	}
}

function f_tryLogout()
{
	
	var packet = new Packet_Logout(LOGOUTREASON.CLOSED_BY_USER, "User requested logout");
	
	packet.onResponse = 
		function(pk)
		{
			if(pk.typeID == PTYPE.ACK)
			{
				//Server has acknowledged logout -> Remove session data and do another HANDSHAKE before showing login form again
				Network.clearSession();
				
				//Handshake takes care of showing login form
				Network.handshake(false); //Don't try a relog this time. It won't succeed anyway
			}
		};
		
	packet.onTimeout =
		function()
		{
			//The server did not confirm the logout. This may have unforeseen consequences, Dr. Freeman. So better not show the login page again. 
			//And smell the ashes. Don't forget to smell the ashes.
			
			//We force the user to reload the page, so if there is really a problem with the server, he will notice on the next handshake
			UI.showError("The server did not confirm the last logout. You can start another session by reloading Tigris.");
		};
	
	Network.sendPacket(packet);
}

function f_serverSideLogout(reason)
{
	var msgs = {};
	msgs[LOGOUTREASON.UNKNOWN] = "of an unknown reason.";
	msgs[LOGOUTREASON.SESSION_EXPIRED] = "your session lost its validity.";
	msgs[LOGOUTREASON.INTERNAL_ERROR] = "an internal server error occurred.";
	msgs[LOGOUTREASON.REFUSED] = "you were somehow suddenly refused.";
	msgs[LOGOUTREASON.CLOSED_BY_USER] = "it says the user did it. Alright, who was it then?";
	
	Network.clearSession();
	
	UI.showLogin();
	UI.login_showError("The server logged you out because " + msgs[reason]);
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

Network.remember = false;

Network.connectionData = 
{
	salt: null,
		
	sessionID: null,
	username: ""
};

/**
 * Removes any session related data to begin a new session.
 */
Network.clearSession = function()
{
	Network.connectionData.sessionID = null;
	Network.connectionData.username = "";
	
	util_setCookie(TIGRIS_SESSION_COOKIE, "", -9999);
};

Network.init = function()
{
	//Set up socket
	var wsURI;
	
	if(ENDPOINT_IS_RELATIVE)
	{
		//The endpoint path is relative -> we must construct absolute path from our current URL
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
		UI.showError("Could not create socket. Your browser probably doesn't support WebSockets.");
		return;
	}
	
	//Redirect the socket's handlers to our script
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
		UI.showError("A fatal communication error occurred: A message could not be parsed into a packet.");
		
		return;
	}
	
	if(packet.uid === "undefined" || packet.typeID === "undefined" || packet.data === "undefined")
	{
		UI.showError("A fatal communication error occurred: Bad packet and shit.");
		
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
		
		//Check if the received response is applicable to the requesting packets response handler
		if($.inArray(packet.typeID , requestingPacket.allowedResponses) == -1)
		{
			if(packet.typeID == PTYPE.ERROR)
			{
				//ERROR packets can be sent in response to any packet, but are
				//directed to the general error handler if the requesting packet does not care for it
				
				Network.generalError(packet.data.errorCode, packet.data.errorMessage);
				
			}else
			{
				//The server did something wrong. We should tell him
				var errorMsg = packet.typeID + " is not allowed as a response to " + requestingPacket.typeID;
				var errorPacket = new Packet_Error(ERRORCODE.INVALID_RESPONSE, errorMsg);
				
				Network.sendPacket(errorPacket);
				
				//We should tell the user, too
				UI.showError("Received invalid response packet: " + errorMsg);
			}
			
		}else
		{
			//Direct response to the requesting packet handler
			requestingPacket.onResponse(packet);
			
		}
			
	}else // -> Packet is a request
	{
		if(packet.typeID == PTYPE.ERROR)
		{
			//ERROR sent as request is considered as a general error
			
			Network.generalError(packet.data.errorCode, packet.data.errorMessage);
			
		}else
		{
			Network.request(packet);
		}
	}
};

Network.ws_onClose = function()
{
	console.log("The connection was closed");
	
	UI.showStatus("The connection to the server was closed.");
};

Network.ws_onError = function(e)
{
	UI.showError("A network error occurred. Please contact the system admin you do not know."); //ERR_COMM_NETWORK
};

Network.handshake = function(tryRelog)
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
			//First look if there is already a session to be restored.
			
			if(tryRelog || true)
			{
				f_tryRelog(); //This method also displays login screen if no session is to be restored; No need for us to take care of that
			}else
			{
				UI.showLogin();
			}
			
		}else if(pk.typeID == PTYPE.ERROR)
		{
			
			if(pk.data.errorCode == ERRORCODE.WRONG_VERSION)
			{
				UI.showError("Connection refused by server: This version of Tigris is too old/new"); //ERR_COMM_CLIENTINCOMPATIBLE
			}else
			{
				UI.showError("Connection refused by server for unknown reason");
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
		
		if(rounds++ > 256) //Don't mess around too long in this unperformant routine TODO: Make this routine more performant
		{
			UI.showError("Packet queue overloaded! This is most likely a bug.");
			return -1;
		}
	}
	
	return uid;
};

/**
 * Sends a packet to the server. Stores it if it awaits any responses so they can
 * be passed to the response handler in the packet object. This method generates
 * an UID if it is not specified.
 * 
 * @param packet The packet object to be sent
 * @param uid Optional: The uid the packet should be sent with
 */
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
		
		//Check if packet expects any responses
		if(packet.allowedResponses.length > 0)
		{
			//Store packet so a response can be passed to its response handler
			Network.sentPacketMap["uid" + packetToSend.uid] = packet;
			
			//Set a timeout for each packet that is removed upon response in the n_ws_onMessage methode (only if timeout is > 0)
			if(packet.timeout > 0)
			{
				packet.timeoutID = window.setTimeout(packet.onTimeout, packet.timeout);
			}
		}
			
		console.log("I sent this: " + jsonPacket);
		
	}else if(Network.socket.readyState == 2)
	{
		
		UI.showError("Tried to send packet after the connection was lost. This is most likely a bug.");
		
	}else if(Network.socket.readyState == 0)
	{
		
		UI.showError("Tried to send packet while the socket was still connecting. This is most likely a bug.");
		
	}
};

/**
 * Called when an ERROR packet is sent as a request
 * or as a response to a packet that does not listen for ERROR packets.
 * 
 * @param id The error code
 * @param msg A human readable message which might be supplied by the server
 */
Network.generalError = function(id, msg)
{
	//TODO: Add more detailed error descriptions
	switch(id)
	{
	case ERRORCODE.INTERNAL_EXCEPTION:
		UI.showError("The server reported an internal exception.");
		break;
		
	case ERRORCODE.SESSION_EXPIRED:
		UI.showError("Your session has expired. Reload the page for relog.");
		break;
		
	default:
		UI.showError("The server reported an error: " + msg);
	
	}
};

/**
 * Processes a packet that was not sent in response to another packet. 
 * ERROR-packets are handled by Network.generalError.
 * 
 * @param pk The packet object
 */
Network.request = function(pk)
{
	
	if(pk.typeID == PTYPE.DATA)
	{
		f_updateTiles(pk.data.category, pk.data.result);
		
	}else if(pk.typeID == PTYPE.LOGOUT)
	{
		f_serverSideLogout(pk.data.reasonCode);
		
		//We should tell the server we acknowledge the logout
		var resp = new Packet_Ack();
		Network.sendPacket(resp, pk.uid);
		
	}else
	{
		//This packet type was not recognized. Tell the server we did not understand his message
		
		var errorPacket = new Packet_Error(ERRORCODE.INVALID_PACKET, "The request was not recognized. You packet may be C->S only.");
		Network.sendPacket(errorPacket);
	}
	
};

//------screen constructors----------

function Dashboard()
{
	this.tiles = {}; //Map of tiles on the dashboard indexed with data unit identifier (category-ident)
	
	this.root = $("#tab-dashboard");
	
	//The jQuery width method seems not to work with a hidden object that defines only percentage dimensions.
	//As the dashboard has the width of the document with absolute margins, we can calculate the tile sizes using the documents width
	this.width = $(document).width() - 10;
	this.height = $(document).height() - 47;
	
	this.columnCount = 4; //Work with a fixed column count for now TODO: Select this value dynamically
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
	
	//Everything is created -> now we can add jQuery UI stuff
	$(".dashboard-column").sortable(
			{
				connectWith: ".dashboard-column",
				placeholder: "tile-placeholder",
				
				start: function(event, ui)
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
			});
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
	this.onTimeout = function(){UI.showError("The server took to long to respond to handshake.");};
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
	this.onTimeout = function(){UI.showError("The server took to long to respond to login.");};
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
	this.onTimeout = function(){UI.showError("The server took to long to respond to relog.");};
}

/**
 * 
 * @constructor
 */
function Packet_Query(category, ident)
{
	this.typeID = PTYPE.QUERY;

	this.data = {};
	this.data.category = category;
	this.data.ident = ident;
	
	this.timeout = GENERAL_TIMEOUT;
	this.timeoutID = null;
	this.allowedResponses = [PTYPE.DATA]; //Even though the protocol asks us to put ERROR here, there is no need to process it separately
	
	this.onResponse = function(){};
	this.onTimeout = function(){UI.showError("The server took to long to respond to query.");};
}

/**
 * 
 * @constructor
 */
function Packet_Logout(reason, msg)
{
	this.typeID = PTYPE.LOGOUT;
	
	this.data = {};
	this.data.reasonCode = reason || LOGOUTREASON.UNKNOWN;
	this.data.reasonMessage = msg || "No message specified";
	
	this.timeout = GENERAL_TIMEOUT;
	this.timeoutID = null;
	this.allowedResponses = [PTYPE.ACK];
	
	this.onResponse = function(){};
	this.onTimeout = function(){UI.showError("The server took to long to respond to logout.");};
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
