
/*--------------------------
 *      Tigris 0.3.3
 * 	Mesopotamia Client v1
 * (C) Niklas Weissner 2014
 *-------------------------- 
 */

//TODO: Sort some code fragments so the source can be easier understood
//TODO: Comment on stuff
//TODO: Localization maybe?
//TODO: Maybe check if a packet is missing fields it should have according to its type
//TODO: Add polish to data fetching functions

//Configure this to your Euphrates installation
//If your endpoint is absolute, make sure you include the full URI (including protocol etc.)
var ENDPOINT_IS_RELATIVE = true;
var MESO_ENDPOINT = "TIG_TEST_END"; //Link to Euphrates


//Constants
var TIGRIS_VERSION = "0.3.3";
var TIGRIS_SESSION_COOKIE = "2324-tigris-session";

var GENERAL_TIMEOUT = 2000; //Default timeout in milliseconds (may be overridden by some packets)

var PTYPE =
{
	NULL:		0,
	HANDSHAKE: 	1,
	ACCEPT:		2,
	LOGIN: 		10,
	AUTH: 		11,
	RELOG: 		12,
	REAUTH: 	13,
	LOGOUT: 	14,
	QUERY: 		20,
	DATA: 		21,
	SUBSCRIBE:	22,
	UNSUBSCRIBE:23,
	CONFIG:		50,
	ACK: 		200,
	NACK: 		201,
	ERROR: 		242
};

var DCAT =
{
	MACHINE:	1,
	JOB:		2,
	
	PRODUCT:	11,
	
	CONFIG:		20,
	
	nameById: function(id)
	{
		var name = null;
		
		jQuery.each(DCAT, function(index,element){ if(element == id){ name = index;}});
		
		return name;
	},
	
	byName: function(name)
	{
		return DCAT[name.toUpperCase()];
	}
};


var ERRORCODE =
{
	UNKNOWN:			0,
	INVALID_PACKET:		1,
	SESSION_EXPIRED:	2,
	INTERNAL_EXCEPTION:	3,
	INVALID_RESPONSE:	4,
	WRONG_VERSION:		5,
	NOT_ALLOWED:		6,
	BAD_QUERY:			7,
	BAD_PACKET:			8
};

var LOGOUTREASON =
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
	
	UI.init();
	
	Network.init();
	
});


//-------------UI stuff---------------

/**
 * Namespace for user interface related functions
 */
var UI = {};

UI.currentScreen = "status"; //Status message for noscript is displayed by default through pure html
UI.currentTab = null;

UI.dashboard = null;
UI.sidebarEnabled = true;

UI.productTypes = {};

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
	
	$("#sidebar-handle").mouseover(function(e)
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
	
	UI.tileCreator = $("#tilecreator");
	
	//Create tables
	//Status values decoded for testing TODO: Localize this
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
	
	$("#table-machine tbody").sortable(
			{
		
				distance: 10,
				
				connectWith: "#tilecreator",
				
				start: UI.table_dragStart,
				stop: UI.table_dragStop,
				
				helper: "clone"
		
			});
	
	UI.jobTable = $("#table-job").dataTable(
			{
				"aoColumns": [
		              { "mData": "id" },
                      { "mData": "target" },
                      { "mData": function(data, type, val) { return util_formatDate(new Date(data.startTime));} },
                      { "mData": function(data, type, val) { return UI.productTypes[data.productType];} }
                ]
			});
	
	$("#table-job tbody").sortable(
			{
		
				distance: 10,
				
				connectWith: "#tilecreator",
				
				start: UI.table_dragStart,
				stop: UI.table_dragStop,
				
				helper: "clone"
		
			});
	
	UI.dashboard = new Dashboard();
	
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
 * Displays data screen with fading animation.
 */
UI.showData = function()
{
	//Play nice fading animation
	$("#screen-" + UI.currentScreen).hide("puff", {}, 600, 
			function()
			{ 
				$("#screen-data").fadeIn();
			});
	
	UI.currentScreen = "data";
	
	//Set up user information
	$("#username").html(Session.username);
	
	
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
	
	if(UI.sidebarEnabled)
	{
		$("#sidebar-handle").fadeOut(100,
				function()
				{
					$("#sidebar").show("slide",300);
				});
	}
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
		
		//Update target tab, then show it
		UI.data_updateTab(target);
		
		UI.data_showTab(target);
	}
};


UI.login_showError = function(msg)
{
	$("#loginerror").html(msg);
	
	$("#loginerror").show();
	
	$("#loginerror").effect("shake");
};


UI.data_showTab = function(name, callback)
{
	if(UI.currentTab != null)
	{
		$("#tab-" + UI.currentTab).fadeOut(function()
				{
					$("#tab-" + name).fadeIn();
					
					if(callback)
					{
						callback();
					}
					
				});
		
	}else
	{
		$("#tab-" + name).fadeIn();
	}
	
	UI.currentTab = name;
};

/**
 * Displays tab and activates correct sidebar button.
 */
UI.data_switchTab = function(name, callback)
{
	//Deactivate all sidebar buttons
	$(".sidebar-item-active").each(function(index)
			{
				$(this).removeClass("sidebar-item-active");
			});
	
	//Activate right item (do it using $.each, an attribute selector takes forever to parse) 
	$(".sidebar-item").each(function(index)
			{
				var $this = $(this);
				
				if($this.attr("data-target") == name)
				{
					$this.addClass("sidebar-item-active");
				}
			});
	
	UI.data_showTab(name, callback);
};

/**
 * Updates the currently displayed data tab.
 */
UI.dataUpdate = function()
{
	if(UI.currentScreen == "data")
	{
		UI.data_updateTab(UI.currentTab);
	}
};

/**
 * Updates a data tab.
 * 
 * @param target The name of the target data tab
 */
UI.data_updateTab = function(target)
{	
	var table = null;
	var category = 0;
	
	if(target == "machine")
	{
		table = UI.machineTable;
		category = DCAT.MACHINE;
	}else if(target == "job")
	{
		table = UI.jobTable;
		category = DCAT.JOB;
	}else
	{
		//Target not supported
		return;
	}
	
	var pkq = new Packet_Query(category, "*");
	pkq.onResponse = function(pk)
	{
		table.fnClearTable();
		
		if(pk.typeID == PTYPE.DATA)
		{
			table.fnAddData(pk.data.result);
			DataPool.updateByData(target, pk.data.result);
		}
	};
	Network.sendPacket(pkq);
	
};

UI.table_dragStart = function(event, ui)
{
	$("#tilecreator").show("slide");
	
	UI.sidebarEnabled = false;
};

UI.table_dragStop = function(event, ui)
{
	$("#tilecreator").hide("slide");
	
	UI.sidebarEnabled = true;
	
	//The user might have reordered the table and thus messed up the coloring, so better reload data
	//(This would not be neccesary if we could use something different than sortable for draggable table rows;
	//thanks to you, jQuery UI)
	UI.dataUpdate();
	
	//Since hover detection does not work with a sortable on the cursor, we have to check manually by position
	if(util_containsPoint(UI.tileCreator,event.pageX,event.pageY))
	{
		//The user dropped the row in the tile creator
		
		//Get the id from the dropped element (It must be the first column in order for this to work)
		var id = ui.item.children("td").first().text();
		
		var dataUnit = DataPool.getSingle(UI.currentTab,id);
		
		f_createTile(UI.currentTab,dataUnit);
		
		
	}
};

/*UI.table_createColHelper = function()
{
	var row = $(this);
	
	var helper = $("<div>");
	helper.addClass("tile-creator-helper");
	helper.css("width","120px");
	
	return helper;
};*/

//-------------Functional stuff------------

function f_tryLogin()
{
	var username = $("#loginform_username").val();
	var remember = $("#loginform_remember").is(":checked");
	
	//Encrypt password in accordance with specification of MCP 1.2.1
	var passwordHashObject = CryptoJS.SHA256($("#loginform_password").val());
	$("#loginform_password").val("");
	var saltedPasswordHash = passwordHashObject.toString(CryptoJS.enc.Hex) + Session.salt; //TODO: replace hex encoder with self written one. plain ridicolous to use a library for that
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
				Session.sessionID = pk.data.sessionID;
				
				Session.username = username;
				
				//Store remember state for this session
				Session.remember = remember;
				if(remember) //Store session ID in cookie if session is to be remembered
				{
					util_setCookie(TIGRIS_SESSION_COOKIE, Session.sessionID);
				}
				
				//As the user is now logged in, we can load his configuration stored on the server
				f_loadServerSideConfig(); //This method displays data screen when done
				
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
					Session.sessionID = pk.data.sessionID;
					
					//Store the new session ID to cookie TODO: maybe integrate this into f_setUpSession()
					//Since this cookie was created in a session the user wanted to keep, we can assume the user wants still to keep it
					util_setCookie(TIGRIS_SESSION_COOKIE, pk.data.sessionID);
					
					//As there was no login, the server has to tell us who we are
					Session.username = pk.data.username;
					$("#username").html(Session.username); //TODO: Put this somewhere it belongs
					
					//As the session was still valid, we can directly display the data view (after loading configs)
					f_loadServerSideConfig(); //This method automatically displays data view after everything was loaded up
					
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
				Session.clear();
				
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
	//TODO: Put this to central location
	var msgs = {};
	msgs[LOGOUTREASON.UNKNOWN] = "of an unknown reason.";
	msgs[LOGOUTREASON.SESSION_EXPIRED] = "your session lost its validity.";
	msgs[LOGOUTREASON.INTERNAL_ERROR] = "an internal server error occurred.";
	msgs[LOGOUTREASON.REFUSED] = "you were somehow suddenly refused.";
	msgs[LOGOUTREASON.CLOSED_BY_USER] = "it says the user did it. Alright, who was it then?";
	
	Session.clear();
	
	UI.showLogin();
	UI.login_showError("The server logged you out because " + msgs[reason]);
}

function f_loadServerSideConfig()
{
	//Load a list of product types so IDs can be resolved to names
	var queryProducts = new Packet_Query(DCAT.PRODUCT,"*");
	queryProducts.onResponse = function(pk)
	{
		if(pk.typeID == PTYPE.DATA)
		{
			jQuery.each(pk.data.result, 
					function(index, value)
					{
						UI.productTypes[value.id] = value.name;
					});
			
			UI.showData(); //Everything is set up -> Show data screen
		}
	};
	
	
	//Fetch config data from server
	var queryConfig = new Packet_Query(DCAT.CONFIG,"*");
	queryConfig.onResponse = function(pk)
	{
		if(pk.typeID == PTYPE.DATA)
		{
			//Store all of the returned config values
			jQuery.each(pk.data.result, 
					function(index, value)
					{
						Session.config[value.id] = value.name;
					});
			
			//When we are finished loading config, we may continue with the product names (ensures everything is done in order)
			Network.sendPacket(queryProducts);
		}
	};
	
	//First load config, then load product names
	Network.sendPacket(queryConfig); 
}

/**
 * Subscribes the data unit and creates a linked tile on the dashboard
 * when the subscriptions succeeds. 
 * 
 * @param category The category of data unit
 * @param dataUnit The data unit object
 * @returns {any}
 */
function f_createTile(category, dataUnit)
{
	
	var subPacket = new Packet_Subscribe(DCAT.byName(category),dataUnit.id);
	subPacket.onResponse = function(pk)
	{
		if(pk.typeID == PTYPE.ACK)
		{
			var tile = null;
			
			if(category == "machine")
			{
				tile = new Tile_Machine(dataUnit);
				
			}else if(category == "job")
			{
				tile = new Tile_Job(dataUnit);
			}
			
			UI.dashboard.addTile(tile);
			
		}else if(pk.typeID == PTYPE.ERROR)
		{
			UI.showError("Subscribing a data unit has failed.");
		}
	};
	
	Network.sendPacket(subPacket);
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
					
					dashboard.tiles[dataUnitIdent].update(item);
				}
				
			});
}

/**
 * Object for storage of session related data.
 */
var Session = {};

Session.salt = null;
Session.sessionID = null;
Session.config = {};
Session.username = "";
Session.remember = false;

/**
 * Removes any session related data to begin a new session.
 */
Session.clear = function()
{
	Session.sessionID = null;
	Session.username = "";
	
	util_setCookie(TIGRIS_SESSION_COOKIE, "", -9999);
};


/**
 * Namespace for storing, receiving and managing data.
 */
var DataPool = {};

DataPool.pools = {};

/**
 * Returns single data unit of one category, identified by id.
 */
DataPool.getSingle = function(category, id)
{
	if(DataPool.isPoolDirty(category))
	{
		DataPool.refreshPool(category);
	}
	
	//If there is no pool yet, we can not return anything until the refresh is done
	if(!(category in DataPool.pools))
	{
		return null;
	}
	
	var pool = DataPool.pools[category];
	
	return pool[id];
};

/**
 * Returns all stored data units of one category.
 * 
 * @returns {Array}
 */
DataPool.getWhole = function(category)
{
	//Is data pool outdated or non-existent yet? Refresh it! (For later, as this happens asynchronous)
	if(DataPool.isPoolDirty(category))
	{
		DataPool.refreshPool(category);
	}
	
	//If there is no pool yet, we can not return anything until the refresh is done (which will be later, so return null)
	if(!(category in DataPool.pools))
	{
		return null;
	}
	
	var pool = DataPool.pools[category];

	//The pool is indexed with ids, we want an int-indexed array
	var dataUnitArray = new Array();
	var i = 0;
	jQuery.each(pool,function(index, element)
			{
				dataUnitArray[i] = element;
			});
	
	return dataUnitArray;
	
};

/**
 * Reports if a data pool is outdated or non-existent.
 * 
 * @param category The data pool category
 * @return {boolean}
 */
DataPool.isPoolDirty = function(category)
{
	
	if(category in DataPool.pools)
	{
		return true;
	}
	
	return true; //Update always for now TODO: Change
};

/**
 * Queries the whole data pool and stores it upon receipt.
 */
DataPool.refreshPool = function(category)
{
	var query = new Packet_Query(DCAT.byName(category), "*");
	query.onResponse = function(pk)
	{
		
		if(pk.typeID == PTYPE.DATA)
		{
			var pool = {};
			
			//Create an id-indexed array
			jQuery.each(pk.data.result, function(index, element)
					{
						pool[element.id] = element;
					});
			
			DataPool.pools[category] = pool;
		}
		
	};
	
	Network.sendPacket(query);
};

/**
 * Stores group of data units to the pool.
 */
DataPool.updateByData = function(category,data)
{
	//Create pool if it does not exist yet
	if(!(category in DataPool.pools))
	{
		DataPool.pools[category] = {};
	}
	
	var pool = DataPool.pools[category];
	
	jQuery.each(data, function(index, element)
			{
				pool[element.id] = element;
			});
};


/**
 * Namespace for network related functions
 */
var Network = {};

Network.socket = null;
Network.sentPacketMap = {};

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
		//Although this error could mean something is wrong with the server, we should send an ERROR packet
		Network.serverError(ERRORCODE.BAD_PACKET, "Could not parse packet: An exception occured while parsing JSON.");
		
		UI.showError("A fatal communication error occurred: A message could not be parsed into a packet.");
		
		return;
	}
	
	if(packet.uid === "undefined" || packet.typeID === "undefined" || packet.data === "undefined")
	{
		Network.serverError(ERRORCODE.BAD_PACKET, "Packet did not contain one or more of the fields: typeID, uid, data");
		
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
				Network.serverError(ERRORCODE.INVALID_RESPONSE, errorMsg);
				
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

/**
 * Reports an error to the server using an ERROR packet.
 * 
 * @param code The error code as specified by MCP 1.1
 * @param msg An optional error message
 */
Network.serverError = function(code, msg)
{
	var pk = new Packet_Error(code, msg);
	Network.sendPacket(pk);
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
			
			Session.salt = pk.data.salt;
			
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
	
	while(("uid" + uid) in Network.sentPacketMap)
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
	if(pk.typeID == PTYPE.NULL)
	{
		//We acknowledge the NULL packet but don't do anything with it as asked by MCP 1.3.4
		
	}else if(pk.typeID == PTYPE.DATA)
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
		
		Network.serverError(ERRORCODE.INVALID_PACKET, "The request was not recognized. You packet may be C->S only.");
	}
	
};

//------screen constructors----------

/**
 * @constructor
 */
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
				//Tile linking to the same data source is already on the dashboard -> refuse adding TODO: Display message
				return;
			}
		
			//Switch over to dashboard, when finished -> add tile
			UI.data_switchTab("dashboard",function()
					{
						//Unfortunetaly, 'this' does not work here
						UI.dashboard.tiles[dataUnitIdent] = tile;
					
						UI.dashboard.columns[0].append(tile.base); //For testing: append tile 0 to dashboard TODO: Add parameter for desired column
						
						tile.onCreate();
					});
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
						
						$(".tile-placeholder").css("height", UI.dashboard.tileWidth); //Height is always the same
						$(".tile-placeholder").css("width", ui.item.hasClass("tile-half") ? UI.dashboard.tileWidth : UI.dashboard.tileWidth*2);
						
					}
					
				},
			
				handle: "h2",
				
				revert: 200
			});
}

//-------tile constructors------------

/**
 * @constructor
 */
function Tile_Machine(dataUnit)
{
	var instance = this; //We need to store this object, as 'this' is not usable in callbacks
	var initialDataUnit = dataUnit;
	
	this.ident = "machine-" + dataUnit.id;
	this.dataUnitCategory = "machine";
	this.dataUnitID = dataUnit.id;
	
	
	//Retrieve and set up HTML structure of tile from a hidden definition area in the document
	this.base = $("#tiledef_machine").clone();
	this.base.attr("id","tile_" + this.ident);
	this.base.css("width",UI.dashboard.tileWidth);
	this.base.css("height",UI.dashboard.tileWidth);
	
	this.title = this.base.children("h2").first();
	this.sizeButton = this.base.children(".size-button").first();
	
	
	this.leftContent = this.base.children(".left-content").first();
	this.leftContent.css("width",UI.dashboard.tileWidth);
	this.leftContent.css("height",UI.dashboard.tileWidth - 13); //Minus 13 pixel header height
	this.leftContent.css("left",UI.dashboard.tileWidth);
	
	this.rightContent = this.base.children(".right-content").first();
	this.rightContent.css("width",UI.dashboard.tileWidth);
	this.rightContent.css("height",UI.dashboard.tileWidth - 13); //Minus 13 pixel header height
	this.rightContent.css("left",UI.dashboard.tileWidth);
	
	this.repairIcon = this.leftContent.children(".repair-icon").first();
	this.repairIcon.css("width",UI.dashboard.tileWidth * 0.8);
	this.repairIcon.css("height",UI.dashboard.tileWidth * 0.8);
	this.repairIcon.hide();
	
	this.cleaningIcon = this.leftContent.children(".cleaning-icon").first();
	this.cleaningIcon.css("width",UI.dashboard.tileWidth * 0.8);
	this.cleaningIcon.css("height",UI.dashboard.tileWidth * 0.8);
	this.cleaningIcon.hide();
	
	this.gaugeBase = this.leftContent.children(".machine-speed-gauge").first();
	this.gaugeBase.css("width",UI.dashboard.tileWidth * 0.8);
	this.gaugeBase.css("height",UI.dashboard.tileWidth * 0.8);
	this.gaugeBase.attr("id","tile_" + this.ident + "_gauge");
	
	this.gauge = null;
	
	//Called immediately after tile was appended to dashboard
	this.onCreate = function()
	{
		//Create gauge
		instance.gauge = new GaugeSVG(
		{
			
			id: "tile_" + this.ident + "_gauge",
			
			title: "Production speed",
			label: "screws/hour",
			
			min: 0,
			max: 1000,
			
			labelColor: "#444",
			valueColor: "#444",
			titleColor: "#444",
			
			upperWarningLimit: 1000,
			upperActionLimit: 1000,
			
			showMinMax: true,
			canvasBackColor: "transparent",
			showGaugeShadow: false
			
		});
		
		//Update new tile once
		instance.update(initialDataUnit);
	};
	
	this.update = function(newDataUnit)
	{
		instance.title.text("Machine '" + newDataUnit.name + "'");
		
		instance.rightContent.html("Status: " + UI.statusTest[newDataUnit.status] + "<br>Job: " + newDataUnit.job);
		
		if(newDataUnit.status == 1) //Running TODO: Store those in constants
		{
			instance.gaugeBase.show();
			instance.repairIcon.hide();
			instance.cleaningIcon.hide();
			
		}else if(newDataUnit.status == 2 || newDataUnit.status == 3 || newDataUnit.status == 4) //TODO: Error displayed as repair, change!
		{
			instance.repairIcon.show();
			instance.gaugeBase.hide();
			instance.cleaningIcon.hide();
			
		}else if(newDataUnit.status == 5)
		{
			instance.repairIcon.hide();
			instance.gaugeBase.hide();
			instance.cleaningIcon.show();
		}
		
		instance.gauge.refresh(Math.round(newDataUnit.speed));
	};
	
	this.toggleSize = function()
	{
		if(instance.base.hasClass("tile-half"))
		{
			instance.base.removeClass("tile-half");
			instance.base.addClass("tile-full");
			
			//Slide tile to full size
			instance.base.animate({width: UI.dashboard.tileWidth * 2}, 350, "easeInOutElastic");

			
		}else if(instance.base.hasClass("tile-full"))
		{
			instance.base.removeClass("tile-full");
			instance.base.addClass("tile-half");
			
			instance.base.animate({width: UI.dashboard.tileWidth}, 350, "easeInOutElastic");
		}
	
	};
	//Link this function to the size button
	this.sizeButton.click(this.toggleSize);
}

/**
 * @constructor
 */
function Tile_Job(dataUnit)
{
	var instance = this; //We need to store this object, as 'this' is not usable in callbacks
	var initialDataUnit = dataUnit;
	
	this.ident = "job-" + dataUnit.id;
	this.dataUnitCategory = "job";
	this.dataUnitID = dataUnit.id;
	
	
	//Retrieve and set up HTML structure of tile from a hidden definition area in the document
	this.base = $("#tiledef_job").clone();
	this.base.attr("id","tile_" + this.ident);
	this.base.css("width",UI.dashboard.tileWidth);
	this.base.css("height",UI.dashboard.tileWidth);
	
	this.title = this.base.children("h2").first();
	this.sizeButton = this.base.children(".size-button").first();
	
	
	this.leftContent = this.base.children(".left-content").first();
	this.leftContent.css("width",UI.dashboard.tileWidth);
	this.leftContent.css("height",UI.dashboard.tileWidth - 13); //Minus 13 pixel header height
	this.leftContent.css("left",UI.dashboard.tileWidth);
	
	this.rightContent = this.base.children(".right-content").first();
	this.rightContent.css("width",UI.dashboard.tileWidth);
	this.rightContent.css("height",UI.dashboard.tileWidth - 13); //Minus 13 pixel header height
	this.rightContent.css("left",UI.dashboard.tileWidth);
	
	
	//Called immediately after tile was appended to dashboard
	this.onCreate = function()
	{
		
		
		//Update new tile once
		instance.update(initialDataUnit);
	};
	
	this.update = function(newDataUnit)
	{
		instance.title.text("Job '" + newDataUnit.id + "'");
	
		
	};
	
	this.toggleSize = function()
	{
		if(instance.base.hasClass("tile-half"))
		{
			instance.base.removeClass("tile-half");
			instance.base.addClass("tile-full");
			
			//Slide tile to full size
			instance.base.animate({width: UI.dashboard.tileWidth * 2}, 350, "easeInOutElastic");

			
		}else if(instance.base.hasClass("tile-full"))
		{
			instance.base.removeClass("tile-full");
			instance.base.addClass("tile-half");
			
			instance.base.animate({width: UI.dashboard.tileWidth}, 350, "easeInOutElastic");
		}
	
	};
	//Link this function to the size button
	this.sizeButton.click(this.toggleSize);
}


//--------packet contructors-----------

/**
 * Prototype for all packets.
 */
var Packet = {};

Packet.typeID = PTYPE.NULL; //MUST be initialized
Packet.timeout = GENERAL_TIMEOUT;
Packet.timeoutID = null;
Packet.allowedResponses = [];
Packet.onResponse = function(){};
Packet.onTimeout = function(){UI.showError("THe server took to long to respond to packet.");};

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
	
	this.onTimeout = function(){UI.showError("The server took to long to respond to handshake.");};
}
Packet_Handshake.prototype = Packet;

/**
 *
 * @constructor
 */
function Packet_Login(usr,pwrdHash)
{
	this.prototype = Packet;
	
	
	this.typeID = PTYPE.LOGIN;
	
	this.data = {};
	this.data.username = usr;
	this.data.passwordHash = pwrdHash;
	
	this.allowedResponses = [PTYPE.AUTH, PTYPE.NACK];
	
	this.onTimeout = function(){UI.showError("The server took to long to respond to login.");};
}
Packet_Login.prototype = Packet;

/**
 * 
 * @constructor
 */
function Packet_Relog(sessionID)
{
	this.prototype = Packet;
	
	
	this.typeID = PTYPE.RELOG;

	this.data = {};
	this.data.sessionID = sessionID;
	
	this.allowedResponses = [PTYPE.REAUTH, PTYPE.NACK];
	
	this.onTimeout = function(){UI.showError("The server took to long to respond to relog.");};
}
Packet_Relog.prototype = Packet;

/**
 * 
 * @constructor
 */
function Packet_Query(category, id)
{
	this.prototype = Packet;
	
	
	this.typeID = PTYPE.QUERY;

	this.data = {};
	this.data.category = category;
	this.data.id = id;
	
	this.allowedResponses = [PTYPE.DATA]; //Even though the protocol asks us to put ERROR here, there is no need to process it separately
	
	this.onTimeout = function(){UI.showError("The server took to long to respond to query.");};
}
Packet_Query.prototype = Packet;

/**
 * @constructor
 */
function Packet_Subscribe(category, id)
{
	this.prototype = Packet;
	
	
	this.typeID = PTYPE.SUBSCRIBE;
	
	this.data = {};
	this.data.category = category;
	this.data.id = id;
	
	this.allowedResponses = [PTYPE.ACK, PTYPE.ERROR];
	
	this.onTimeout = function(){UI.showError("The server took to long to respond to subscribe.");};
}
Packet_Subscribe.prototype = Packet;

/**
 * 
 * @constructor
 */
function Packet_Logout(reason, msg)
{
	this.prototype = Packet;
	
	
	this.typeID = PTYPE.LOGOUT;
	
	this.data = {};
	this.data.reasonCode = reason || LOGOUTREASON.UNKNOWN;
	this.data.reasonMessage = msg || "No message specified";
	
	this.allowedResponses = [PTYPE.ACK];
	
	this.onTimeout = function(){UI.showError("The server took to long to respond to logout.");};
}
Packet_Logout.prototype = Packet;

/**
 * 
 * @constructor
 */
function Packet_Error(code, message)
{
	this.prototype = Packet;
	
	
	this.typeID = PTYPE.ERROR;
	
	this.data = {};
	this.data.errorCode = code;
	this.data.errorMessage = message;
	
	//No timeout or responses allowed
	this.timeout = 0;
}
Packet_Error.prototype = Packet;

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

/**
 * Formats a Date object according to german date convention. TODO: Localize this
 * 
 * @param d The Date object
 * @returns {String}
 */
function util_formatDate(d)
{
	
	return d.getDate() + "." + (d.getMonth() + 1) + "." + d.getFullYear() + " " + d.getHours() + ":" + d.getMinutes();
	
}

/**
 * Checks if a given point lies in the bounds of a jQuery element.
 * 
 * @param element The jquery element
 * @param x X-coordinate of the point
 * @param y Y-coordinate of the point
 * @returns {Boolean}
 */
function util_containsPoint(element, x, y)
{
	var startX = element.offset().left;
	var startY = element.offset().top;
	var endX =  startX + element.width();
	var endY = startY + element.height();
	
	return (x > startX && y > startY && x < endX && y < endY);
}
