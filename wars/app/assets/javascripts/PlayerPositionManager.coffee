# log helper
log = (args...) ->
    console.log.apply console, args if console.log?

# keeps track of position of the players nearby
class ArWars.PlayerPositionManager

	MAX_UNCERTAINTY: 200

	@mapOptions = 
		center : new google.maps.LatLng 48.133, 11.566
		zoom : 11
		mapTypeId : google.maps.MapTypeId.ROADMAP
		mapTypeControl: false
		streetViewControl: false
		styles: [
			stylers: [
				invert_lightness: true
			,
				saturation: -80
			]
		,
			featureType: "road"
			elementType: "geometry"
			stylers: [color: "#646464"]
		,
			featureType: "road"
			elementType: "labels.icon"
			stylers: [visibility: "off"]
		,
			featureType: "poi"
			elementType: "labels"
			stylers: [
				color: "#faa732"
			,
				weight: 0.1
			]
		]

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
	
	playerMarkers: []
	circles: []
	players: []
	placeMarkers: []
	
	locationWatchHandle: null
	map: null
	mapNode: null
	selectedPlayer: null
	infoPanel: null
	infowindow: null
	bounds: null

	lastUnprecisePositionNotification: null

	constructor: (@mapNode, @infoPanel, @conquerManager) ->
		@map = new google.maps.Map @mapNode, ArWars.PlayerPositionManager.mapOptions
		@locationWatchHandle = navigator.geolocation.watchPosition @onPositionChange, @onPositionError, ArWars.PlayerPositionManager.locationOptions
		@bounds = new google.maps.LatLngBounds()

	getMap: () ->
		@map

	# Removes a player from the map (removes the circle and the marker)
	removeFromMap: (pId) ->
		@playerMarkers[pId] = null
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
			radius:		150
			
		@infowindow = new google.maps.InfoWindow(content: "Loading...")
		service = new google.maps.places.PlacesService(@map)
		service.nearbySearch(request, @callback)

	callback: (results, status, pagination) =>
		if status is google.maps.places.PlacesServiceStatus.OK
			@createMarker(i) for i in results
		
		if pagination.hasNextPage
    		pagination.nextPage()
    		
		for k,v of @placeMarkers
			@bounds.extend v.position
		@map.fitBounds @bounds

	createMarker: (place) ->
		placeLoc = place.geometry.location
		iconUrl = undefined
		switch place.types[0]
		  when "atm", "bank", "casino", "dentist", "doctor", "electrician", "establishment", "finance", "florist", "insurance_agency", "jewelry_store", "lawyer"
		    iconUrl = "/assets/images/resources/credits_marker.png"
		  when "bakery", "bar", "cafe", "food", "liquor_store", "meal_delivery", "meal_takeaway", "restaurant", "shopping_mall", "store"
		    iconUrl = "/assets/images/resources/food_marker.png"
		  when "book_store", "library", "school", "university"
		    iconUrl = "/assets/images/resources/knowledge_marker.png"
		  when "campground", "cemetery", "church", "city_hall", "courthouse", "embassy", "fire_station", "hindu_temple", "local_government_office", "mosque", "place_of_worship", "police", "stadium", "synagogue", "zoo"
		    iconUrl = "/assets/images/resources/special_marker.png"
		  when "amusement_park", "aquarium", "art_gallery", "beauty_salon", "bowling_alley", "movie_rental", "movie_theater", "moving_company", "museum", "night_club", "park"
		    iconUrl = "/assets/images/resources/cultural_marker.png"
		  when "bicycle_store", "clothing_store", "convenience_store", "department_store", "electronics_store", "funeral_home", "furniture_store", "gas_station", "general_contractor", "grocery_or_supermarket", "gym", "hair_care", "hardware_store", "health", "home_goods_store", "hospital", "laundry", "locksmith", "lodging", "painter", "pet_store", "pharmacy", "physiotherapist", "plumber", "post_office", "real_estate_agency", "roofing_contractor", "rv_park", "shoe_store", "spa", "storage"
		    iconUrl = "/assets/images/resources/material_marker.png"
		  when "bus_station", "car_dealer", "car_rental", "car_repair", "car_wash", "parking", "subway_station", "taxi_stand", "train_station", "travel_agency", "veterinary_care"
		    iconUrl = "/assets/images/resources/transportation_marker.png"
		  else
		    return
		
		markerOpts = 
			map: @map
			position: place.geometry.location
			animation: google.maps.Animation.DROP
			icon: iconUrl
		
		marker = new google.maps.Marker markerOpts
		@placeMarkers[place.id] = marker

		google.maps.event.addListener marker, "click", () =>
			type = place.types[0]
			content = "#{place.name}<br/>#{place.vicinity}<br/>Type:#{type}<br/>Resource: <img src=\"#{marker.icon}\"><br/><br/><button class=\"btn btn-block btn-warning\" type=\"button\" placeId=\"#{place.id}\">Conquer</button>"
			@infowindow.setContent content
			@infowindow.open @map, marker
			$("button[placeId=#{place.id}]").click () => 
				@conquerManager.initiateConquer place.id, place.reference

	
	# Called when LocationAPI detects a location change of the current player
	onPositionChange: (location) =>
		coords = location.coords
		@push2Map window.ArWars.playerId, coords.latitude, coords.longitude, coords.accuracy

		if not coords.latitude?
			return

		if coords.accuracy >= @MAX_UNCERTAINTY
			player = @players[window.ArWars.playerId]

			if not player?
				return

			if not player.team?
				return

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
		if not pId? then return
		log "playerId: #{pId}, lat: #{latitude}, lng: #{longitude}, uncertainty: #{uncertainty}"

		if uncertainty >= @MAX_UNCERTAINTY and pId is window.ArWars.playerId
			# was there a notification in the last 30 seconds? then don't show it again
			if @lastUnprecisePositionNotification?
				timeDifference = new Date()-@lastUnprecisePositionNotification
				if (timeDifference)/1000 <= 30
					return

			@lastUnprecisePositionNotification = new Date()
			$.pnotify
			    title: 'Unprecise location'
			    text: 'The precision of your location could not be determined in a precise manner. You could try to activate GPS or go outside.'
			    type: 'error'
	
			return

		# Add Marker for the player
		if @playerMarkers[pId]
			@playerMarkers[pId].setMap null

		pos = new google.maps.LatLng latitude, longitude
		markerOpts = 
			position: pos
			draggable: false
			icon: '/assets/images/player.png'

		marker = new google.maps.Marker markerOpts
		marker.setMap @map
		@playerMarkers[pId] = marker
		@bounds.extend pos
		@map.fitBounds @bounds
	
		# Register ClickHandler for this marker
		google.maps.event.addListener marker, 'click', () =>
			player = @players[pId]
			if not player? then return

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