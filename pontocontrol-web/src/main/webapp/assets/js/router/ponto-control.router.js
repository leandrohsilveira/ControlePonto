angular.module('PontoControlFX')

.config(function($routeProvider) {

	$routeProvider.when('/registro/mensal', {
		templateUrl: 'registro-mensal.html',
		controller: 'RegistroMensalController'
	})

});
