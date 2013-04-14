var app = app || {};


(function ($) {
	'use strict';

	app.DieView = Backbone.View.extend({

		events: {
			"click .minus": "removeDie",
			"click h1": "roll",
			"click .pencil": "edit",
		},

		template: _.template($("#die-template").html()),
		edit_template: _.template($("#die-edit-template").html()),
		initialize: function () {
			this.listenTo(this.model, 'change', this.render);
			this.listenTo(this.model, 'destroy', this.remove);
		},

		render: function () {
			var grhm = {};
			grhm.description = this.model.description();
			grhm.title = this.model.get("title");
			grhm.lastRoll = this.model.get("lastRoll") > 0 ? this.model.get("lastRoll") : "-";
			this.$el.html(this.template(grhm));
			return this;
		}, 

		roll: function (e) {
			this.model.roll();
		},
		removeDie: function (e) {
			e.preventDefault();
			this.model.destroy();
		}, 
		edit: function (e) {
			var dial = $("#dialogs").html(this.edit_template(this.model));
			$("#baseModal").foundation("reveal", "open");
		}
		

	});
})(jQuery);