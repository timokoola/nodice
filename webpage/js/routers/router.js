/*global Backbone */
var app = app || {};

(function () {
	'use strict';

	// Todo Router
	// ----------
	var Workspace = Backbone.Router.extend({
		
	});

	app.DieRouter = new Workspace();
	Backbone.history.start();
})();