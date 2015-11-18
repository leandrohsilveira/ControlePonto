angular.module('PontoControlFX')

.controller('LoginController', function($scope, $location, PontoService, autenticado) {

	$scope.login = '';

	$scope.autenticar = function () {
		PontoService.autenticar({login: $scope.login})
						.then(function (response) {
							$location.path('/');
						}, function (errorResponse) {
							PontoService.checkError(errorResponse);
						});
	}

});
