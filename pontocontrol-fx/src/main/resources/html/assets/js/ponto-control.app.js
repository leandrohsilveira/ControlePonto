angular.module('PontoControlFX', ['ngRoute', 'ui.bootstrap'])

.config(function($routeProvider) {
	$routeProvider

	.when('/erro', {
		templateUrl: 'erro.html'
	})

	.otherwise('/registro/mensal');

});
