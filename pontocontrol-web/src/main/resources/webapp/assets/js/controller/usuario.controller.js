angular.module('PontoControlFX')

.controller('UsuarioController', function($scope, $rootScope, $location, PontoService) {

	$scope.usuario = {
		nome: '',
		login: '',
		offset: '28800'
	};

	if($rootScope.usuarioAutenticado) {
		$scope.usuario = {
			nome: $rootScope.usuarioAutenticado.nome,
			login: $rootScope.usuarioAutenticado.login,
			offset: $rootScope.usuarioAutenticado.offset
		}
	}

	$scope.offsets = [
		{text: '4 Horas', value: '14400'},
		{text: '6 Horas', value: '21600'},
		{text: '8 Horas', value: '28800'},
		{text: '12 Horas', value: '43200'}
	];

	$scope.salvar = function () {
		var httpPromise;
		if($rootScope.usuarioAutenticado) {
			httpPromise = PontoService.atualizarUsuario($scope.usuario);
		} else {
			httpPromise = PontoService.cadastrarUsuario($scope.usuario);
		}

		httpPromise.then(function (response) {
			alert('Usuario armazenado com sucesso.');
			$location.path('/');
		}, function (errorResponse) {
			PontoService.checkError(errorResponse);
		});
	}

});
