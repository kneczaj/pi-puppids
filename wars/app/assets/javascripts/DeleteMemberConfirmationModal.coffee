class ArWars.DeleteMemberConfirmationModal
	
	# needed to have access to the class as well as the queried object
	# in show()
	`var that`

	constructor: () ->
		@modal = "#deleteMember_confirmationModal";
		$("button[name=ok]", @modal).bind 'click', this.accept
		$("button[name=cancel]", @modal).bind 'click', this.cancel
		$("a[name=playerRemove]", "#teamMembers").bind 'click', this.show
		`that = this`
	
	show: () ->
		that.username = $(this).attr "player"
		$("#memberToDelete").html that.username
		
		$(that.modal).modal
			backdrop: "static"
			keyboard: false 
		return false
		
	accept: () =>
		$(@modal).modal 'hide'
		parent.location="team/deleteMember?member="+@username
		return false
	
	cancel: () =>
		$(@modal).modal 'hide'
		return false