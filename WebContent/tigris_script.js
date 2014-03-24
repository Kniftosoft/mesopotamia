
/*--------------------------
 *      Tigris 0.3.8
 * 	Mesopotamia Client v1
 * (C) Niklas Weissner 2014
 *-------------------------- 
 */

//TODO: Give your sources
//TODO: Sort some code fragments so the source can be easier understood
//TODO: Comment on stuff
//TODO: Localization maybe?
//TODO: Add polish to data fetching functions (and try to check for null everywhere)

//Configure this to your Euphrates installation
//If your endpoint is absolute, make sure you include the full URI (including protocol etc.)
var ENDPOINT_IS_RELATIVE = true;
var MESO_ENDPOINT = "TIG_TEST_END"; //Link to Euphrates


//Constants
var TIGRIS_VERSION = "0.3.8";
var TIGRIS_SESSION_COOKIE = "2324-tigris-session";
var TIGRIS_SESSION_COOKIE_TTL = 365; //The number of days a stored session cookie will last 

var GENERAL_TIMEOUT = 3000; //Default timeout in milliseconds (may be overridden by some packets)

/**
 * Packet type IDs
 */
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

/**
 * Data type IDs
 */
var DTYPE =
{
	MACHINE:	1,
	JOB:		2,
	
	PRODUCT:	11,
	
	CONFIG:		20,
	
	
	byName: function(name)
	{
		return DTYPE[name.toUpperCase()];
	}
};

/**
 * Config type IDs
 */
var CTYPE =
{
	CONFIG_VERSION:		1,
	INTRO_FINISHED:		2,
	LOCALE:				3,
	TILES:				4
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
/**
 * Storage for data unit ident of table row beeing dragged
 */
UI.currentTableHelper = 
{
	id: 0,
	category: 0
};

UI.dashboard = null;
UI.sidebarEnabled = true;

UI.productTypes = {};

UI.init = function()
{	
	UI.dashboard = new Dashboard();
	
	//Set up events
	$(window).resize(UI.dashboard.resize);
	
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
                      { "mData": function(data, type, val) { return UI.statusTest[data.status]; }},
                      { "mData": "location"},
                      { "mData": "job" },
                      { "mData": "speed" },
                      { "mData": "totalProduced"}
                    ]});
	
	var tableSortOptions = 
		{
			distance: 10,
			
			connectWith: "#tilecreator",
			
			start: UI.table_dragStart,
			stop: UI.table_dragStop,
			sort: UI.table_dragging,
			
			helper: UI.table_createHelper,
			
			cursorAt: { top: 20, left: 20 },
			
			appendTo: document.body
		};
	
	$("#table-machine tbody").sortable(tableSortOptions);
	
	UI.jobTable = $("#table-job").dataTable(
			{
				"aoColumns": [
		              { "mData": "id" },
                      { "mData": "target" },
                      { "mData": function(data, type, val) { return util_formatDate(new Date(data.startTime));} },
                      { "mData": function(data, type, val) { return DataPool.getSingle(DTYPE.PRODUCT, data.productType).name || data.productType;} }
                ]
			});
	
	$("#table-job tbody").sortable(tableSortOptions);
	
	UI.showStatus("Connecting to the server..."); //Initially show message on connection status
};


/**
 * Called after all login-procedured are finished and a valid session is opened.
 */
UI.sessionSetup = function()
{
	//Load tile list from config
	var tileConfList = DataPool.getSingle(DTYPE.CONFIG, CTYPE.TILES);
	if(tileConfList != null)
	{
		try
		{
			//Don't forget tileConfigList.value is an ARRAY of strings! I know this is confusing.
			jQuery.each(tileConfList.value, function(index, item)
					{
						var unescapedString = util_dirtyUnescape(item);//We need to remove escape characters created while converting this to JSON
						
						var tileConfig = jQuery.parseJSON(unescapedString); 
						
						//Try to get the data unit for this tile
						DataPool.fetchSingle(tileConfig.category, tileConfig.id, function(tileDataUnit)
								{
									//When a valid data unit is returned, create a tile. When not, discard it TODO: Maybe show a greyed out tile or something
									if(tileDataUnit != null)
									{
										f_createTileOnDashboard(tileConfig.category, tileDataUnit, tileConfig.column ,false); //Don't store tiles. We would send exactly what we received
									}
								});
				
					});
			
		}catch(e)
		{
			console.error("The tile configuration string could not be parsed: " + e); //No need to tell the user. It's not that fatal. Just log the error for debugging
		}
	}
	
};

/**
 * Show screen with given name without any animation.
 * 
 * @param name The name of the screen to be displayed
 */
UI.showScreen = function(name)
{
	$(".screen").hide(); //Hide all screens...
	$("#screen-" + name).show(); //and show only desired one
	
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
	
	Network.clearAllTimeouts();
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
		UI.dataUpdate(target);
		
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
 * Updates the data view if displayed.
 */
UI.dataUpdate = function(target)
{
	var t = target || UI.currentTab;
	
	if(UI.currentScreen == "data")
	{
		if(t == "dashboard")
		{
			UI.dashboard.refresh();
		}else
		{
			UI.data_updateTable(t);
		}
	}
};

/**
 * Updates a data table.
 * 
 * @param target The name of the target data table
 */
UI.data_updateTable = function(target)
{	
	var table = null;
	var category = 0;
	
	if(target == "machine")
	{
		table = UI.machineTable;
		category = DTYPE.MACHINE;
	}else if(target == "job")
	{
		table = UI.jobTable;
		category = DTYPE.JOB;
	}else
	{
		UI.showError("Tried to update unsupported table: " + target);
	}
	
	DataPool.fetchWhole(category,
			function(data, rawData)
			{
		
				table.fnClearTable();
				
				table.fnAddData(rawData);
			});
	
};

UI.table_dragStart = function(event, ui)
{
	$("#tilecreator").show("slide");
	
	UI.sidebarEnabled = false;
};

UI.table_dragging = function(event, ui)
{
	//Check if cursor is inside tileCreator
	//Since hover detection does not work with a sortable on the cursor, we have to check manually by position
	if(util_containsPoint(UI.tileCreator,event.pageX,event.pageY) && UI.currentTab != "dashboard")
	{
		$("#tilecreator").hide("slide");
		
		UI.data_switchTab("dashboard");
	}
};

UI.table_dragStop = function(event, ui)
{
	UI.sidebarEnabled = true;
	
	$("#tilecreator").hide("slide");
	
	//The user might have reordered the table and thus messed up the coloring, so better reload data
	//(This would not be neccesary if we could use something different than sortable for draggable table rows;
	// thanks to you, jQuery UI)
	UI.dataUpdate();
	
	//Was tile dropped on dashboard?
	if(util_containsPoint(UI.dashboard.base,event.pageX,event.pageY) && UI.currentTab == "dashboard")
	{
		//Yes -> Create new tile
		
		//Get the data unit ident of the dropped element (Stored in attributes of the custom helper)
		var id = UI.currentTableHelper.id;
		var dataUnitCatID = UI.currentTableHelper.category;
		
		DataPool.fetchSingle(dataUnitCatID,id,
				function(dataUnit)
				{
					if(dataUnit != null)
					{
						
						f_createTileOnDashboard(dataUnitCatID,dataUnit,true); //Create tile and write tile config to remote storage
					}
				});
	}
};

/**
 * Creates a helper for the sortable widget. Provides a preview of the tile that can be placed
 * on the dashboard.
 */
UI.table_createHelper = function(event, item)
{
	//Get data unit information in order to create tile object for dragging around
	var id = item.find("td").first().text(); //ID is the first table column in the dragged row
	var dataUnitCatID = DTYPE.byName(UI.currentTab);
	
	var dataUnit = DataPool.getSingle(dataUnitCatID, id);
	if(dataUnit != null)
	{
		var tile = tileFromCategory(dataUnitCatID, dataUnit);
		
		tile.update(dataUnit);
		
		UI.currentTableHelper.id = id;
		UI.currentTableHelper.category = dataUnitCatID;
		
		return tile.base;
	}else
	{
		return this;
	}
};

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
	
	var packet = new Packet_Login(username, finalPasswordHash, remember);
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
					util_setCookie(TIGRIS_SESSION_COOKIE, Session.sessionID,TIGRIS_SESSION_COOKIE_TTL);
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
					Session.sessionID = pk.data.newSessionID;
					
					//Store the new session ID to cookie TODO: maybe integrate this into f_setUpSession()
					//Since this cookie was created in a session the user wanted to keep, we can assume the user wants still to keep it
					util_setCookie(TIGRIS_SESSION_COOKIE, Session.sessionID, TIGRIS_SESSION_COOKIE_TTL);
					
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
	
	var packet = new Packet_Logout(Session.sessionID,LOGOUTREASON.CLOSED_BY_USER, "User requested logout");

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
	msgs[LOGOUTREASON.SESSION_EXPIRED] = "your session lost it's validity.";
	msgs[LOGOUTREASON.INTERNAL_ERROR] = "an internal server error occurred.";
	msgs[LOGOUTREASON.REFUSED] = "you were somehow suddenly refused.";
	msgs[LOGOUTREASON.CLOSED_BY_USER] = "it says the user did it. Alright, who was it then?";
	
	Session.clear();
	
	UI.showLogin();
	UI.login_showError("The server logged you out because " + msgs[reason]);
}

function f_loadServerSideConfig()
{
	//First fetch config, then product data
	
	//Fetch config data from server
	DataPool.fetchWhole(DTYPE.CONFIG,
	function(data)
	{
		//data-field is ignored, we don't need it here -> config data is also stored in data pool from now on
		
		//Load a list of product types so IDs can be resolved to names. After that, display data screen TODO: Maybe remove this and integrate it into mData function
		DataPool.fetchWhole(DTYPE.PRODUCT, function(data)
				{
					UI.sessionSetup();
					
					UI.showData();
				});
		
	});
}

/**
 * Subscribes the data unit and creates a linked tile on the dashboard
 * when the subscriptions succeeds. 
 * 
 * @param category The category id of data unit
 * @param dataUnit The data unit object
 * @param store Set to true if storage should be updated after succesful creation
 * @returns {any} The tile
 */
function f_createTileOnDashboard(category, dataUnit, column, store)
{
	
	var subPacket = new Packet_Subscribe(category,dataUnit.id);
	subPacket.onResponse = function(pk)
	{
		if(pk.typeID == PTYPE.ACK)
		{
			var tile = tileFromCategory(category, dataUnit);
			
			tile.columnID = column;
			
			if(tile != null)
			{
				UI.dashboard.addTile(tile,store);
				
				//In order not to loose anything, we must store the tile configs
				UI.dashboard.storeTiles();
			}
			
		}else if(pk.typeID == PTYPE.ERROR)
		{
			UI.showError("Subscribing a data unit has failed.");
		}
	};
	
	Network.sendPacket(subPacket);
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
 * Stores a single config entry (consisting of an array of strings) in the
 * user-specific server-side config file.
 */
Session.setConfig = function(id, value)
{
	var config = new Packet_Config(id, value);
	config.onResponse = function(pk)
	{
		if(pk.typeID == PTYPE.NACK)
		{
			UI.showError("Could not set config " + id + " to " + value);
		}
	};
	
	Network.sendPacket(config);
};

/**
 * Removes any session related data to begin a new session.
 */
Session.clear = function()
{
	Session.sessionID = null;
	Session.username = "";
	
	util_setCookie(TIGRIS_SESSION_COOKIE, "", -1);
};


/**
 * Namespace for storing, receiving and managing data.
 */
var DataPool = {};

DataPool.pools = {};

/**
 * Retrieves a single data unit. If the desired data unit is not stored locally,
 * it is retrieved from server. After it has been successfully located, callback
 * is called with the unit as parameter. If the desired data unit could neither be
 * found locally nor be fetched from server, the callback is called with null.
 * 
 * Note that the returned data may not be up-to-date. If most recent data is wanted,
 * the optional parameter forceQuery can be set to true to always fetch from server
 * regardless of whether the data is stored locally or not.
 */
DataPool.fetchSingle = function(category, id, callback, forceQuery)
{
	//Do we already have the desired data in storage and is no query wanted?
	if(DataPool.hasItem(category,id) && !forceQuery)
	{
		//Yes -> no need for query
		
		var pool = DataPool.pools[category];
		callback(pool[id]);
		
	}else
	{
		//No -> We need to query from server
		
		var query = new Packet_Query(category,id);
		query.onResponse = function(pk)
		{
			if(pk.typeID == PTYPE.DATA)
			{
				//Data received -> Store it to pool (using echoed category to allow redirection)
				DataPool.updateByData(pk.data.category, pk.data.result);
				
				//Do we have the desired unit now?
				if(DataPool.hasItem(category,id))
				{
					//Yes -> report to callback
					var pool = DataPool.pools[category];
					
					callback(pool[id]);
				}else
				{
					//No -> Return null to callback TODO: Check if we might count this as an error
					
					callback(null);
				}
			}
		};
		
		Network.sendPacket(query);
	}
};

/**
 * Retrieves a whole data category. Different to fetchSingle, this function
 * ALWAYS queries the data from the server, as there is no way to check if the current
 * local pool is complete.
 */
DataPool.fetchWhole = function(category, callback)
{
	//We always need to query from server
		
	var query = new Packet_Query(category,"*");
	query.onResponse = function(pk)
	{
		if(pk.typeID == PTYPE.DATA)
		{
			//Data received -> Store pool (using echoed category to allow redirection)
			DataPool.updateByData(pk.data.category, pk.data.result);
			
			//Do we really have the right pool?
			if(category in DataPool.pools)
			{
				//Yes -> report to callback
				callback(DataPool.pools[category], pk.data.result);
				
			}else
			{
				//No -> Return null to callback TODO: Check if we might count this as an error
				
				callback(null);
			}
		}
	};
		
	Network.sendPacket(query);
	
};

/**
 * Returns a single, locally stored data unit. Different to fetchSingle,
 * this function needs no callback function and data is not queried when non-existent.
 * In the latter case, null is returned.
 */
DataPool.getSingle = function(category, id)
{
	if(DataPool.hasItem(category,id))
	{
		
		var pool = DataPool.pools[category];
		return pool[id];
		
	}else
	{
		return null;
	}
};

/**
 * Checks if a specific data unit is present in local storage.
 * 
 * @returns {boolean}
 */
DataPool.hasItem = function(category,id)
{
	if(!DataPool.pools[category])
	{
		return false;
	}
	
	var pool = DataPool.pools[category];
	return (pool[id] != "undefined");
};

/**
 * Queries the whole data pool and stores it upon receipt.
 * 
 * TODO: Not needed; remove if not in use upon release
 */
DataPool.refreshPool = function(category)
{
	var query = new Packet_Query(category, "*");
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
		//The endpoint path is relative -> we must construct an absolute path from our current URL
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
	Network.handshake(true); //Only start handshaking after connection has been established
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
	
	//Check for obligatory fields
	if(packet.uid === "undefined" || packet.typeID === "undefined" || packet.data === "undefined")
	{
		Network.serverError(ERRORCODE.BAD_PACKET, "Packet did not contain one or more of the fields: typeID, uid, data");
		
		UI.showError("A fatal communication error occurred: Bad packet and shit.");
		
		return;
	}
	
	
	//Check for type-specific fields
	var typeFields = packet_fields[packet.typeID];
	
	if(typeFields == "undefined")
	{
		Network.serverError(ERRORCODE.BAD_PACKET, "Unknown packet type or C->S packet only: " + packet.typeID);
		
		UI.showError("A fatal communication error occurred: Bad packet and shit.");
		
		return;
	}
	
	var missingFields = "";
	jQuery.each(typeFields,
			function(index,item)
			{
				if(packet.data[item] == "undefined")
				{
					missingFields += item + " ";
				}
				
			});
	if(missingFields != "")
	{
		Network.serverError(ERRORCODE.BAD_PACKET, "Packet of type " + packet.typeID + " was missing the following fields: " + missingFields);
		
		UI.showError("A fatal communication error occurred: Bad packet and shit.");
		
		return;
	}
	
	//Locate packet target
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
			
			if(tryRelog)
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

Network.clearAllTimeouts = function()
{
	jQuery.each(Network.sentPacketMap,
			function(index, item)
			{
				if(item.timeoutID != null)
				{
					window.clearTimeout(item.timeoutID);
				}
			});
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
			//Store packet so a response can be passed to it's response handler
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
		//Write data to data pool
		DataPool.updateByData(pk.data.category, pk.data.result);
		
		UI.dataUpdate();
		
	}else if(pk.typeID == PTYPE.LOGOUT)
	{
		f_serverSideLogout(pk.data.reasonCode);
		
		//We should tell the server we acknowledge the logout
		var resp = new Packet_Ack();
		Network.sendPacket(resp, pk.uid);
		
	}else
	{
		//This packet type was not recognized. Tell the server we did not understand his message
		
		Network.serverError(ERRORCODE.BAD_PACKET, "Unrecognized packet. May be C->S packet only: " + packet.typeID);
	}
	
};

//------screen constructors----------

/**
 * @constructor
 */
function Dashboard()
{
	var reThis = this; //We can not use 'this' in functions called from outside, so we store it
	
	this.base = $("#tab-dashboard");
	
	this.tiles = {}; //Map of tiles on the dashboard indexed with data unit identifier (category-ident)
	this.columns = new Array();
	
	this.resize =
		function()
		{
			//As the dashboard has the width of the document with absolute margins, we can calculate the tile sizes using the documents width
			reThis.width = $(document).width() - 20; //No idea why we have to take the margin times 2, but it works
			reThis.height = $(document).height() - 57;
			
			reThis.base.css("width",reThis.width);
			reThis.base.css("height",reThis.height);
			
			//Apparently, a horizontal resolution of 1920 is best viewed with 4 columns TODO: Further research on that 
			// (1920 - 10) / 4 = 477.5
			reThis.columnCount = Math.round(reThis.width / 477.5);
			reThis.columnWidth = reThis.width / reThis.columnCount;
			
			reThis.tileWidth = (reThis.columnWidth - 10) / 2; //Width of a half tile (including the 5px margin on each side)
			
			//Update column size
			for(var i = 0; i < reThis.columnCount ; i++)
			{
				//Change width of existing columns, create new for non-existing
				if(reThis.columns[i])
				{
					reThis.columns[i].css("width",reThis.columnWidth);
				}else
				{
					var col = $("<div>");
					col.attr("id","dash-col-" + i);
					col.addClass("dashboard-column");
					col.css("width",reThis.columnWidth);
					
					reThis.columns[i] = col;
					reThis.base.append(col);
				}
				
			}
			
			//Update tile size
			jQuery.each(reThis.tiles, 
					function(index, item)
					{
						item.resize(reThis.tileWidth);
					});
		};
	this.resize();
	
	
	this.addTile = 
		function(tile, store)
		{
			if(tile.ident in reThis.tiles)
			{
				//Tile linking to the same data source is already on the dashboard -> refuse adding TODO: Allow grouping
				return;
			}
		
			reThis.tiles[tile.ident] = tile;
		
			var column = tile.columnID || 0; //Append tile to specified column. If not specified, add to 0
			
			//If the column this tile was stored in does not exist anymore, put it in the last column
			if(column >= reThis.columns.length)
			{
				column = reThis.columns.length - 1;
			}
			
			reThis.columns[column].append(tile.base);
			tile.columnID = column;
			
			tile.onCreate();
		};
	
		
	this.refresh =
		function()
		{
			//Careful! This function is called upon receipt of tile-relevant data. Do
			//not access remote data by yourself here!
		
			//Fetch new data unit object from data pool for each tile 
			jQuery.each(reThis.tiles, 
					function(index, item)
					{
						//Is a data unit registered for the current tile? (Use local data only)
						var dataUnit = DataPool.getSingle(item.dataUnitCategory,item.dataUnitID);
				
						if(dataUnit)
						{
							//Yes -> Update tile
							
							item.update(dataUnit);
						}
					});
		
		};
		
	
	this.storeTiles = function()
	{
		
		var configArray = new Array();
		
		jQuery.each(reThis.tiles,
				function(index, item)
				{
					var tileStor = {};
					
					tileStor.id = item.dataUnitID;
					tileStor.category = item.dataUnitCategory;
					tileStor.column = item.columnID;
					
					var tileStorString = JSON.stringify(tileStor);
					
					configArray.push(tileStorString);
				});
		
		
		
		Session.setConfig(CTYPE.TILES, configArray);
		
	};
		
		
	//Everything is created -> now we can add jQuery UI stuff
	$(".dashboard-column").sortable(
			{
				connectWith: ".dashboard-column",
				placeholder: "tile-placeholder",
				
				start: function(event, ui)
				{
					UI.sidebarEnabled = false;
					
					//As jQuery UIs sortable does not support dynamic sized placeholders,
					//we must change them manually
					if(ui.item.hasClass("tile"))
					{
						//Only change the placeholder for actual tiles beeing dragged
						
						$(".tile-placeholder").css("height", reThis.tileWidth); //Height is always the same
						$(".tile-placeholder").css("width", ui.item.hasClass("tile-half") ? reThis.tileWidth : reThis.tileWidth*2);
						
					}
					
				},
				
				stop: function(event, ui)
				{
					UI.sidebarEnabled = true;
				},
			
				handle: "h2",
				
				revert: 200,
				
				tolerance: "pointer",
				
				scrollSpeed: 10
			});
}

//-------tile constructors------------

/**
 * Selects right tile constructor from given category ID.
 */
function tileFromCategory(category, dataUnit)
{
	if(category == DTYPE.MACHINE)
	{
		return new Tile_Machine(dataUnit);
	}else if(category == DTYPE.JOB)
	{
		return new Tile_Job(dataUnit);
	}else
	{
		UI.showError("Tried to create tile of unknown category: " + category);
		return null;
	}
}


/**
 * @constructor
 */
function Tile_Machine(dataUnit)
{
	var reThis = this; //We need to store this object, as 'this' is not usable in callbacks called in other contexts
	var initialDataUnit = dataUnit;
	
	this.dataUnitCategory = DTYPE.MACHINE;
	this.dataUnitID = dataUnit.id;
	this.ident = this.dataUnitCategory + "-" + this.dataUnitID;
	
	this.width = 1; //Set upon size calculation
	
	//Retrieve and set up HTML structure of tile from a hidden definition area in the document
	this.base = $("#tiledef_machine").clone();
	this.base.attr("id","tile_" + this.ident);
	
	this.title = this.base.children("h2").first();
	this.sizeButton = this.base.find(".size-button").first();
	
	this.leftContent = this.base.find(".left-content").first();
	this.rightContent = this.base.find(".right-content").first();
	
	this.repairIcon = this.base.find(".repair-icon").first();
	this.cleaningIcon = this.base.find(".cleaning-icon").first();
	this.errorIcon = this.base.find(".error-icon").first();
	
	this.statusValue = this.base.find(".status-value").first();
	this.jobValue = this.base.find(".job-value").first();
	this.locationValue = this.base.find(".location-value").first();
	
	this.gaugeBase = this.base.find(".machine-speed-gauge").first();
	this.gaugeBase.attr("id","tile_" + this.ident + "_gauge");
	
	this.gauge = null;
	
	this.STATUS =
		{
			RUNNING:	1,
			ERROR:		2,
			REPAIR:		3,
			MODIFIC:	4,
			CLEANING:	5
		};
	
	//Called immediately after tile was appended to dashboard
	this.onCreate = function()
	{
		//Create gauge
		reThis.gauge = new JustGage(
				{
					id: "tile_" + reThis.ident + "_gauge",
					value: 0,
					min: 0,
					max: 1000,
					title: "Production speed",
					
					 showInnerShadow: false
				});
		
		//Update new tile once
		reThis.update(initialDataUnit);
	};
	
	this.resize = function(size)
	{
		reThis.width = size;
		
		reThis.base.css("height",size);
		
		if(reThis.base.hasClass("tile-full"))
		{
			reThis.base.css("width",size*2);
		}else
		{
			reThis.base.css("width",size);
		}
		
		
		reThis.leftContent.css("width",size);
		reThis.leftContent.css("height",size - 13); //Minus 13 pixel header height
		reThis.leftContent.css("left",size);
		
		reThis.rightContent.css("width",size);
		reThis.rightContent.css("height",size - 13); //Minus 13 pixel header height
		reThis.rightContent.css("left",size);
		
		//Adjust size and margin for all icons
		var icons = reThis.leftContent.children(".icon");
		icons.css("width",size * 0.8);
		icons.css("height",size * 0.8);
		icons.css("margin-left",size * 0.1); //Center icons
		icons.css("margin-right",size * 0.1);
		
		reThis.gaugeBase.css("width", size);
		reThis.gaugeBase.css("height", size);
	};
	this.resize(UI.dashboard.tileWidth); //Set up tile size
	
	
	this.update = function(newDataUnit)
	{
		reThis.title.text("Machine '" + newDataUnit.name + "'");
		
		reThis.statusValue.text(UI.statusTest[newDataUnit.status] || newDataUnit.status);
		reThis.jobValue.text(newDataUnit.job);
		reThis.locationValue.text(newDataUnit.location);
		
		if(newDataUnit.status == reThis.STATUS.RUNNING)
		{
			reThis.base.find(".icon").hide(); //Hide all icons
			
			//Gauge can only be created after it has been added to document. This, however may be calleb before that
			if(reThis.gauge != null)
			{
				reThis.gaugeBase.show();
				reThis.gauge.refresh(Math.round(newDataUnit.speed)); //Refresh gauge when running
			}
				
		}else if(newDataUnit.status == reThis.STATUS.REPAIR || newDataUnit.status == reThis.STATUS.MODIFIC)
		{
			reThis.gaugeBase.hide();
			reThis.base.find(".icon").hide();
			
			reThis.repairIcon.show();
			
		}else if(newDataUnit.status == reThis.STATUS.ERROR)
		{
			reThis.gaugeBase.hide();
			reThis.base.find(".icon").hide();
			
			reThis.errorIcon.show();
			
			
		}else if(newDataUnit.status == reThis.STATUS.CLEANING)
		{
			reThis.gaugeBase.hide();
			reThis.base.find(".icon").hide();
			
			reThis.cleaningIcon.show();
		}
		
	};
	
	this.toggleSize = function()
	{
		if(reThis.base.hasClass("tile-half"))
		{
			reThis.base.removeClass("tile-half");
			reThis.base.addClass("tile-full");
			
			//Slide tile to full size
			reThis.base.animate({width: reThis.width * 2}, 350, "easeInOutElastic");

			
		}else if(reThis.base.hasClass("tile-full"))
		{
			reThis.base.removeClass("tile-full");
			reThis.base.addClass("tile-half");
			
			reThis.base.animate({width: reThis.width}, 350, "easeInOutElastic");
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
	
	this.dataUnitCategory = DTYPE.JOB;
	this.dataUnitID = dataUnit.id;
	this.ident = this.dataUnitCategory + "-" + this.dataUnitID;
	
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
Packet.onTimeout = function(){UI.showError("The server took to long to respond to packet.");};

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
function Packet_Login(usr,pwrdHash,persist)
{
	this.typeID = PTYPE.LOGIN;
	
	this.data = {};
	this.data.username = usr;
	this.data.passwordHash = pwrdHash;
	this.data.persist = persist;
	
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
 * @param category Category of data unit
 * @param id ID of data unit
 */
function Packet_Subscribe(category, id)
{		
	this.typeID = PTYPE.SUBSCRIBE;
	
	this.data = {};
	this.data.category = category;
	this.data.id = id;
	
	this.allowedResponses = [PTYPE.ACK, PTYPE.ERROR];
	
	this.onTimeout = function(){UI.showError("The server took to long to respond to subscribe.");};
}
Packet_Subscribe.prototype = Packet;

/**
 * @constructor
 * @param category Category of data unit
 * @param id ID of data unit
 */
function Packet_Unsubscribe(category, id)
{
	this.typeID = PTYPE.UNSUBSCRIBE;
	
	this.data = {};
	this.data.category = category;
	this.data.id = id;
	
	this.allowedResponses = [PTYPE.ACK, PTYPE.ERROR];
	
	this.onTimeout = function(){UI.showError("The server took to long to respond to unsubscribe.");};
};
Packet_Unsubscribe.prototype = Packet;

/**
 * @constructor
 * @param id The numerical ID of the config field
 * @param value The value to be stored
 */
function Packet_Config(id, value)
{	
	this.typeID = PTYPE.CONFIG;
	
	this.data = {};
	this.data.id = id;
	this.data.value = value;
	
	this.allowedResponses = [PTYPE.ACK, PTYPE.NACK];
	
	this.onTimeout = function(){UI.showError("The server took to long to respond to config.");};
};
Packet_Config.prototype = Packet;

/**
 * 
 * @constructor
 */
function Packet_Logout(session, reason, msg)
{
	this.typeID = PTYPE.LOGOUT;
	
	this.data = {};
	this.data.sessionID = session;
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
	this.typeID = PTYPE.ERROR;
	
	this.data = {};
	this.data.errorCode = code;
	this.data.errorMessage = message;
	
	//No timeout or responses allowed
	this.timeout = 0;
}
Packet_Error.prototype = Packet;


/**
 * Array storing fields defining must-have-fields for incoming packets. Used for type safety checking
 * and error reporting.
 *
 * TODO: Find better name for this field
 */
var packet_fields = new Array();
packet_fields[PTYPE.NULL] = [];
packet_fields[PTYPE.ACCEPT] = ["salt"];
packet_fields[PTYPE.AUTH] = ["sessionID"];
packet_fields[PTYPE.REAUTH] = ["newSessionID","username"];
packet_fields[PTYPE.LOGOUT] = ["sessionID","reasonCode","reasonMessage"];
packet_fields[PTYPE.DATA] = ["category","result"];
packet_fields[PTYPE.ACK] = [];
packet_fields[PTYPE.NACK] = [];
packet_fields[PTYPE.ERROR] = ["errorCode","errorMessage"];

//---------------utility stuff----------------

/**
 * Sets cookie with specific name, value and time-to-live.
 * 
 * @param name Name of the cookie
 * @param value Value of the cookie
 * @param ttl Lifespan of the cookie in days
 */
function util_setCookie(name, value, ttl)
{
	var expires = new Date();
	expires.setDate(expires.getDate() + ttl);

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

/**
 * Removes every '\' while leaving the follwing character ('\\' converts to '\').
 * Doesn't resolve '\n' to new line. Simply inserts 'n'. Therefore dirty.
 * 
 * @param string The string to unescape
 * @returns {string}
 */
function util_dirtyUnescape(string)
{
	var result = "";
	
	for(var i = 0; i < string.length; i++)
	{
		var c = string.charAt(i);
		
		if(c == "\\")
		{
			result += string.charAt(++i);
		}else
		{
			result += c;
		}
	}
	
	return result;
}
