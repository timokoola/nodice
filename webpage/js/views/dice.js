var app = app || {};


(function ($) {
	'use strict';

	app.DieView = Backbone.View.extend({
		

		events: {
			"click .diepanel": "roll",
			
		},

		template: _.template($("#die-template").html()),
		initialize: function () {
			this.listenTo(this.model, 'change', this.render);
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
		}

	});
})(jQuery);