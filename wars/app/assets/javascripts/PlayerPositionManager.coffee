# log helper
log = (args...) ->
    console.log.apply console, args if console.log?

# keeps track of position of the players nearby
class ArWars.PlayerPositionManager

	MAX_UNCERTAINTY: 200

	@locationOptions = 
		enableHighAccuracy : false
		timeout : 300
		maximumAge : 50000

	@circleOpts =  
		clickable: false
		fillColor: "RoyalBlue"
		fillOpacity: 0.2
		strokeWidth: 0
		strokeOpacity: 0
	
	playerMarkers: []
	circles: []
	players: []

	constructor: (@infoPanel, @conquerManager, @mapInfoManager) ->
		@map = @mapInfoManager.getMap()
		@bounds = new google.maps.LatLngBounds()

		# load information for the current player
		$.getJSON '/getPlayer', (playerId: window.ArWars.playerId), (responseData) => 
			@players[window.ArWars.playerId] = responseData
			@locationWatchHandle = navigator.geolocation.getCurrentPosition @onFirstPosition, @onPositionError, ArWars.PlayerPositionManager.locationOptions
			@locationWatchHandle = navigator.geolocation.watchPosition @onPositionChange, @onPositionError, ArWars.PlayerPositionManager.locationOptions

	# Removes a player from the map (removes the circle and the marker)
	removeFromMap: (pId) ->
		@playerMarkers[pId] = null
		@circles[pId] = null

	loadPlayersNearby: (lat, lng) -> 
		$.ajax
			url : "/mapinfo/playersNearby"
			type : "get"
			data : 
				lat: lat
				lng: lng
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
			radius:		150
			
		service = new google.maps.places.PlacesService(@map)
		service.nearbySearch(request, @onNearbySearchResult)

	onNearbySearchResult: (results, status, pagination) =>
		if status is google.maps.places.PlacesServiceStatus.OK
			@mapInfoManager.createUnconqueredMarker(i) for i in results
		
		if pagination.hasNextPage
    		pagination.nextPage()
    		
		for k,v of @mapInfoManager.unconqueredPlaceMarkers
			@bounds.extend v.position
		@map.fitBounds @bounds
		
	# Called when LocationAPI detects the player location for the first time
	onFirstPosition: (location) =>
		latitude = location.coords.latitude
		longitude = location.coords.longitude
		pos = new google.maps.LatLng latitude, longitude
		@bounds.extend pos
		@map.fitBounds @bounds
		@onPositionChange location
	
	# Called when LocationAPI detects a location change of the current player
	onPositionChange: (location) =>
		coords = location.coords
		@push2Map window.ArWars.playerId, coords.latitude, coords.longitude, coords.accuracy

		if not coords.latitude?
			return

		if coords.accuracy >= @MAX_UNCERTAINTY
			player = @players[window.ArWars.playerId]

			latitude = player.team.lat
			longitude = player.team.lng

			@loadPlacesNearby latitude, longitude
			@loadPlayersNearby latitude, longitude
			return

		else 
			@loadPlacesNearby coords.latitude, coords.longitude
			@loadPlayersNearby coords.latitude, coords.longitude
		
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

	# Called when the LocationAPI threw an error
	onPositionError: (error) =>
		if error.code == 1 # permission was denied by user
			@notify 'Location Error', 'To play ARWars it is strongly recommended that you switch on the location features in your browsers preferences.', 'error'
		else if error.code == 0 or error.code == 2
			@notify 'Location Error', 'Currently, there is no position data available with your device.', 'info'
		else
			console.log "geolocation error: timeout because the used device is static/not moving - error code: " + error.code

	# Pushes the location of a player to the Google Map
	push2Map: (pId, latitude, longitude, uncertainty) ->
		if not pId? then return

		if uncertainty >= @MAX_UNCERTAINTY and pId is window.ArWars.playerId
			# was there a notification in the last 30 seconds? then don't show it again
			if @lastUnprecisePositionNotification?
				timeDifference = new Date()-@lastUnprecisePositionNotification
				if (timeDifference)/1000 <= 30
					return

			@lastUnprecisePositionNotification = new Date()
			@notify 'Unprecise location', 'The precision of your location could not be determined in a precise manner. You could try to activate GPS or go outside.', 'error'
	
			return

		# Add Marker for the player
		if @playerMarkers[pId]
			@playerMarkers[pId].setMap null

		pos = new google.maps.LatLng latitude, longitude
		markerOpts = 
			position: pos
			draggable: false
			icon: '/assets/images/player.png'
			zIndex: google.maps.Marker.MAX_ZINDEX

		marker = new google.maps.Marker markerOpts
		marker.setMap @map
		@playerMarkers[pId] = marker

		player = @players[pId]
		if not player?
			$.getJSON '/getPlayer', (playerId: pId), (responseData) => 
				@players[pId] = responseData
	
		# Register ClickHandler for this marker
		google.maps.event.addListener marker, 'click', () =>
			player = @players[pId]
			
			if not player?
				return

			username = if player.username then player.username else ""
			team = if player.team.name is "pseudo" then "loner" else player.team.name
			faction = player.faction.name

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

		currentOpts = {}
		for key of ArWars.PlayerPositionManager.circleOpts
    		currentOpts[key] = ArWars.PlayerPositionManager.circleOpts[key]

    	currentOpts.radius = uncertainty
    	currentOpts.center = pos

		circle = new google.maps.Circle currentOpts
		circle.setMap @map
		@circles[pId] = circle

	notify: (title, text, type) ->
		$.pnotify
			title: title
			text: text
			type: type