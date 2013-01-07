# log helper
log = (args...) ->
    console.log.apply console, args if console.log?

# keeps track of position of the players nearby
class ArWars.PlayerPositionManager

	@mapOptions = 
		center : new google.maps.LatLng 48.133, 11.566
		zoom : 16
		mapTypeId : google.maps.MapTypeId.ROADMAP
		styles : [ { "stylers": [ { "invert_lightness": true }, { "saturation": -80 } ] },{ "featureType": "road", "elementType": "geometry", "stylers": [ { "color": "#646464" } ] },{ "featureType": "road", "elementType": "labels.icon", "stylers": [ { "visibility": "off" } ] },{ "featureType": "poi", "elementType": "labels", "stylers": [ { "color": "#faa732" }, { "weight": 0.1 } ] } ]

	@locationOptions = 
		enableHighAccuracy : true
		timeout : 3000
		maximumAge : 500

	@circleOpts =  
		clickable: false
		fillColor: "RoyalBlue"
		fillOpacity: 0.2
		strokeWidth: 0
		strokeOpacity: 0
	
	markers: []
	circles: []
	players: []
	
	locationWatchHandle: null
	map: null
	mapNode: null
	selectedPlayer: null
	infoPanel: null
	infowindow: null

	constructor: (@mapNode, @infoPanel) ->
		@map = new google.maps.Map @mapNode, ArWars.PlayerPositionManager.mapOptions
		@locationWatchHandle = navigator.geolocation.watchPosition @onPositionChange, @onPositionError, ArWars.PlayerPositionManager.locationOptions

	# Removes a player from the map (removes the circle and the marker)
	removeFromMap: (pId) ->
		@markers[pId] = null
		@circles[pId] = null

	loadPlayersNearby: (lat, lng) -> 
		d = 
			lat: lat
			lng: lng
		
		$.ajax
			url : "/mapinfo/playersNearby"
			type : "get"
			data : d
			dataType : "json"
			success : (response, textStatus, jqXHR) =>
				for key, value of response
					@players[key] = value.player
					@push2Map key, value.latitude, value.longitude, value.uncertainty
			
			error : (jqXHR, textStatus, errorThrown) ->
				log textStatus
				
	# Show places on the map around the player location
	loadPlacesNearby: (lat, lng) ->
		request = 
			location: 	new google.maps.LatLng(lat, lng)
			radius:		400
			types: 		["atm", "bakery", "school", "church", "movie_theater", "pharmacy", "train_station"]
			
		@infowindow = new google.maps.InfoWindow(content: "Loading...")
		service = new google.maps.places.PlacesService(@map)
		service.nearbySearch(request, @callback)

	callback: (results, status, pagination) =>
		if status is google.maps.places.PlacesServiceStatus.OK
			@createMarker(i) for i in results
			
		if pagination.hasNextPage
    		pagination.nextPage()

	createMarker: (place) ->
		placeLoc = place.geometry.location
		iconUrl = undefined
		switch place.types[0]
		  when "atm"
		    iconUrl = "/assets/images/credits.png"
		  when "bakery"
		    iconUrl = "/assets/images/food.png"
		  when "school"
		    iconUrl = "/assets/images/knowledge.png"
		  when "church"
		    iconUrl = "/assets/images/special.png"
		  when "movie_theater"
		    iconUrl = "/assets/images/cultural.png"
		  when "pharmacy"
		    iconUrl = "/assets/images/material.png"
		  when "train_station"
		    iconUrl = "/assets/images/transportation.png"
		  else
		    iconUrl = ""
		
		markerOpts = 
			map: @map
			position: place.geometry.location
			icon: iconUrl
		
		marker = new google.maps.Marker markerOpts
	
		google.maps.event.addListener marker, "click", =>
		  @infowindow.setContent place.name + "<br/>" + place.vicinity + "<br/>Type: " + place.types[0] + "<br/>Resource: <img src=\"" + marker.icon + "\"><br/><br/><button class=\"btn btn-block btn-warning\" type=\"button\">Conquer</button>"
		  @infowindow.open @map, marker

	# Called when LocationAPI detects a location change of the current player
	onPositionChange: (location) =>
		coords = location.coords
		@push2Map window.ArWars.playerId, coords.latitude, coords.longitude, coords.accuracy

		if not coords.latitude?
			return

		currentDate = new Date
		currentTimestamp = currentDate.getTime()
		timestamp = location.timestamp

		if timestamp.toString().length > currentDate.getTime().toString().length
			timestamp = Math.floor(timestamp / 1000)

		serializedData = 
			lat : coords.latitude
			lng : coords.longitude
			uncertainty : coords.accuracy
			speed : coords.speed
			timestamp : timestamp

		$.ajax
			url : "/updateLocation"
			type : "get"
			data : serializedData
			success : (response, textStatus, jqXHR) ->
				log "successfuly pushed new location to the server"
			
			error : (jqXHR, textStatus, errorThrown) -> 
				log textStatus
		
		@loadPlayersNearby coords.latitude, coords.longitude
		@loadPlacesNearby coords.latitude, coords.longitude

	# Called when the LocationAPI threw an error
	onPositionError: (error) ->
		if error.code == 1 # permission was denied by user
			log "User denied geolocation"
		else if error.code == 2 # position unavailable
			log "position unavailable"
		else if error.code == 3 # timeout in calculating / finding the position
			log "timeout while calculating position"
		else
			log "unknown error"

	# Pushes the location of a player to the Google Map
	push2Map: (pId, latitude, longitude, uncertainty) ->
		log "playerId: #{pId}, lat: #{latitude}, lng: #{longitude}, uncertainty: #{uncertainty}"

		# Add Marker for the player
		if @markers[pId]
			@markers[pId].setMap null

		pos = new google.maps.LatLng latitude, longitude
		markerOpts = 
			position: pos
			draggable: false
			icon: '/assets/images/player.png'

		marker = new google.maps.Marker markerOpts
		marker.setMap @map
		@markers[pId] = marker
		
		@map.panTo pos

		# Register ClickHandler for this marker
		google.maps.event.addListener marker, 'click', () =>
			player = @players[pId]
			if not player? then return

			username = if player.username then player.username else ""
			team = if player.team.name is "pseudo" then "loner" else player.team.name
			faction = player.team.faction.name

			$("h4", @infoPanel).text "Player #{username}"
			$(".team", @infoPanel).text team
			$(".faction", @infoPanel).text faction
			
			if @selectedPlayer
				props = right: '-320px'
				@infoPanel.animate props
				@selectedPlayer = null
			else
				props = right: '0px'
				@infoPanel.animate props
				@selectedPlayer = marker
		
		# Add circle around marker
		if @circles[pId]
			@circles[pId].setMap null

		u = if uncertainty then uncertainty else 1000

		currentOpts = {}
		for key of ArWars.PlayerPositionManager.circleOpts
    		currentOpts[key] = ArWars.PlayerPositionManager.circleOpts[key]

    	currentOpts.radius = u
    	currentOpts.center = pos

		circle = new google.maps.Circle currentOpts
		circle.setMap @map
		@circles[pId] = circle