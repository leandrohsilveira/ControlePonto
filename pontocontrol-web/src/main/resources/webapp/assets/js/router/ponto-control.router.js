angular.module('PontoControlFX')

.config(function($routeProvider) {

	$routeProvider

	.when('/publico/login', {
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

	.when('/publico/usuario', {
		templateUrl: 'usuario.html',
		controller: 'UsuarioController'
	})

	.when('/restrito/registro/mensal', {
		templateUrl: 'registro-mensal.html',
		controller: 'RegistroMensalController'
	})
	
	.when('/restrito/registro/diario', {
		templateUrl: 'registro-diario.html',
		controller: 'RegistroDiarioController'
	});



}).run(function ($rootScope, $location, PontoService) {
	$rootScope.$on("$locationChangeStart",function(event, next, current){
		if(/\#\/publico/g.test(next)) {
			if(!$rootScope.usuarioAutenticado) {
				PontoService.isAutenticado().then(function (response) {
					if(response.data.autenticado) {
						$rootScope.usuarioAutenticado = response.data;
						$rootScope.$broadcast('_usuarioAutenticado');
					}
				});
			}
		} else if(/\#\/restrito/g.test(next)) {
			if(!$rootScope.usuarioAutenticado) {
				PontoService.getUsuarioAutenticado().then(function (response) {
					if(response.data) {
						$rootScope.usuarioAutenticado = response.data;
						$rootScope.$broadcast('_usuarioAutenticado');
					} else {
						event.preventDefault();
						$location.path('/publico/login');
					}
				});
			}
		} else {
			console.log('Nenhum padrão de diretório encontrado para Filtrar. URL: ' + next);
		}
	});
});
