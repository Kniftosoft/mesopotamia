
/*--------------------------
 *      Tigris 0.2.0
 * 	Mesopotamia Client v1
 * (C) Niklas Weissner 2014
 *-------------------------- 
 */

//TODO: Implement better error handling all over the script
 

//Configure this to your Euphrates installation
//If your endpoint is absolute, make sure you include the full URI (including protocol etc.)
var ENDPOINT_IS_RELATIVE = false;
var MESO_ENDPOINT = "ws://localhost:8080/mesopotamia/TIG_TEST_END"; //Link to Euphrates


//Constants
var TIGRIS_VERSION = "0.2.0";
var TIGRIS_SESSION_COOKIE = "559-tigris-session";


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


var connectionData = {};

var socket;
var sentPacketMap = {};


$(document).ready(
function()
{
	
	ui_init();
	
	n_init();
	
});


//-------------UI stuff---------------

function ui_init()
{

	$("#dashboard-screen").hide();
	
	$("#login-screen").show();
	
	
	$("#loginform").submit(function(e) 
	{
		e.preventDefault();
		
		i_tryLogin();
	});
	
	$(".sidebar-item").click(function(e)
			{
		
				$(".sidebar-item").each(function(index)
						{
							$(this).removeClass("sidebar-item-active", 350);
						});
				
				$(e.target).addClass("sidebar-item-active", 350);
		
			});
}

function ui_showDashboard()
{
	//Play nice fading animation
	$("#login-screen").hide("puff", {}, 600, 
			function()
			{ 
				$("#dashboard-screen").fadeIn();
			});
}

function ui_showError(msg)
{

	alert("Put this in real error msg: " + msg);
	
}

function ui_login_showError(msg)
{
	$("#loginerror").html(msg);
	
	$("#loginerror").show();
	
	$("#loginerror").effect("shake");
}

//-------------Interaction stuff------------

function i_tryLogin()
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
				//var sessionID = pk.data.sessionID;
				
				ui_showDashboard();
				
			}else if(pk.typeID == PTYPE.NACK)
			{
				ui_login_showError("Your login data is incorrect"); //MSG_LOGIN_BADLOGINDATA
			}
		};
		
	n_sendPacket(packet);
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
			
			//TODO: Let handshake display login page
			//ui_showLoginPage();
			
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
