angular.module('PontoControlFX', ['ngRoute', 'ui.bootstrap'])

.constant('appContext', 'pontocontrol')
.constant('url', '/pontocontrol/controller')

.config(function($routeProvider) {
	$routeProvider

	.otherwise('/registro/mensal');

});
