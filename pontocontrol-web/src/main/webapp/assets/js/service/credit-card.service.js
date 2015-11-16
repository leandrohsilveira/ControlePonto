angular.module('PontoControlFX')

.factory('CreditCardService', function($q, $http, url) {

	var _getToken = function() {
		return $http({
			url: url + '/pagseguro',
			method: 'GET'
		});
	};

	var _getPaymentMethods = function(value) {
		var deffered = $q.defer();
		PagSeguroDirectPayment.getPaymentMethods({
			amount: +value,
			success: function(response) {
				deffered.resolve(response.paymentMethods);
			},
			error: function(response) {
				deffered.reject({mensagem: 'Não foi possível obter os métodos de pagamento disponíveis.'});
			}
		});
		return deffered.promise;
	};

	var _createCreditCardToken = function(cardData) {
		var deffered = $q.defer();
		var data = {
			success: function(response) {
				deffered.resolve(response.card.token);
			},
			error: function(response) {
				deffered.reject({mensagem: 'Não foi possível gerar o token do cartão de crédito.'});
			}
		};

		angular.extend(data, cardData);

		PagSeguroDirectPayment.createCardToken(data);

		return deffered.promise;
	};

	var _sendCheckout = function(type, paymentData) {
		return $http({
			url: url + '/pagseguro/'+type,
			method: 'POST',
			data: paymentData
		});
	};

	var _executeLightboxCheckout = function (value, senderData) {
		return _sendCheckout('lightbox', {
			valor: value,
			nome: senderData.senderName,
			cpf: senderData.senderCpf
		});
	};

	var _executeRecurrentCheckout = function(value, senderData) {
		return _sendCheckout('recurrent', {
			valor: value,
			nome: senderData.senderName,
			cpf: senderData.senderCpf
		});
	};

	var _openLightBox = function (code) {
		var deffered = $q.defer();
		var open = PagSeguroLightbox({ code: code }, {
				success: function (transactionCode) {
					deffered.resolve(transactionCode);
				},
				abort: function () {
					deffered.reject('Não foi possível abrir a janela de pagamento do PagSeguro.');
				}
			});
		return {
			open: open,
			promise: deffered.promise
		};
	};

	var _executeTransparentCheckout = function(config, checkoutData) {
		var data = {
			valor: config.value,
			nome: checkoutData.holderName,
			cpf: checkoutData.holderCpf,
			senderHash: config.senderHash,
			cardToken: null
		};

		var deffered = $q.defer();

		_createCreditCardToken({
			cardNumber: checkoutData.cardNumber,
			brand: checkoutData.brand,
			cvv: checkoutData.cvv,
			expirationMonth: checkoutData.expirationMonth,
			expirationYear: checkoutData.expirationYear
		}).then(
			function(cardToken) {
				data.cardToken = cardToken;
				_sendCheckout('transparent', data)
					.then(function(success) {
						deffered.resolve(success.data);
					}, function(error) {
						deffered.reject(error);
					});
			},
			function(error) {
				deffered.reject(error);
			}
		);

		return deffered.promise;
	};

	return {
		getConfiguration: function(value) {

			var deffered = $q.defer();

			_getToken()
				.then(function(response){
					PagSeguroDirectPayment.setSessionId(response.data.token);
					var senderHash = PagSeguroDirectPayment.getSenderHash();
					_getPaymentMethods(value)
						.then(function(paymentMethods) {
							deffered.resolve({
								value: value,
								token: response.data.token,
								paymentMethods: paymentMethods,
								senderHash: senderHash
							});
						}, function(error) {
							deffered.reject(error);
						});
				}, function(error) {
					deffered.reject(error)
				});

			return deffered.promise;
		},
		getBrandData: function(cardBin) {
			var deffered = $q.defer();
			PagSeguroDirectPayment.getBrand({
				cardBin: cardBin,
				success: function(response) {
					deffered.resolve(response.brand);
				},
				erro: function(response) {
					console.error(response);
					deffered.reject({mensagem: 'Não foi possível obter a bandeira do cartão.'});
				}
			});
			return deffered.promise;
		},

		openLightBox: function (code) {
			return _openLightBox(code);
		},

		executeLightboxCheckout: function(value, senderData) {
			return _executeLightboxCheckout(value, senderData);
		},

		executeRecurrentCheckout: function(value, senderData) {
			return _executeRecurrentCheckout(value, senderData);
		},

		executeTransparentCheckout: function(config, checkoutData) {
			return _executeTransparentCheckout(config, checkoutData);
		},

		getTransactions: function(page, initialDate, finalDate) {
			return $http({
					url: url + '/pagseguro/transaction/' + page,
					method: 'GET',
					params: {
						di: initialDate,
						df: finalDate
					}
				});
		}

	};

});
