class ArWars.DeleteMemberConfirmationModal
	
	# needed to have access to the class as well as the queried object
	# in show()
	`var that`

	constructor: () ->
		@modal = "#deleteMemberConfirmationModal";
		$("button[name=ok]", @modal).bind 'click', this.accept
		$("button[name=cancel]", @modal).bind 'click', this.cancel
		$("a[name=playerRemove]", "#teamMembers").bind 'click', this.show
		`that = this`
	
	show: () ->
		that.username = $(this).attr "player"
		$("#memberToDelete").html that.username
		
		$("#deleteMemberConfirmationModal").modal
			backdrop: "static"
			keyboard: false 
		return false
		
	accept: () =>
		$("#deleteMemberConfirmationModal").modal 'hide'
		parent.location="team/deleteMember?member="+@username
		return false
	
	cancel: () =>
		$("#deleteMemberConfirmationModal").modal 'hide'
		return false