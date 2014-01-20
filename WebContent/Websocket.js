var wsuri ="ws://"+document.location.host + document.location.pathname+"MESOEND";
var websocket = new WebSocket(wsuri);
var output = document.getElementById("output");
websocket.onopen = function(evt) { onOpen(evt);};
websocket.onerror = function(evt) { onError(evt);};
websocket.onmessage = function(evt) { onMessage(evt);};

function save()
{
	text = document.getElementById('Text1').value;
	websocket.send(text);
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
    websocket.send("Hello Socket");
}
function onMessage(evt) {
    console.log("received: " + evt.data);
    writeToScreen(evt.data);
}
// End test functions