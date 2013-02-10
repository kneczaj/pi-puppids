# log helper
log = (args...) ->
    console.log.apply console, args if console.log?

$(document).ready ->

	rejectInvitationClickHandler = ->
		$("#sentInvitationsModal").modal 'hide'
		$("#chooseFactionModal").modal
			backdrop: "static"
			keyboard: false
		return false
		
	acceptInvitationClickHandler = ->
		token = $("input[name=invitationId]:checked").val()
		parent.location="/acceptInvitation/" + token
		return false
	
	$("button[name=accept]", "#sentInvitationsModal").bind 'click', acceptInvitationClickHandler
	$("button[name=reject]", "#sentInvitationsModal").bind 'click', rejectInvitationClickHandler	
	if window.ArWars.showSentInvitations is true 
		$("#sentInvitationsModal").modal
			backdrop: "static"
			keyboard: false
		

	showError = (message) ->
		hideError()
		$("#chooseFactionModal .modal-body").prepend "<div class='alert alert-error'>#{message}</div>"

	hideError = ->
		alertNode = $("#chooseFactionModal .modalBody .alert")
		if alertNode?
			alertNode.remove()

	factionClickHandler = =>
		factionId = $("input[name=factionId]:checked").val()
		cityId = $("select[name=cityId]").val()

		data = 
			factionId: factionId
			cityId: cityId

		$.ajax
			url : "/team/joinFactionAndCity"
			data : data
			success : (response, textStatus, jqXHR) =>
				if response is 'ok'
					hideError()
					$("#chooseFactionModal").modal('hide')
					loadContents()
				else
					showError 'Please revise your data'
			
			error : (jqXHR, textStatus, errorThrown) ->
				showError "There was an server error, please try again later"

		return false
		
	factionBackToInvitationsClickHandler = ->
		$("#sentInvitationsModal").modal
			backdrop: "static"
			keyboard: false
		$("#chooseFactionModal").modal 'hide'
		return false
		
	$("button[name=save]", "#chooseFactionModal").bind 'click', @factionClickHandler
	$("button[name=back]", "#chooseFactionModal").bind 'click', factionBackToInvitationsClickHandler
	$("#chooseFactionModal form").submit factionClickHandler	

	setupSliders = ->
		$("#gruntBuildSlider").slider
			range: "min"
			value: 0
			min: 0
			max: 10
			slide: (event, ui) ->
				$("#gruntBuildAmount").val ui.value

		$("#gruntBuildAmount").val $("#gruntBuildSlider").slider("value")
		$("#infantryBuildSlider").slider
			range: "min"
			value: 0
			min: 0
			max: 10
			slide: (event, ui) ->
				$("#infantryBuildAmount").val ui.value

		$("#infantryBuildAmount").val $("#infantryBuildSlider").slider("value")
		$("#gruntDeploySlider").slider
			range: "min"
			value: 0
			min: 0
			max: 0
			slide: (event, ui) ->
				$("#gruntDeployAmount").val ui.value

		$("#gruntDeployAmount").val $("#gruntDeploySlider").slider("value")
		$("#infantryDeploySlider").slider
			range: "min"
			value: 0
			min: 0
			max: 0
			slide: (event, ui) ->
				$("#infantryDeployAmount").val ui.value

		$("#infantryDeployAmount").val $("#infantryDeploySlider").slider("value")

	loadContents = =>
		conquerManager = new window.ArWars.ConquerManager()
		layoutResizer = new window.ArWars.LayoutResizer $("#map_canvas"), $("#map_container_mobile"), $("#map_container_desktop"), $("#playerDetails")
		setupSliders()
		
		mapInfoManger = new window.ArWars.MapInfoManager $(window.ArWars.mapSelector)[0], conquerManager
		mapInfoManger.loadConqueredPlaces()
		
		playerPositionManager = new window.ArWars.PlayerPositionManager $('#playerDetails'), conquerManager, mapInfoManger
		deleteMemberConfirmationModal = new window.ArWars.DeleteMemberConfirmationModal
		notificationsManager = new window.ArWars.NotificationsManager conquerManager, mapInfoManger
		
		conquerManager.setPlayerPositionManager playerPositionManager
		conquerManager.setNotificationsManager notificationsManager
		
		sidebar = new window.ArWars.SideBar conquerManager, mapInfoManger
		sidebar.loadResourceSourcesOfPlayer()
		sidebar.loadResourcesOfPlayer()
		sidebar.loadResourcesOfTeam()
		sidebar.loadUnitsOfPlayer()
		notificationsManager.showUndeliveredNotifications()
		
		$("button#btnDeploy").click () => 
			sidebar.deployUnitsClickHandler()
			return false
			
		$("button#btnBuild").click () => 
			sidebar.buildUnitsClickHandler()
			return false

		webSocket = new window.ArWars.WebSocketManager playerPositionManager, conquerManager, notificationsManager
		webSocket.establishWebSocket window.ArWars.webSocketURL

	if window.ArWars.factionHasToBeChosen is true 
		$("#chooseFactionModal").modal
			backdrop: "static"
			keyboard: false
	else
		loadContents()
