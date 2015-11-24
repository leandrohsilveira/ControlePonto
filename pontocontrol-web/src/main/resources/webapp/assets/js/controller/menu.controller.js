angular.module('PontoControlFX')

.controller('MenuController', function ($scope, $rootScope, $location, PontoService) {
	$scope.usuario = $rootScope.usuarioAutenticado;

	$rootScope.$on('_usuarioAutenticado', function () {
		$scope.usuario = $rootScope.usuarioAutenticado;
	});

	$scope.sair = function () {
		PontoService.sair().then(function (response) {
			console.log('Logout realizado com sucesso.');
			$rootScope.usuarioAutenticado = null;
			$scope.usuario = null;
			$location.path('/');
		}, function (errorResponse) {
			console.log(errorResponse.data);
			PontoService.checkError(errorResponse);
		});
	};
});
