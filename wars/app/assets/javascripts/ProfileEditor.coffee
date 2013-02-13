$(document).ready ->
	
	shoppingManager = new window.ArWars.ShoppingManager
	
	teamFields =
			'teamname':	$("#inputTeamname")
			
	teamEditor = new window.ArWars.FormValidator(teamFields, $("#saveTeamButton"), '/team/changeTeamData', shoppingManager);
	
	profileFields =
			'firstname':$("#inputFirstname"),
			'lastname':	$("#inputLastname"),
			'email':	$("#inputEmail"),
			'hometown':	$("#inputHometown"),
			'username': $("#inputUsername")
			
	profileEditor = new window.ArWars.FormValidator(profileFields, $("#saveButton"), '/profile/changeProfile', shoppingManager);