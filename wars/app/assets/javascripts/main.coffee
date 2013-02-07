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

	factionClickHandler = ->
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
		
	factionBackToInvitationsClickHandler = ->
		$("#sentInvitationsModal").modal
			backdrop: "static"
			keyboard: false
		$("#chooseFactionModal").modal 'hide'
		return false
		
	$("button[name=save]", "#chooseFactionModal").bind 'click', factionClickHandler
	$("button[name=back]", "#chooseFactionModal").bind 'click', factionBackToInvitationsClickHandler
	$("#chooseFactionModal form").submit factionClickHandler	

	if window.ArWars.factionHasToBeChosen is true 
		$("#chooseFactionModal").modal
			backdrop: "static"
			keyboard: false

	
	conquerManager = new window.ArWars.ConquerManager
	layoutResizer = new window.ArWars.LayoutResizer $("#map_canvas"), $("#map_container_mobile"), $("#map_container_desktop"), $("#playerDetails")
	playerPositionManager = new window.ArWars.PlayerPositionManager $(window.ArWars.mapSelector)[0], $('#playerDetails'), conquerManager
	deleteMemberConfirmationModal = new window.ArWars.DeleteMemberConfirmationModal
	
	conquerManager.setPlayerPositionManager playerPositionManager
	
	sidebar = new window.ArWars.SideBar playerPositionManager
	sidebar.loadResourceSourcesOfPlayer()
	sidebar.loadUnitsOfPlayer()
	sidebar.loadNotifications()
	
	$("button#btnBuild").click () => 
		sidebar.buildUnitsClickHandler()
		return false

	webSocket = new window.ArWars.WebSocketManager playerPositionManager, conquerManager
	webSocket.establishWebSocket window.ArWars.webSocketURL