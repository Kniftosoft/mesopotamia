
/*--------------------------
 *      Tigris 0.0.4
 * 	Mesopotamia Client v1
 * (C) Niklas Weissner 2014
 *-------------------------- 
 */


//Configure this to your Euphrates installation
//If your endpoint is absolute, make sure you include the full URI (including protocol etc.)
var ENDPOINT_IS_RELATIVE = true;
var MESO_ENDPOINT = "MESOEND"; //Link to Euphrates


//Constants
var TIGRIS_VERSION = "0.0.4";
var TIGRIS_SESSION_COOKIE = "559-tigris-session";

//Packet type IDs
var PTYPE =
{
	HANDSHAKE: 	1,
	LOGIN: 		10,
	AUTH: 		11,
	RELOG: 		12,
	REAUTH: 	13,
	LOGOUT: 	14,
	QUERY: 		20,
	DATA: 		21,
	ACK: 		200,
	NACK: 		201,
	ERROR: 		242,
	QUIT:		255
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

//Reason codes
var REASONCODE =
{
	UNKNOWN:			0,
	CLOSED_BY_USER:		1,
	SESSION_EXPIRED:	2,
	INTERNAL_ERROR:		3,
	REFUSED:			4
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
	$('#loginform').submit(function(e) 
	{
	    e.preventDefault();
	    ui_i_login();
	});
	
	//Set up elements
	
	ui_showLoginPage(); //Initially show login page TODO: Put this in routine for Handshaking
}

function ui_showLoginPage()
{
	$("#login_errorbox").hide();
	$("#dashboard").hide();
	$("#header").hide();
	
	$("#login_dialog").show();
}

function ui_showDashboard()
{
	$("#header").show();
	$("#dashobard").show();
	
	$("#login_dialog").hide();
}


function ui_i_login()
{
	var username = $("#loginform_username").val();
	var passwordHashObject = CryptoJS.SHA256($("#loginform_password").val());
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
				ui_login_showBadLoginMessage();
			}
		};
		
	netio_sendPacket(packet);
}

function ui_login_showBadLoginMessage()
{
	$("#login_errorbox").html("Your login data is incorrect!");
	$("#login_errorbox").show();
	$("#login_errorbox").effect("shake");
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
	
	socket = new WebSocket(wsURI);
	
	socket.onopen = 
	function()
	{
		netio_onOpen();
	};
	
	socket.onmessage =
	function(msg)
	{
		netio_onMessage(msg);
	};
	
	socket.onclose =
	function()
	{
		netio_onClose();
	};
	
	socket.onerror =
	function()
	{
		netio_onError();
	};
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
		console.log("Message was not in JSON format or shit. \n" + e + "\n" + msg.data);
		
		return;
	}
	
	//TODO: Check for missing fields here
	
	if(("uid" + packet.uid) in sentPacketMap) //UID is registered -> Packet is an answer
	{
		
		var requestingPacket = sentPacketMap["uid" + packet.uid]; //Get the packet that requested this answer
		
		delete sentPacketMap["uid" + packet.uid];
		
		if($.inArray(packet.typeID , requestingPacket.allowedResponses) == -1) 
		{
			//This packet type is not allowed as a response to the requesting packet
			// -> send INVALID_ANSWER ERROR packet
			
			//TODO: Send packet
			console.error("Received invalid response packet: " + packet.typeID + " is not allowed as a response to " + requestingPacket.packetID);
			
		}else
		{
		
			requestingPacket.onResponse(packet);
			
		}
			
	}else // -> Packet is a request
	{
		//TODO:  Implement request processing
		console.log("I received a packet as a request.\n At the moment I don't know what to do with it so I throw it away.");
	}
}

function netio_onClose()
{
	console.log("The connection was closed");
}

function netio_onError()
{
	console.error("Network error");
}

function netio_handshake()
{

	var packet = new Packet_Handshake();
	packet.onResponse = 
	function(pk)
	{
		if(pk.typeID == PTYPE.ACK)
		{
			console.log("Handshake successful");
		}else if(pk.typeID == PTYPE.QUIT)
		{
			console.error("Handshake refused. Reason: " + pk.data.reasonMessage);
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
	
	packetToSend.typeID = packet.packetID;
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

//---------------Packet constructors------------------

/**
 *
 * @constructor
 */
function Packet_Handshake()
{
	this.packetID = PTYPE.HANDSHAKE;
	
	this.data = {};
	this.data.clientVersion = TIGRIS_VERSION;
	
	this.allowedResponses = [PTYPE.ACK, PTYPE.QUIT];
}

/**
 *
 * @constructor
 */
function Packet_Login(usr,pwrdHash)
{
	this.packetID = PTYPE.LOGIN;
	
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
	this.packetID = PTYPE.ERROR;
	
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
