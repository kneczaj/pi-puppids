class PlayerPositions
	
	markers: {}
	circles: {}

	constructor: (@map) ->

	remove: (playerId) ->
		markers[playerId] = null
		circles[playerId] = null

	push: (playerId, latitude, longitude) ->
		markerOpts = 
			position: new google.maps.LatLng(lat, lng)
			draggable: false

		marker = new google.maps.Marker(markerOpts)
		markers[playerId] = marker

		circleOpts = 
			center: pos
			clickable: false
			fillColor: "RoyalBlue"
			fillOpacity: 0.1
			radius: u
			strokeColor: "RoyalBlue"
			strokeOpacity: 0.3 
			strokeWidth: 1
		
		circle = new google.maps.Circle(circleOpts)
		circles[playerId] = circle

