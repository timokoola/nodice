var app = app || {};

(function () {
	'use strict';


	var DiceList = Backbone.Collection.extend({
		model: app.Die,

		localStorage: new Backbone.LocalStorage('nodice-backbone'),

		nextOrder: function() {
			if(!this.length) {
				return 1;
			}
			return this.last().get('order') + 1;
		},
		comparator: function(die) {
			return die.get('order');
		}

	});
	app.Dice = new DiceList();
})();