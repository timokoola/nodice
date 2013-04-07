/* global */
var app = app || {};

(function () {
	'use strict';

	app.Die = Backbone.Model.extend({
		defaults: {
			title: "Die",
			multiplier: 1,
			type: 6,
			modifier: 0,
			lastRoll: -Infinity
		},

		modifierPart: function() {
			var modifier = this.get("modifier");
			if (modifier === 0) {
				return "";
			} else if(modifier >= 0) {
				return "+" + modifier;
			} else {
				return "" + modifier;
			}
		},
		description: function() {
			var result = "" + this.get("multiplier") + "d" + this.get("type") + this.modifierPart();
			return result;
		},
		minValue: function() {
			return this.get("multiplier") * 1 + this.get("modifier");
		},
		maxValue: function() {
			return this.get("multiplier")  * this.get("type") + this.get("modifier");
		},
		roll: function() {
			var result = this.get("modifier");
			for(var i = 0; i < this.get("multiplier"); i++) {
				result += Math.floor((Math.random() * this.get("type")) + 1);
			}
			this.save("lastRoll",result);
			return result;
		}

	});
  
  

})();
