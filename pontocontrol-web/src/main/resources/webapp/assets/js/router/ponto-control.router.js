angular.module('PontoControlFX')

.config(function($routeProvider) {

	$routeProvider

	.when('/login', {
		templateUrl: 'login.html',
		controller: 'LoginController',
		resolve: {
			autenticado: function ($location, PontoService) {
				PontoService.isAutenticado()
					.then(function (response) {
						if(response.data.autenticado) {
							$location.path('/');
						}
						return response.data.autenticado;
					});
			}
		}
	})

	.when('/usuario', {
		templateUrl: 'usuario.html',
		controller: 'UsuarioController',
		resolve: {
			usuarioAutenticado: function (PontoService) {
				PontoService.getUsuarioAutenticado()
								.then(function (response) {
									return response.data;
								}, function (error) {
									return PontoService.checkError(error);
								});
			}
		}
	})

	.when('/registro/mensal', {
		templateUrl: 'registro-mensal.html',
		controller: 'RegistroMensalController',
		resolve: {
			folhaMensal: function ($location, PontoService) {
				var mes = $location.search().mes;
				var ano = $location.search().ano;
				return PontoService.getFolhaMensal(mes, ano)
							.then(function (response) {
								return response.data;
							}, function (errorResponse) {
								return PontoService.checkError(errorResponse);
							});
			}
		}
	})

});
