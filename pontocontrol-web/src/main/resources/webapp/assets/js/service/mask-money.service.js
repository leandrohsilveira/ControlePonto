angular.module('PontoControlFX')

.factory('MaskMoneyService', function() {

	return {

		apply: function(selector) {
			if(!selector) {
				selector = '.mask-money';
			}
			var $maskMoney = $(selector);

			$maskMoney.maskMoney({prefix:'R$ ', allowNegative: false, thousands:'.', decimal:',', affixesStay: false});

			return $maskMoney;
		},

		parse: function(value) {
			var str = value.replace(/[^\d\,\-]/g, "");
			return +str.replace(/[\,]/g, ".");
		}

	};

});
