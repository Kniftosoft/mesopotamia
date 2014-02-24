
/*--------------------------
 *      Tigris 0.1.0
 * 	Mesopotamia Client v1
 * (C) Niklas Weissner 2014
 *-------------------------- 
 */

//TODO: Implement better error handling all over the script
 

//Configure this to your Euphrates installation
//If your endpoint is absolute, make sure you include the full URI (including protocol etc.)
var ENDPOINT_IS_RELATIVE = true;
var MESO_ENDPOINT = "TIG_TEST_END"; //Link to Euphrates


//Constants
var TIGRIS_VERSION = "0.1.0";
var TIGRIS_SESSION_COOKIE = "559-tigris-session";

//Packet type IDs
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

//Error codes
var ERRORCODE =
{
	UNKNOWN:			0,
	INVALID_PACKET:		1,
	SESSION_EXPIRED:	2,
	INTERNAL_EXCEPTION:	3,
	INVALID_RESPONSE:	4
};

//Logout reason codes
var REASONCODE =
{
	UNKNOWN:			0,
	CLOSED_BY_USER:		1,
	SESSION_EXPIRED:	2,
	INTERNAL_ERROR:		3,
	REFUSED:			4
};


var connectionData =
{
	sessionOpen:	false
}; 


$(document).ready(
function()
{
	ui_init(); //Initialize UI
	
	netio_init(); //Init connection
});

//-----------------Functional stuff-------------------

function f_setUpSession(sessionID)
{
	//util_setCookie(TIGRIS_SESSION_COOKIE, sessionID, 600);
	
	ui_showDashboard();
	
	connectionData.sessionID = sessionID;
	connectionData.sessionOpen = true;
	
	var d = $("#dashboard");
	
	console.log("Computed dimensions of dashboard: " + d.width() + "/" + d.height());
}


//-------------------Utility stuff-------------------

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

//---------------------UI handler--------------------

/**
 * Initializes all GUI elements.
 */
function ui_init()
{
	//Set up events
	
	//Init login form events
	$("#loginform").submit(function(e) 
	{
	    e.preventDefault();
	    ui_i_login();
	});
	
	$("#sidebar_handle").click(function(e)
	{
		console.log("the sidebar handle was clicked");
		$("#sidebar").animate( { width: "toggle" }, 1000, "easeInOutExpo");
	});
	
	$("#sidebar").animate( { width: "hide" }, 1, "linear");
	
	//Initially show message indicating we are still connection TODO: change this to something different then an error message
	ui_showError("Connecting to the server..."); 
}

function ui_showLoginPage()
{
	$("#login").show();

	$("#login_errorbox").hide();
	$("#dashboard").hide();
	$("#header").hide();
	$("#error").hide();
}

function ui_showDashboard()
{
	$("#header").show();
	$("#dashboard").show();
	
	$("#login").hide();
	$("#error").hide();
}

function ui_showError(msg)
{
	var error = msg || "An unknown error occurred";

	$("#login").hide();
	$("#dashboard").hide();
	$("#header").hide();
	
	$("#error_msg").html(error);
	
	$("#error").show();
}

function ui_i_login()
{
	var username = $("#loginform_username").val();
	var password = $("#loginform_password").val() + connectionData.salt;
	var passwordHashObject = CryptoJS.SHA256(password);
	delete password; //For safety :)
	$("#loginform_password").val("");
	var passwordHash = passwordHashObject.toString(CryptoJS.enc.Hex);
	
	var packet = new Packet_Login(username, passwordHash);
	packet.onResponse = 
		function(pk)
		{
			if(pk.typeID == PTYPE.AUTH)
			{
				var sessionID = pk.data.sessionID;
				//Wait until full protocol specification before implementing this. Ignore it for now
				//var userConfig = data.userConfig;
				
				f_setUpSession(sessionID);
				
			}else if(pk.typeID == PTYPE.NACK)
			{
				ui_login_showError("Your login data is incorrect");
			}
		};
		
	netio_sendPacket(packet);
}

function ui_login_showError(msg, shk)
{
	var shake = shk || false;
	
	$("#login_errorbox").html(msg);
	$("#login_errorbox").show();
	
	if(shake)
	{
		$("#login_errorbox").effect("shake");
	}
}

//--------------Net handler-------------------------

var socket;
var sentPacketMap = new Array();

function netio_init()
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
	
	socket.onopen = netio_onOpen;
	
	socket.onmessage = netio_onMessage;
	
	socket.onclose = netio_onClose;
	
	socket.onerror = netio_onError;
}

function netio_onOpen()
{
	netio_handshake(); //Only start handshaking after connection has been established
}

function netio_onMessage(msg)
{
	console.log("Message: " + msg.data);
	
	var packet;

	try
	{
		packet = jQuery.parseJSON(msg.data);
		
	}catch(e)
	{	
		console.error("Message was not in JSON format or shit. \n" + e + "\n" + msg.data);
		
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
		netio_request(packet);
	}
}

function netio_onClose()
{
	console.log("The connection was closed");
}

function netio_onError(e)
{
	console.error("Network error");
	
	ui_showError("A network error occurred. Please contact the system admin you do not know.");
}

function netio_handshake()
{

	var packet = new Packet_Handshake();
	packet.onResponse = 
	function(pk)
	{
		if(pk.typeID == PTYPE.ACCEPT)
		{
			console.log("Handshake successful");
			
			connectionData.salt = pk.data.salt;
			
			ui_showLoginPage();
			
		}else if(pk.typeID == PTYPE.NACK)
		{
			console.error("Handshake refused");
			
			ui_showError("Connection refused by server");
		}
	};
	
	netio_sendPacket(packet);
}

function netio_generateUID()
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

function netio_sendPacket(packet, uid)
{	
	var packetToSend = {};
	
	packetToSend.typeID = packet.typeID;
	packetToSend.data = packet.data;
	packetToSend.uid = uid || netio_generateUID();
	
	var jsonPacket = JSON.stringify(packetToSend);
	
	if(socket.readyState == 1)
	{
		socket.send(jsonPacket);
		sentPacketMap["uid" + packetToSend.uid] = packet;
		
		console.log("I sent this: " + jsonPacket);
		
	}else if(socket.readyState == 2)
	{
		console.error("The connection was lost");
	}else if(socket.readyState == 0)
	{
		console.error("The socket is still connecting");
	}
	
	
}


function netio_request(packet)
{
	if(packet.typeID == PTYPE.LOGOUT)
	{
		ui_showLoginPage();
		ui_login_showError("Session closed by server.", false);
		
		return;
	}
}

//---------------Packet constructors------------------

/**
 *
 * @constructor
 */
function Packet_Handshake()
{
	this.typeID = PTYPE.HANDSHAKE;
	
	this.data = {};
	this.data.clientVersion = TIGRIS_VERSION;
	
	this.allowedResponses = [PTYPE.ACCEPT, PTYPE.NACK];
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
}


var UI_STRINGS = {};

UI_STRINGS.MSG_FATAL_NETWORK_ERROR = 		"A fatal network error occurred!";
UI_STRINGS.MSG_FATAL_NETWORK_ERROR_TITLE = 	"Fatal network error";

UI_STRINGS.MSG_COM_ERROR_BAD_PACKET = 		"The server sent a message that could not be parsed into a valid packet.";
UI_STRINGS.MSG_COM_ERROR_BAD_PACKET_TITLE = "Communication error";
