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

	var _getFolhaMensal = function(mes, ano) {
		var now = new Date();
		if(!mes) {
			mes = now.getMonth();
		}
		if(!ano) {
			ano = now.getYear();
		}
		return $http({
			url: url + "/folha/mensal",
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
			$location.path('/login');
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
		isAutenticado: function () {
			return _isAutenticado();
		},
		getUsuarioAutenticado: function () {
			return _getUsuarioAutenticado();
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
