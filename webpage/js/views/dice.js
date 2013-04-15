var app = app || {};


(function ($) {
	'use strict';

	app.DieView = Backbone.View.extend({

		events: {
			"click .minus": "removeDie",
			"click h1": "roll",
			"click p.text-center": "edit",
		},

		template: _.template($("#die-template").html()),
		edit_template: _.template($("#die-edit-template").html()),
		initialize: function () {
			this.listenTo(this.model, 'change', this.render);
			this.listenTo(this.model, 'destroy', this.remove);
		},

		render: function () {
			this.$el.find("h1").hide();
			var grhm = {};
			grhm.description = this.model.description();
			grhm.title = this.model.get("title");
			grhm.lastRoll = this.model.get("lastRoll") !== null && !isNaN(this.model.get("lastRoll")) ? this.model.get("lastRoll") : "-";
			app.lastRoll = grhm.lastRoll;
			this.$el.html(this.template(grhm));
			this.$el.find("h1").fadeIn("slow");
			return this;
		}, 

		roll: function (e) {
			app.h1 = this.$el.find("h1");
			app.that = this;
			var hideReady = function () {
				app.that.model.roll();
				$(app.h1).fadeIn("fast");
			};
			$(app.h1).fadeOut("fast", hideReady);
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