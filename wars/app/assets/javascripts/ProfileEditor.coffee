class ArWars.ProfileEditor
	
	# needed to have access to the class instance pointer from the event handlers
	`var that`
	
	constructor: () ->
		`that = this`
		$("#saveButton").bind 'click', @updateProfile
		
		$("#inputFirstname").bind 'change keyup', @validateMandatoryWord
		$("#inputLastname").bind 'change keyup', @validateMandatoryWord
		$("#inputEmail").bind 'change keyup', @validateEmail
		$("#inputHometown").bind 'change keyup', @validateOptionalWord
		$("#inputBirthday").bind 'change keyup', @validateDate
		
		@validateAll()

	validateAll: () ->
		@validateMandatoryWord.call($("#inputFirstname")) and
		@validateMandatoryWord.call($("#inputLastname")) and
		@validateEmail.call($("#inputEmail")) and
		@validateOptionalWord.call($("#inputHometown")) and
		@validateDate.call($("#inputBirthday"))
		
	toggleHint: (result, inputField) ->
		if result is true
			$(inputField).parent().parent().removeClass("error")
			$(inputField).next().addClass("hide")
		else
			$(inputField).parent().parent().addClass("error")
			$(inputField).next().removeClass("hide")
		
	validateEmail: () ->
		email = $(this).val()
		re = new RegExp "^([a-zA-Z0-9])+([a-zA-Z0-9\\._-])*@([a-zA-Z0-9_-])+([a-zA-Z0-9\\._-]+)+$"
		result = re.test email 
		that.toggleHint result, this
		result
		
	validateDate: () ->
		date = $(this).val()
		re = new RegExp "^[0-1]\\d\\.\\d{2}\\.[1-2]\\d{3}$"
		test = re.test date
		result = test or (date.length is 0)
		if result is true
			$(this).parent().parent().removeClass("error")
		else
			$(this).parent().parent().addClass("error")
		result
		
	validateMandatoryWord: () ->
		word = $(this).val()
		re = new RegExp "^[a-zA-Z]+$"
		result = re.test word
		that.toggleHint result, this
		result

	validateOptionalWord: () ->
		word = $(this).val()
		re = new RegExp "^[a-zA-Z]*$"
		result = re.test word
		that.toggleHint result, this
		result
		
	updateProfile: () =>
		
		if @validateAll() is false
			return false
		
		params:
			firstname: $("#inputFirstname").val()
			lastname: $("#inputLastname").val()
			email: $("#inputEmail").val()
			hometown: $("#inputHometown").val() 
			birthday: $("#inputBirthday").val()
		
		$.ajax
			url: 'profile/changeProfile'
			success : (response, textStatus, jqXHR) =>
				parent.location = '/'