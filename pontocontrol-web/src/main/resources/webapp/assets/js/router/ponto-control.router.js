angular.module('PontoControlFX')

.config(function($routeProvider) {

	$routeProvider.when('/registro/mensal', {
		templateUrl: 'registro-mensal.html',
		controller: 'RegistroMensalController',
		resolve: {
			registros: function ($location, PontoService) {
				return PontoService.then(function (response) {
					return response.data;
				}, function (errorResponse) {
					console.error(errorResponse)
					alert(errorResponse.data);
					return;
				});
			}
		}
	})

});
