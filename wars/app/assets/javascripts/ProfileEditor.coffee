$(document).ready ->
	
	teamFields =
			'teamname':	$("#inputTeamname")
			
	teamEditor = new window.ArWars.FormValidator(teamFields, $("#saveTeamButton"), '/team/changeTeamData');
	
	profileFields =
			'firstname':$("#inputFirstname"),
			'lastname':	$("#inputLastname"),
			'email':	$("#inputEmail"),
			'hometown':	$("#inputHometown"),
			
	profileEditor = new window.ArWars.FormValidator(profileFields, $("#saveButton"), '/profile/changeProfile');