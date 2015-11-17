angular.module('PontoControlFX')

.factory('PontoService', function($q, $http, url) {

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
	}

	return {
		getFolhaMensal: function (mes, ano) {
			return _getFolhaMensal(mes, ano);
		}
	}

});
