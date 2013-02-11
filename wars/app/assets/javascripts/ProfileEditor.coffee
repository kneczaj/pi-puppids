class ArWars.ProfileEditor
	
	# needed to have access to the class instance pointer from the event handlers
	`var that`
	
	constructor: () ->
		`that = this`
		$("#saveButton").bind 'click', @updateProfile
		
		@fields =
			'firstname':$("#inputFirstname"),
			'lastname':	$("#inputLastname"),
			'email':	$("#inputEmail"),
			'hometown':	$("#inputHometown"),
			'birthday':	$("#inputBirthday")
			
		@validationMethods =
			'mandatoryWord':@validateMandatoryWord,
			'optionalWord':	@validateOptionalWord
			'email':		@validateEmail
			'date':			@validateDate
			
		@validationAssociations = {}
		
		for key of @fields
			fieldId = @fields[key]
			methodKey = $(fieldId).attr "validation"
			validationMethod = @validationMethods[methodKey] 
			@validationAssociations[key] = validationMethod
			$(fieldId).bind 'change keyup', validationMethod
		
		@validateAll()

	validateAll: () =>
		result = true
		for key of @validationAssociations
			result = result and @validationAssociations[key].call(@fields[key])
		result
		
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
		
		params =
			firstname: @fields.firstname.val()
			lastname: @fields.lastname.val()
			email: @fields.email.val()
			hometown: @fields.hometown.val() 
			birthday: @fields.birthday.val()
		
		$.getJSON '/profile/changeProfile', params, (responseData) =>
				
			# no invalid fields - redirect to the main page
			if responseData.length is 0
				parent.location = '/'
				
			# mark invalid fields 
			for item in responseData
				@toggleHint 0, @fields[item]
					