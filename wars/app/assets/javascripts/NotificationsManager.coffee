class ArWars.NotificationsManager

	constructor: (@conquerManager, @playerPositionManager) ->
		@notificationNode = $("#notifications .accordion-inner")
		@notificationTable = $("#notificationsTable")
		
	showUndeliveredNotifications: () ->
		
		params =
			numberOfFullNotifications: 3
			
		$.getJSON 'notifications/getHistory', params, (responseData) =>
			for notification in responseData.notifications
				switch notification.messageType 
					when "ConqueringInvitation" 
						@notifyConqueringInvitation data

					when "ParticipantJoinedConquer" 
						@notifyParticipantJoined data

					when "ConquerPossible"
						@notifyConquerPossible data
				
					when "OtherNotification"
						@notify data.title, data.message, data.type
						
			num = responseData.undeliveredNumber
			@notify 'Others', 'You have <a name=\"notificationsTabLink\">#{num} other undelivered notifications.</a>', 'info'
			#TODO: the link should open notifications tab and mark all notifications as read

	loadNotifications: () ->

		params = 
			offset: 0
			count: 100

		$.getJSON 'notifications/getHistory', params, (responseData) =>
			if responseData.isEmpty
				 @notificationNode.html "You don't have any notifications"
				 return
			@notificationTable.html = ''
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
		
	renderTableRow: (time, content) ->
		@notificationTable.append "<tr><td>" + time + "</td><td>" + content + "</td></tr>"
		
#------------	LIVE RENDERING	---------------------

	notifyConquerInvitation: (invitation) ->
		noticeNode = $ "#conqueringInvitationNotice"
		$(noticeNode).find("#place").text invitation.placeName
		$(noticeNode).find("#invitator").text invitation.initiatorName

		# Jump to fighting place
		$(noticeNode).find("#place").click () => 
			newLocation = new google.maps.LatLng data.lat, data.lng
			@playerPositionManager.getMap().panTo newLocation
	
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