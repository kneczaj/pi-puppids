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

class ArWars.PlayerPositionManager
	
	@markers: []
	@circles: []
	@players: []
	defaultLocationAPIOptions = 
		enableHighAccuracy : true
		timeout : 3000
		maximumAge : 500
	@locationWatchHandle: null
	@map: null
	@mapNode: null
	@selectedPlayer: null
	@infoPanel: null

	mapOptions = 
		center : new google.maps.LatLng 48.133, 11.566
		zoom : 11
		mapTypeId : google.maps.MapTypeId.ROADMAP

	constructor: (@mapNode, @infoPanel) ->
		@map = new google.maps.Map @mapNode, mapOptions
		@markers = []
		@circles = []
		@players = []
		@locationWatchHandle = navigator.geolocation.watchPosition @onPositionChange, @onPositionError, defaultLocationAPIOptions

	###
	Removes a player from the map (removes the circle and the marker)
	###
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
				console.log textStatus

	###
	Called when LocationAPI detects a location change of the current player
	###
	onPositionChange: (location) =>
		coords = location.coords
		@push2Map window.ArWars.playerId, coords.latitude, coords.longitude, coords.accuracy
		serializedData = 
			lat : coords.latitude
			lng : coords.longitude
			uncertainty : coords.accuracy
			speed : coords.speed
			timestamp : location.timestamp

		$.ajax
			url : "/updateLocation"
			type : "get"
			data : serializedData
			success : (response, textStatus, jqXHR) ->
				console.log "successfuly pushed new location to the server"
			
			error : (jqXHR, textStatus, errorThrown) -> 
				console.log textStatus
		
		@loadPlayersNearby coords.latitude, coords.longitude

	###
	Called when the LocationAPI threw an error
	###
	onPositionError: (error) ->
		if error.code == 1 # permission was denied by user
			console.log "User denied geolocation"
		else if error.code == 2 # position unavailable
			console.log "position unavailable"
		else if error.code == 3 # timeout in calculating / finding the position
			console.log "timeout while calculating position"
		else
			console.log "unknown error"

	###
	Pushes the location of a player to the Google Map
	###
	push2Map: (pId, latitude, longitude, uncertainty) ->
		console.log "playerId: #{pId}, lat: #{latitude}, lng: #{longitude}, uncertainty: #{uncertainty}"
		pos = new google.maps.LatLng latitude, longitude
		markerOpts = 
			position: pos
			draggable: false
			icon: '/assets/images/player.png'

		if @markers[pId]
			@markers[pId].setMap null

		marker = new google.maps.Marker markerOpts
		marker.setMap @map
		@markers[pId] = marker

		google.maps.event.addListener marker, 'click', () =>
			player = @players[pId].email
			$("h4", @infoPanel).text "Player #{player}"

			if @selectedPlayer
				props = right: '-320px'
				@infoPanel.animate props
				@selectedPlayer = null
			else
				props = right: '0px'
				@infoPanel.animate props
				@selectedPlayer = marker

		u = if uncertainty then uncertainty else 1000

		circleOpts = 
			center: pos
			clickable: false
			fillColor: "RoyalBlue"
			fillOpacity: 0.1
			radius: u
			strokeColor: "RoyalBlue"
			strokeWidth: 0.1

		if @circles[pId]
			@circles[pId].setMap null

		circle = new google.maps.Circle circleOpts
		circle.setMap @map
		@circles[pId] = circle

$(document).ready ->
	playerPositionManager = new window.ArWars.PlayerPositionManager $(window.ArWars.mapSelector)[0], $('#playerDetails')
	webSocket = new window.ArWars.CustomWebSocket playerPositionManager
	webSocket.establishWebSocket window.ArWars.webSocketURL