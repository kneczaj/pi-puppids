/*
 * For detailed information about the Gelocation API of HTML5 
 * http://diveintohtml5.info/geolocation.html was used
 */

var defaultGeolocationOptions = {
	enableHighAccuracy : true,
	timeout : 3000,
	maximumAge : 200
}
var locationWatchHandle = null;

function setLocationWatcher(options) {
	if (options == null) {
		options = defaultGeolocationOptions;
	}

	if (locationWatchHandle != null) {
		navigator.geolocation.clearWatch(locationWatchHandle);
	}

	locationWatchHandle = navigator.geolocation.watchPosition(positionChanged,
			errorCallback, options);
}

function errorCallback(error) {
	if (error.code == 1) { // permission was denied by user
		console.log("User denied geolocation");
	} else if (error.code == 2) { // position unavailable
		console.log("position unavailable");
	} else if (error.code == 3) { // timeout in calculating / finding the
									// position
		console.log("timeout while calculating position");
	} else {
		console.log("unknown error");
	}
}

function positionChanged(location) {
	var lat = location.coords.latitude;
	var lng = location.coords.longitude;
	var accuracy = location.coords.accuracy;
	var time = new Date(location.timestamp);
	var speed = location.coords.speed;
	$("#locations").append(
			"Time: " + time + " Lat: " + lat + " Long: " + lng + " Accuracy: "
					+ accuracy + " meters<br />");

	var serializedData = {
		playerId : playerId,
		lat : lat,
		lng : lng,
		uncertainty : accuracy,
		speed : speed,
		timestamp : location.timestamp
	}

	$.ajax({
		url : "/updateLocation",
		type : "get",
		data : serializedData,
		success : function(response, textStatus, jqXHR) {
			console.log("successfuly pushed new location to the server");
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.log("The following error occured: " + textStatus,
					errorThrown);
		}
	});
}
