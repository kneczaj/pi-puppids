var setPlayerMarker = function(playerId, lat, lng, uncertainty) {
	console.log("put marker for player " + playerId + " to lat: " + lat
			+ ", lng: " + lng + ", uncertainty: " + uncertainty);

	if (playerId == null || lat == null || lng == null) {
		return;
	}

	if (player2Marker[playerId]) {
		player2Marker[playerId].setMap(null);
	}

	var pos = new google.maps.LatLng(lat, lng);
	var marker = new google.maps.Marker({
		position : pos,
		draggable : false
	});
	marker.setMap(map);
	player2Marker[playerId] = marker;

	var u = 1000;
	if (uncertainty) {
		u = uncertainty;
	}
	
	if (player2Circle[playerId]) {
		player2Circle[playerId].setMap(null);
	}

	var circle = new google.maps.Circle({
		center : pos,
		clickable : false,
		fillColor : "RoyalBlue",
		fillOpacity : 0.1,
		radius : u,
		strokeColor : "RoyalBlue",
		strokeOpacity : 0.3, 
		strokeWidth : 1,
	});

	circle.setMap(map);
	player2Circle[playerId] = circle;
}