angular.module('PontoControlFX')

.controller('MenuController', function ($scope, $rootScope) {
	$scope.usuario = $rootScope.usuarioAutenticado;

	$rootScope.$on('_usuarioAutenticado', function () {
		$scope.usuario = $rootScope.usuarioAutenticado;
	});
});
