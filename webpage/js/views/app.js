var app = app || {};

(function ($) {

	app.AppView = Backbone.View.extend({

		el: "body",

		events: {
			'click #addpanel': "addDie",
			"click #rollbutton": "rollAll",
			"click .makeitsobutton": "changeType"
		},
		

		initialize: function() {
			this.listenTo(app.Dice, "add", this.addOne);
			this.listenTo(app.Dice, "reset", this.addAll);
			this.listenTo(app.Dice, "all", this.render);

			app.Dice.fetch();
		},

		
		addOne: function(die) {
			var view = new app.DieView({model: die});
			view.render().$el.hide();
			$("#diced").prepend(view.render().el);
			view.render().$el.fadeIn("slow");
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
			$("h1.roll-number").hide();
			app.Dice.each(this.rollOne,this);
			$("h1.roll-number").fadeIn("slow");
		},
		rollOne: function(d) {
			d.roll();
		},
		changeType: function(e) {
			var title = $("input[name='title']").val();
			var modifier = parseInt($("input[name='modifier']").val(),10);
			var type = parseInt($("input[name='type']").val(),10);
			var multiplier = parseInt($("input[name='multiplier']").val(),10);
			var iid = $("input[name='id']").val();
			var item = app.Dice.get(iid);
			item.set("title",title);
			item.set("modifier",modifier);
			item.set("multiplier",multiplier);
			item.set("type",type);
			item.save();
			$("#baseModal").foundation("reveal", "close");
		}


		});
})(jQuery);