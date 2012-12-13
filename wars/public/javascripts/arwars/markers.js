var setPlayerMarker = function(playerId, lat, lng) {
	console.log("put marker for player " + playerId + " to lat: " + lat
			+ ", lng: " + lng);

	if (player2Marker[playerId]) {
		player2Marker[playerId].setMap(null);
	}

	var marker = new google.maps.Marker({
		position : new google.maps.LatLng(lat, lng),
		draggable : false
	});
	marker.setMap(map);

	player2Marker[playerId] = marker;
}