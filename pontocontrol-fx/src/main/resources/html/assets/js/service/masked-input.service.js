angular.module('PontoControlFX')

.service('MaskedInputService', function () {

	return {

		apply: function (selector) {
			var defaults = {
				cpfSelector: '.cpf-mask',
				creditCardSelector: '.credit-card-mask',
				config: {
					autoclear: false,
					placeholder: ' '
				}
			};

			angular.extend(defaults, selector);

			var $inputs = {
				$cpfInputs: $(defaults.cpfSelector),
				$creditCardInputs: $(defaults.creditCardSelector)
			};

			$inputs.$cpfInputs.mask('999.999.999-99', defaults.config);
			$inputs.$creditCardInputs.mask('9999 9999 9999 9999', defaults.config);
			return $inputs;
		},

		parseCreditCard: function (maskedCreditCard) {
			if(maskedCreditCard) {
				return maskedCreditCard.replace(/ /g, '');
			}
			return '';
		}

	};

});
