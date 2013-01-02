# log helper
log = (args...) ->
    console.log.apply console, args if console.log?

class ArWars.LayoutResizer
	constructor: (@mapNode, @mobileNode, @desktopNode, @playerDetailsNode) ->
		$(window).resize(@resize).resize()

	resize: () =>
		offsetTop = 0

		if $(window).width() < 768
			@mapNode.appendTo @mobileNode
			@playerDetailsNode.appendTo @mobileNode
			offsetTop = 200

		else
			@mapNode.appendTo @desktopNode
			@playerDetailsNode.appendTo @desktopNode
			offsetTop = 180

		height = $(window).height()
		newHeight = height-offsetTop
		@mapNode.css 'height', newHeight

# Handels WebSocket communication
class ArWars.CustomWebSocket
	@wsInstance: null
	@socket: null

	constructor: (@playerPositionManager) ->
		@wsInstance = if window['MozWebSocket'] then MozWebSocket else WebSocket

	receiveEvent: (event) => 
		data = JSON.parse(event.data)
		@playerPositionManager.push2Map data.id, data.latitude, data.longitude, data.accuracy

	establishWebSocket: (url) ->
		socket = new @wsInstance url 
		socket.onmessage = @receiveEvent

# keeps track of position of the players nearby
class ArWars.PlayerPositionManager

	@mapOptions = 
		center : new google.maps.LatLng 48.133, 11.566
		zoom : 11
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
			radius: 	1000
			types: 		["atm", "bakery", "school", "church", "movie_theater", "pharmacy", "train_station"]

		service = new google.maps.places.PlacesService(@map)
		service.search(request, @callback)

	callback: (results, status, pagination) =>
		if status is google.maps.places.PlacesServiceStatus.OK
			console.log results
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

$(document).ready ->
	showError = (message) ->
		hideError()
		$("#chooseFactionModal .modal-body").prepend "<div class='alert alert-error'>#{message}</div>"

	hideError = ->
		alertNode = $("#chooseFactionModal .modalBody .alert")
		if alertNode?
			alertNode.remove()

	clickHandler = ->
		factionId = $("input[name=factionId]:checked").val()
		cityId = $("select[name=cityId]").val()

		data = 
			factionId: factionId
			cityId: cityId

		$.ajax
			url : "/team/joinFactionAndCity"
			data : data
			success : (response, textStatus, jqXHR) ->
				if response is 'ok'
					hideError()
					$("#chooseFactionModal").modal('hide')
				else
					showError 'Please revise your data'
			
			error : (jqXHR, textStatus, errorThrown) ->
				showError "There was an server error, please try again later"

		return false

	$("button[name=save]", "#chooseFactionModal").bind 'click', clickHandler
	$("#chooseFactionModal form").submit clickHandler	

	if window.ArWars.factionHasToBeChosen is true 
		$("#chooseFactionModal").modal 'show'

	layoutResizer = new window.ArWars.LayoutResizer $("#map_canvas"), $("#map_container_mobile"), $("#map_container_desktop"), $("#playerDetails")
	playerPositionManager = new window.ArWars.PlayerPositionManager $(window.ArWars.mapSelector)[0], $('#playerDetails')
	webSocket = new window.ArWars.CustomWebSocket playerPositionManager
	webSocket.establishWebSocket window.ArWars.webSocketURL