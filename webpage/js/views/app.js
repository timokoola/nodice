var app = app || {};

(function ($) {

	app.AppView = Backbone.View.extend({

		el: "body",

		events: {
			'click #addpanel': "addDie",
			"click .button": "rollAll",
			"keypress": "rollAll",
			"click .panel p span": "changeType"
		},
		

		initialize: function() {
			this.listenTo(app.Dice, "add", this.addOne);
			this.listenTo(app.Dice, "reset", this.addAll);
			this.listenTo(app.Dice, "all", this.render);

			app.Dice.fetch();
		},

		
		addOne: function(die) {
			var view = new app.DieView({model: die});
			$("#diced").prepend(view.render().el);

		},
		addAll: function(die) {
			this.$("#diced").html("");
			app.Dice.each(this.addOne, this);
		},
		newAttrs: function() {
			return {
				multiplier: 1,
				type: 6,
				modifier: 0
			}
		},
		addDie: function(e) {
			//alert("Rock!");
			app.Dice.create(this.newAttrs());
		},
		rollAll: function(e) {
			e.preventDefault();
			app.Dice.each(this.rollOne,this);
		},
		rollOne: function(d) {
			d.roll();
		},
		changeType: function(e) {
			e.preventDefault();
			alert("changing");
		}


		});
})(jQuery);