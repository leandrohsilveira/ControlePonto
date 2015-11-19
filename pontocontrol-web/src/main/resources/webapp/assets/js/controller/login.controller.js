angular.module('PontoControlFX')

.controller('LoginController', function($scope, $rootScope, $location, PontoService, autenticado) {

	$scope.login = '';

	$scope.autenticar = function () {
		PontoService.autenticar({login: $scope.login})
						.then(function (response) {
							$rootScope.usuarioAutenticado = response.data;
							$location.path('/');
						}, function (errorResponse) {
							PontoService.checkError(errorResponse);
						});
	}

});
