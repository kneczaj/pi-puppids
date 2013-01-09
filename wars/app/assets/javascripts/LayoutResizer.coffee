# automatically resizes the layout of the page
class ArWars.LayoutResizer
	constructor: (@mapNode, @mobileNode, @desktopNode, @playerDetailsNode) ->
		$(window).resize(@resize).resize()

	resize: () =>
		offsetTop = 0

		if $(window).width() < 768
			@mapNode.appendTo @mobileNode
			@playerDetailsNode.appendTo @mobileNode
			offsetTop = 200

		else
			@mapNode.appendTo @desktopNode
			@playerDetailsNode.appendTo @desktopNode
			offsetTop = 180

		height = $(window).height()
		newHeight = height-offsetTop
		@mapNode.css 'height', newHeight