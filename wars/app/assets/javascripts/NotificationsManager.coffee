class ArWars.NotificationsManager

	constructor: (@conquerManager, @mapInfoManger) ->
		@notificationNode = $("#notifications .accordion-inner")
		@notificationTable = $("#notificationsTable")
		$("#clearNotifications").bind 'click', @clearNotifications
		$("#notificationsTab").bind 'click', @onNotificationsTabOpened
		
	showUndeliveredNotifications: () ->
		
		params =
			numberOfFullNotifications: 3
			
		$.getJSON 'notifications/getUndelivered', params, (responseData) =>
			for notification in responseData.notifications
				switch notification.messageType 
					when "ConqueringInvitation" 
						@notifyConqueringInvitation notification

					when "ParticipantJoinedConquer" 
						@notifyParticipantJoined notification

					when "ConquerPossible"
						@notifyConquerPossible notification
				
					when "OtherNotification"
						@notify notification.title, notification.message, notification.type
			
			if responseData.othersNumber > 0			
				num = responseData.othersNumber
				@notify 'Notifications', 'You have <a id="notificationsTabLink" name=\"notificationsTabLink\"> '+num+' other not read notifications.</a>', 'info'
				$("#notificationsTabLink").bind 'click', @openNotificationsTab
				
	notificationsMarkAsRead: () ->
		$.ajax
			url : "/notifications/markAllUndeliveredAsRead"
				
	openNotificationsTab: () =>
		# TODO: here the tab should acctually toggle
		$("a[href='#notifications']").parent().next().collapse('show')
		@onNotificationsTabOpened
		
	onNotificationsTabOpened: () =>
		@notificationsMarkAsRead()
		@reloadNotifications()

	reloadNotifications: () =>

		params = 
			offset: 0
			count: 100

		$.getJSON 'notifications/getHistory', params, (responseData) =>
			if responseData.length is 0
				 @notificationNode.html "You don't have any notifications"
				 return
			@notificationTable.html ''
			@notificationTable.append "<tbody>"
			$.each responseData, (index, notification) =>
				switch notification.messageType 
					when 'ConqueringInvitation'
						@renderConqueringInvitation notification
					when 'ParticipantJoinedConquer'
						@renderParticipantJoinedConquer notification
					when 'ConquerPossible'
						@renderConquerPossible notification
					when 'OtherNotification'
						@renderOtherNotification notification
			@notificationTable.append "</tbody>"
			
	clearNotifications: () =>
		$.ajax
			url : "/notifications/deletePlayersNotifications"
			success : (response, textStatus, jqXHR) =>
				@reloadNotifications()
			
#-----------	ARCHIVE RENDERING	-------------------

	renderConqueringInvitation: (notification) ->
		aId = notification.conqueringAttemptId
		@renderTableRow notification.time, "You are invited to <a name=\"joinConquer-#{aId}\">join the conquer</a> of #{notification.placeName}. It was initiated by #{notification.initiatorName}"
		$("a[name='joinConquer-#{aId}']").click () =>
			@conquerManager.joinConquer aId

	renderParticipantJoinedConquer: (notification) ->
		@renderTableRow notification.time, "#{notification.participantName} joined your conquering attempt."

	renderConquerPossible: (notification) ->
		aId = notification.conqueringAttemptId
		@renderTableRow notification.time, "Your conquering attempt for #{notification.placeName} is now possible. <a name=\"conquer-#{aId}\">Conduct it immediately</a>"
		$("a[name='conquer-#{aId}']").click() =>
			@conquerManager.conquer aId
			
	renderOtherNotification: (notification) ->
		@renderTableRow notification.time, notification.message
		
	renderTableRow: (time, content) =>
		@notificationTable.append "<tr><td>" + time + "</td><td>" + content + "</td></tr>"
		
#------------	LIVE RENDERING	(bubbles) ---------------------

	notifyConquerInvitation: (invitation) ->
		noticeNode = $ "#conqueringInvitationNotice"
		$(noticeNode).find("#place").text invitation.placeName
		$(noticeNode).find("#invitator").text invitation.initiatorName

		# Jump to fighting place
		$(noticeNode).find("#place").click () => 
			newLocation = new google.maps.LatLng data.lat, data.lng
			@mapInfoManger.getMap().panTo newLocation
	
		$.pnotify
			text: $(noticeNode).html()
			title: 'Conquering Invitation'
			insert_brs: false
			
		# Click on join conquer button
		$("button[name='join']").click () =>
			@conquerManager.joinConquer invitation.conqueringAttemptId
			
	notifyConquerParticipantJoined: (data) ->
		@notify 'Participant joined', "#{data.participantName} joined conquer", 'info'
		
	notifyConquerPossible: (data) ->
		@notify 'Conquer is now possible', 'You meet all requirements to conquer the place. <a name="conquerNow">Click here to conduct it.</a>', 'success' 
		$("a[name='conquerNow']").click () =>
			@conquerManager.conquer data.conqueringAttemptId
			
	notifyConquerCancelled: (data) ->
		@notifyAndStore 'Canceled Conquer', 'The conquering attempt was canceled', 'success'
		
#	its a question whether this function will stay here
#	notifications should rather be generated and stored directly at server side
# 	not by javascript
	notifyAndStore: (title, text, type) ->
		data = 
			title: title
			message: text
			type: type

		$.ajax
			url : "/notifications/addHistoryEntry"
			data : data
			
		@notify title, text, type
			
	notify: (title, text, type) ->
		$.pnotify
			title: title
			text: text
			type: type