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
		controller: 'UsuarioController'
	})

	.when('/registro/mensal', {
		templateUrl: 'registro-mensal.html',
		controller: 'RegistroMensalController'
	});

});
