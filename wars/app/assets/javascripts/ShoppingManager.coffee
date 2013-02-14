class ArWars.ShoppingManager
		
	showShoppingConfirmation: (shoppingList) ->
		
	# needed to have access to the class as well as the queried object
	# in show()
	`var that`

	constructor: () ->
		@modal = "#shopping_confirmationModal";
		$("button[name=ok]", @modal).bind 'click', this.accept
		$("button[name=cancel]", @modal).bind 'click', this.cancel
		$("button[name=ok]", "#shopping_noticeModal").bind 'click', this.closePaymentConfirmation
		`that = this`
	
	showShoppingList: (shoppingList) ->
		
		if shoppingList.shoppingHeader isnt undefined
			$("#shoppingMessage").html shoppingList.shoppingHeader
		
		listHtml = "";
		for item of shoppingList.content.positions
			listHtml += "<tr><td>" + item + ":</td><td>" + shoppingList.content.positions[item] + " &euro;</td></tr>"
			
		$("#shoppingList").html listHtml
		that.content = shoppingList
			
		$(@modal).modal
			backdrop: "static"
			keyboard: false
		 
		return false
		
	accept: () =>
		$(@modal).modal 'hide'
		$.ajax
			url : "/buy"
			type : "POST"
			data : JSON.stringify(@content)
			contentType: 'application/json; charset=utf-8',
			dataType: 'html',
			async: false,
			success : (response, textStatus, jqXHR) =>
				@showPaymentConfirmation()
				
		return false
	
	cancel: () =>
		$(@modal).modal 'hide'
		return false
		
	showPaymentConfirmation: () ->
		$("#shopping_noticeModal").modal
			backdrop: "static"
			keyboard: false
		return false
		
	closePaymentConfirmation: () ->
		$("#shopping_noticeModal").modal 'hide'
		return false