angular.module('PontoControlFX')

.service('MaskedInputService', function ($filter, $rootScope) {

	return {

		apply: function (selector) {
			var defaults = {
				cpfSelector: '.cpf-mask',
				creditCardSelector: '.credit-card-mask',
				timeSelector: '.time-mask',
				dateSelector: '.date-mask',
				config: {
					autoclear: false,
					placeholder: ' '
				}
			};

			angular.extend(defaults, selector);

			var $inputs = {
				$cpfInputs: $(defaults.cpfSelector),
				$creditCardInputs: $(defaults.creditCardSelector),
				$timeInputs: $(defaults.timeSelector),
				$dateInputs: $(defaults.dateSelector)
			};

			$inputs.$cpfInputs.mask('999.999.999-99', defaults.config);
			$inputs.$creditCardInputs.mask('9999 9999 9999 9999', defaults.config);
			$inputs.$timeInputs.mask('99:99:99', defaults.config).on('blur', function (event) {
				var currentValue = event.currentTarget.value;
				//alert('currentValue: '+currentValue);
				var parsedValue;

				var now = new Date();

				var splitted = currentValue.replace(/ /g, '').split(':');
				//alert('splitted: '+ JSON.stringify({value: splitted}));
				if(!splitted || splitted.length != 3 || (!splitted[0] && !splitted[1] && !splitted[2])) {
					//alert('returned!');
					return;
				};

				var horas;
				try {
					horas = parseInt(splitted[0]);
					horas = horas ? horas : 0;
				} catch(e) {
					horas = 0
				};

				var minutos;
				try {
					minutos = parseInt(splitted[1]);
					minutos = minutos ? minutos : 0;
				} catch(e) {
					minutos = 0
				};

				var segundos;
				try {
					segundos = parseInt(splitted[2]);
					segundos = segundos ? segundos : 0;
				} catch(e) {
					segundos = 0
				};

				//var horas = Number.isInteger(Number.parseInt(splitted[0])) ? Number.parseInt(splitted[0]) : 0;
				//var minutos = Number.isInteger(Number.parseInt(splitted[1])) ? Number.parseInt(splitted[1]) : 0;
				//var segundos = Number.isInteger(Number.parseInt(splitted[2])) ? Number.parseInt(splitted[2]) : 0;

				//alert('resolvedTime: '+ JSON.stringify({horas: horas, minutos: minutos, segundos: segundos}));

				var date = new Date();
				date.setHours(horas);
				date.setMinutes(minutos);
				date.setSeconds(segundos);

				parsedValue = $filter('date')(date, 'HH:mm:ss');
				//alert('parsedValue: '+ parsedValue);

				$rootScope.$broadcast('_timeMaskedInputUpdated', {
					inputId: event.currentTarget.id,
					inputValue: parsedValue
				});
			});
			$inputs.$dateInputs.mask('99/99/9999', defaults.config).on('blur', function () {
				var currentValue = event.currentTarget.value;
				var parsedValue;

				var now = new Date();

				var splitted = currentValue.replace(/ /g, '').split('/');
				var dia = Number.isInteger(Number.parseInt(splitted[0])) ? Number.parseInt(splitted[0]) : now.getDate();
				var mes = Number.isInteger(Number.parseInt(splitted[1])) ? Number.parseInt(splitted[1]) : now.getMonth() + 1;
				var ano = Number.isInteger(Number.parseInt(splitted[2])) ? Number.parseInt(splitted[2]) : now.getFullYear();

				var date = new Date();
				date.setDate(dia);
				date.setMonth(mes - 1);
				date.setFullYear(ano);

				parsedValue = $filter('date')(date, 'dd/MM/yyyy');

				$rootScope.$broadcast('_dateMaskedInputUpdated', {
					inputId: event.currentTarget.id,
					inputValue: parsedValue
				});
			});

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
