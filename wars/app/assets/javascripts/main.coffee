# log helper
log = (args...) ->
    console.log.apply console, args if console.log?

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
	webSocket = new window.ArWars.WebSocketManager playerPositionManager
	webSocket.establishWebSocket window.ArWars.webSocketURL