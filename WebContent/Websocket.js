var wsuri ="ws://"+document.location.host + document.location.pathname+"MESOEND";
var websocket = new WebSocket(wsuri);
var output = document.getElementById("output");
websocket.onopen = function(evt) { onOpen(evt);};
websocket.onerror = function(evt) { onError(evt);};
websocket.onmessage = function(evt) { onMessage(evt);};

function save()
{
	var data = document.getElementById('Text1').value;
	
	var json = JSON.stringify(
			{
				"method":"test1",
				"data"	:data
			}		
			);
	websocket.send(json);
}
function onError(evt)
{
	writeToScreen('<span style="Color: red;">ERROR:</span>'+evt.data);
}
//For testing purposes

function writeToScreen(message){
    output.innerHTML += message + "<br>";
}

function onOpen(evt){
    writeToScreen("Connected to " + wsuri);
    var json = JSON.stringify(
			{
				"method":"test1",
				"data"	:"Hello Socket"
			}		
			);
    websocket.send(json);
}
function onMessage(evt) {
    console.log("received: " + evt.data);
    writeToScreen(evt.data);
}
// End test functions