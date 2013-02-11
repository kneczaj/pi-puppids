class ArWars.MapInfoManager
	
	conqueredPlaces: []
	conqueredPlaceMarkers: []
	unconqueredPlaceMarkers: []
	
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
		
	@mcOptions =
		maxZoom: 16
		styles: [
			url: "./assets/images/cluster_orange.png",
			height: 56,
			width: 56
			]

	constructor: (@mapNode, @conquerManager) ->
		@map = new google.maps.Map @mapNode, ArWars.MapInfoManager.mapOptions
		@mc = new MarkerClusterer @map, [], ArWars.MapInfoManager.mcOptions
		@infowindow = new google.maps.InfoWindow(content: "Loading...")

		$.getJSON '/getPlayer', (playerId: window.ArWars.playerId), (responseData) => 
			@player = responseData
			
	getMap: () ->
		@map
	
	loadConqueredPlaces: () ->
		$.getJSON "/mapinfo/getConqueredPlaces", (data) =>
			$.each data, (key, val) =>
				@conqueredPlaces[key] = val
				@createConqueredMarker key, val
					
	createConqueredMarker: (pid, place) ->
		placeLoc = new google.maps.LatLng place.lat, place.lng
		iconUrl = "/assets/images/resources/" + place.resource.toLowerCase() + "_marker_" + place.faction + ".png"
		
		markerOpts = 
			position: placeLoc
			icon: iconUrl
		
		marker = new google.maps.Marker markerOpts
		@mc.addMarker marker
		@conqueredPlaceMarkers[pid] = marker
		
		google.maps.event.addListener marker, "click", () =>
			@setInfowindow pid, place, marker
			
	setInfowindow: (pid, place, marker) =>
		typeCap = (place.type.split('_').map (word) -> word[0].toUpperCase() + word[1..-1].toLowerCase()).join ' '
		buttonClass = ""
		switch place.faction
		  when "red"
		    buttonClass = "danger"
		  when "blue"
		    buttonClass = "primary"
		    
		content = "<span class=\"infowindowTitle\">#{place.name}</span><br/>Type: #{typeCap}<br/>Resources: #{place.resAmount} <img src=\"/assets/images/resources/#{place.resource.toLowerCase()}_#{place.faction}.png\"><br/>Units: #{place.units}<br/>Conquered by: #{place.team} (#{place.faction})"
		
		if @player.team.name is place.team 
			content += "<br/><br/><button class=\"btn btn-block btn-#{buttonClass}\" type=\"button\" name=\"btnDeploy\" placeId=\"#{pid}\">Deploy units</button>"
		else if not (@player.faction.name is place.faction) 
			content += "<br/><br/><button class=\"btn btn-block btn-#{buttonClass}\" type=\"button\" name=\"btnConquer\" placeId=\"#{pid}\" reference=\"#{place.reference}\">Conquer</button>"
		
		@infowindow.setContent content
		@infowindow.open @map, marker
		$("button[placeId=#{pid}][name=\"btnDeploy\"]").click () -> 
				$("#deployAt").val pid 
				$("#deployUnitsModal").modal 'show'

		$("button[placeId='#{pid}'][name=\"btnConquer\"]").click () =>
			@conquerManager.conquer pid, place.reference
			
	createUnconqueredMarker: (place) ->
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
			position: place.geometry.location
			icon: iconUrl
			zIndex: 1
		
		marker = new google.maps.Marker markerOpts
		@mc.addMarker marker
		@unconqueredPlaceMarkers[place.id] = marker

		google.maps.event.addListener marker, "click", () =>
			type = (place.types[0].split('_').map (word) -> word[0].toUpperCase() + word[1..-1].toLowerCase()).join ' '
			resourceIcon = marker.icon.replace /marker/, "orange"
			content = "<span class=\"infowindowTitle\">#{place.name}</span><br/>Type: #{type}<br/>Resource: <img src=\"#{resourceIcon}\"><br/><br/><button class=\"btn btn-block btn-warning\" type=\"button\" placeId=\"#{place.id}\">Conquer</button>"
			@infowindow.setContent content
			@infowindow.open @map, marker
			$("button[placeId=#{place.id}]").click () => 
				@conquerManager.initiateConquer place.id, place.reference
		
	notify: (title, text, type) ->
		$.pnotify
			title: title
			text: text
			type: type
