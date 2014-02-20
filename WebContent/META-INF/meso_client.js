
/*
 *      Tigris 0.0.1
 * 	Mesopotamia Client v1
 * (C) Niklas Weissner 2014
 */


//Configure this to your Euphrates installation
//If your endpoint is absolute, make sure you include the full URI (including protocol etc.)
var ENDPOINT_IS_RELATIVE = true;
var MESO_ENDPOINT = "/MesoEndpoint"; //Link to Euphrates


//Constants
var TIGRIS_VERSION = "0.0.1";


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
	
	//netio_init(); //Init connection
	
});

//---------------------UI handler--------------------

/**
 * Initializes all GUI elements.
 */
function ui_init()
{
	
}

function ui_displayErrorDialog(title,message)
{

	
}

//--------------Net handler-------------------------

var socket;
var sentPacketMap = {};

function netio_init()
{
	var wsURI;
	
	if(ENPOINT_IS_RELATIVE)
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
	
	socket.onOpen = 
	function()
	{
		netio_onOpen();
	};
	
	socket.onMessage =
	function(msg)
	{
		netio_onMessage(msg);
	};
	
	socket.onClose =
	function()
	{
		netio_onClose();
	};
	
	socket.onError =
	function()
	{
		netio_onError();
	};
}

function netio_onOpen()
{
	
}

function netio_onMessage(msg)
{
	var packet;

	try
	{
		packet = jQuery.parseJSON(msg);
		
	}catch(e)
	{	
		ui_showErrorDialog("PANIC","Message was not in JSON format or shit");
		
		return;
	}
	
	//TODO: Check for missing fields here
	
	if(packet.uid in sentPacketMap) //UID is registered -> Packet is an answer
	{
		
		var requestPacket = sentPacketMap[packet.uid]; //Get the packet that requested this answer
		
		if(!(packet.typeID in requestPacket.allowedResponses)) 
		{
			//This packet type is not allowed as a response to the requesting packet
			// -> send INVALID_ANSWER ERROR packet
			
			//TODO: Send packet
		}
		
		if(requestPacket.requestOnly)
		{
			//This packet type is not allowed as a response in general
			// -> send INVALID_PACKET ERROR packet
			
			//TODO: Send packet
		}
		
		requestingPacket.onResponse(packet.typeID, packet.data);
		
		
		
	}else // -> Packet is a request
	{
	
	}
}

function netio_onClose()
{
	
}

function netio_onError()
{
	ui_showErrorDialog(UI_STRINGS.MSG_FATAL_NETWORK_ERROR_TITLE,UI_STRINGS.MSG_FATAL_NETWORK_ERROR);
}

function netio_sendPacket(packet)
{
	var packetToSend;
	
	packetToSend.typeID = packet.packetID;
	packetToSend.data = packet.data;
	
	var jsonPacket = JSON.stringify(packetToSend);
	
	socket.send(jsonPacket);
}

//---------------Packet constructors------------------

/**
 *
 * @constructor
 */
function Packet_Handshake()
{
	this.packetID = PTYPE.HANDSHAKE;
	
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
	
	this.data.errorCode = code;
	this.data.errorMessage = message;
	
	this.allowedResponses = [];
}


var UI_STRINGS = {};

UI_STRINGS.MSG_FATAL_NETWORK_ERROR = 		"A fatal network error occurred!";
UI_STRINGS.MSG_FATAL_NETWORK_ERROR_TITLE = 	"Fatal network error";

UI_STRINGS.MSG_COM_ERROR_BAD_PACKET = 		"The server sent a message that could not be parsed into a valid packet.";
UI_STRINGS.MSG_COM_ERROR_BAD_PACKET_TITLE = "Communication error";
