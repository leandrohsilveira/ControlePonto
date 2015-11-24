angular.module('PontoControlFX')

.factory('PontoService', function($q, $http, $location, url) {

	var _isAutenticado = function () {
		return $http({
			url: url + '/login',
			method: 'GET'
		});
	};

	var _getUsuarioAutenticado = function () {
		return $http({
			url: url + '/usuario',
			method: 'GET'
		});
	};

	var _salvarUsuario = function (method, usuario) {
		return $http({
			url: url + '/usuario',
			method: method,
			data: usuario
		});
	};

	var _registrarAgora = function () {
		return $http({
			url: url + '/restrito/registrar',
			method: 'POST'
		});
	};

	var _autenticar = function (params) {
		return $http({
			url: url + '/login',
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded'
			},
			params: params
		});
	};

	var _sair = function () {
		return $http({
			url: url + '/login',
			method: 'DELETE'
		});
	};

	var _getFolhaMensal = function(mes, ano) {
		var now = new Date();
		if(!mes) {
			mes = now.getMonth();
		}
		if(!ano) {
			ano = now.getFullYear();
		}
		return $http({
			url: url + "/restrito/folha/mensal",
			method: 'GET',
			params: {
				mes: +mes,
				ano: +ano
			}
		});
	};

	var _checkError = function (errorResponse) {
		var data = errorResponse.data;
		if(errorResponse.status == 401) {
			$location.path('/publico/login');
			return errorResponse;
		} else {
			console.error(errorResponse);
			if(errorResponse.data) {
				alert(errorResponse.status +": "+ data.mensagem);
			} else {
				alert(errorResponse.status);
			}
			return errorResponse;
		}
	};

	return {
		autenticar: function (data) {
			return _autenticar(data);
		},
		sair: function () {
			return _sair();
		},
		isAutenticado: function () {
			return _isAutenticado();
		},
		getUsuarioAutenticado: function () {
			return _getUsuarioAutenticado();
		},
		registrarAgora: function () {
			return _registrarAgora();
		},
		cadastrarUsuario: function (usuario) {
			return _salvarUsuario('POST', usuario);
		},
		atualizarUsuario: function (usuario) {
			return _salvarUsuario('PUT', usuario);
		},
		getFolhaMensal: function (mes, ano) {
			return _getFolhaMensal(mes, ano);
		},
		checkError: function (errorResponse) {
			return _checkError(errorResponse);
		}
	}

});
