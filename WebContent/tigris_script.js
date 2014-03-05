
/*--------------------------
 *      Tigris 0.2.1
 * 	Mesopotamia Client v1
 * (C) Niklas Weissner 2014
 *-------------------------- 
 */

//TODO: Implement better error handling all over the script
//TODO: Sort some code fragment so the source can be easier understood
//TODO: Comment on stuff
//TODO: Localization maybe?

//Configure this to your Euphrates installation
//If your endpoint is absolute, make sure you include the full URI (including protocol etc.)
var ENDPOINT_IS_RELATIVE = false;
var MESO_ENDPOINT = "ws://localhost:8080/mesopotamia/TIG_TEST_END"; //Link to Euphrates


//Constants
var TIGRIS_VERSION = "0.2.1";
var TIGRIS_SESSION_COOKIE = "2324-tigris-session";


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


var connectionData = 
{
		salt: null,
		
		sessionID: null
};

var socket;
var sentPacketMap = {};

var currentTab = null; //The data screen tab currently displayed TODO: Make this a bit fancier

$(document).ready(
function()
{
	
	ui_init();
	
	n_init();
});


//-------------UI stuff---------------

function ui_init()
{

	ui_showStatus("Connecting to the server..."); //Initially show messeage on connection status
	
	//Set up events
	$("#loginform").submit(function(e) 
	{
		e.preventDefault();
		
		f_tryLogin();
	});
	
	$(".sidebar-item").click(function(e)
			{
				ui_sidebarItemClicked(e.target);
			});
	
	ui_setUpDashboard(); //Set up dashboard TODO: Review if this is really needed (currently only for testing)
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
	
	$("#data-frame > .data-frame-tab").hide(); //Initially hide all data tabs...
	ui_data_showTab("dashboard"); //and show dashboard
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

	$(".dashboard-column").sortable(
			{
				connectWith: ".dashboard-column",
				handle: ".dashboard-tile-header",
				placeholder: "dashboard-tile-placeholder"
			});
	
	$(".data-frame-sub").hide(); //Hide all tabs
	ui_data_showTab("dashboard"); //Initially show dashboard
	
	//Let's create some example tiles	
	var tile0 = new Tile(0);
	tile0.setTitle("Have some good chip music");
	tile0.content.html("<audio controls> <source src='http://ftp.df.lth.se/pub/media/soasc/soasc_mp3/MUSICIANS/T/Trident/A_Short_One_T01.sid_CSG8580R5.mp3' type='audio/mpeg'></audio>" +
			"<br>Adam Dunkels - A Short One");
	$("#dashboard-col1").append(tile0.base);
	
	var tile1 = new Tile(1);
	tile1.setTitle("These tiles are sortable");
	tile1.content.html("The dashboard is split into four columns (WordPress-like).<br>" +
			"You can move the tiles between the columns by dragging it by the header.<br>" +
			"The columns are stacked top-down (This is the best thing I could do without fucking up the portability).");
	$("#dashboard-col0").append(tile1.base);
	
	var tile2 = new Tile(2);
	tile2.setTitle("Content coming soon");
	tile2.content.html("These tiles are going to conatin statistics and stuff and will be creatable<br>" +
			"by dragging items of the tableview to the dashboard.");
	$("#dashboard-col0").append(tile2.base);
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
	delete passwordHashObject; //Delete unneeded vars containing sensible data. For safety. God, I think someone is watching me!
	delete saltedPasswordHash;
	
	var packet = new Packet_Login(username, finalPasswordHash);
	packet.onResponse = 
		function(pk)
		{
			if(pk.typeID == PTYPE.AUTH)
			{
				var sessionID = pk.data.sessionID;
				
				f_setUpSession(sessionID);
				
			}else if(pk.typeID == PTYPE.NACK)
			{
				ui_login_showError("Your login data is incorrect"); //MSG_LOGIN_BADLOGINDATA
			}
		};
		
	n_sendPacket(packet);
}

function f_tryRelog()
{
	var cook = util_getCookie(TIGRIS_SESSION_COOKIE);
	
	if(cook != null)
	{
		//There was a session ID stored -> ask server if it is still valid
		
		var pk = new Packet_Relog(cook);
		pk.onResponse = 
			function(pk)
			{
				if(pk.typeID == PTYPE.REAUTH)
				{
					//The old session ID was still valid -> use echoed (maybe changed) session ID
					connectionData.sessionID = pk.data.sessionID;
					
					//Store the new session ID to cookie TODO: maybe integrate this into f_setUpSession()
					util_setCookie(TIGRIS_SESSION_COOKIE, pk.data.sessionID);
					
					//As the session was still valid, we can directly display the data view
					ui_showScreen("data");
					
				}else if(pk.typeID == PTYPE.NACK)
				{
					//The old session ID has expired -> user has to login again
					ui_showScreen("login");
				}
			};
			
		n_sendPacket(pk);
		
	}else
	{
		//No stored session -> user has to login
		
		ui_showScreen("login");
	}
}

function f_setUpSession(sessionID)
{
	connectionData.sessionID = sessionID;
	
	util_setCookie(TIGRIS_SESSION_COOKIE,sessionID);
	
	ui_showDataScreen();
}

//-------------Network stuff------------------

function n_init()
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
		socket = new WebSocket(wsURI);
	}catch(e)
	{
		ui_showError("Error while opening connection:<br><b>" + e + "</b>");
		return;
	}
	
	socket.onopen = n_ws_onOpen;
	
	socket.onmessage = n_ws_onMessage;
	
	socket.onclose = n_ws_onClose;
	
	socket.onerror = n_ws_onError;
}

function n_ws_onOpen()
{
	n_handshake(); //Only start handshaking after connection has been established
}

function n_ws_onMessage(msg)
{
	console.log("I received this: " + msg.data);
	
	var packet;

	try
	{
		packet = jQuery.parseJSON(msg.data);
		
	}catch(e)
	{	
		console.error("Message was not in JSON format or shit. \n" + e);
		
		return;
	}
	
	if(packet.uid == "undefined" || packet.typeID == "undefined" || packet.data == "undefined")
	{
		console.error("Packet had missing fields.");
		
		return;
	}
	
	if(("uid" + packet.uid) in sentPacketMap) //UID is registered -> Packet is an answer
	{
		
		var requestingPacket = sentPacketMap["uid" + packet.uid]; //Get the packet that requested this answer
		
		delete sentPacketMap["uid" + packet.uid];
		
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
		n_request(packet);
	}
}

function n_ws_onClose()
{
	console.log("The connection was closed");
}

function n_ws_onError(e)
{
	console.error("Network error");
	
	ui_showError("A network error occurred. Please contact the system admin you do not know."); //ERR_COMM_NETWORK
}

function n_handshake()
{

	var packet = new Packet_Handshake();
	packet.onResponse = 
	function(pk)
	{
		if(pk.typeID == PTYPE.ACCEPT)
		{
			console.log("Handshake successful");
			
			connectionData.salt = pk.data.salt;
			
			//Connection to server is OK -> The user may log in
			f_tryRelog(); //First look if there is already a session to be restored.
			
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
	
	n_sendPacket(packet);
}

function n_generateUID()
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
}

function n_sendPacket(packet, uid)
{
	var packetToSend = {};
	
	packetToSend.typeID = packet.typeID;
	packetToSend.data = packet.data;
	packetToSend.uid = uid || n_generateUID();
	
	var jsonPacket = JSON.stringify(packetToSend);
	
	if(socket.readyState == 1)
	{
		socket.send(jsonPacket);
		sentPacketMap["uid" + packetToSend.uid] = packet;
		
		console.log("I sent this: " + jsonPacket);
		
	}else if(socket.readyState == 2)
	{
		console.error("Tried to send after the connection was lost");
	}else if(socket.readyState == 0)
	{
		console.error("Tried to send while the socket was still connecting");
	}
}

function n_request()
{
	
}


//-------tile constructors------------

function Tile(id)
{
	
	this.base = $("<div>").addClass("dashboard-tile");
	this.base.attr("id","tile"+id);
	
	this.header = $("<div>").addClass("dashboard-tile-header");
	this.base.append(this.header);
	
	this.content = $("<div>").addClass("dashboard-tile-content");
	this.base.append(this.content);
	
	this.setTitle = 
		function(title)
		{
			this.header.html(title);
		};
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
	
	this.allowedResponses = [PTYPE.ACCEPT, PTYPE.ERROR];
	
	this.onResponse = function(){};
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
	
	this.allowedResponses = [PTYPE.AUTH, PTYPE.NACK];
	
	this.onResponse = function(){};
}

function Packet_Relog(sessionID)
{
	this.typeID = PTYPE.RELOG;

	this.data = {};
	this.data.sessionID = sessionID;
	
	this.allowedResponses = [PTYPE.REAUTH, PTYPE.NACK];
	
	this.onResponse = function(){};
}

/**
* @constructor
*/
function Packet_Error(code, message)
{
	this.typeID = PTYPE.ERROR;
	
	this.data = {};
	this.data.errorCode = code;
	this.data.errorMessage = message;
	
	this.allowedResponses = [];
	
	this.onResponse = function(){};
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
