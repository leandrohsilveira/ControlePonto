angular.module('PontoControlFX')

.controller('RegistroMensalController', function($scope, $rootScope, DateService, PontoService) {
	var now = new Date();
	var usuarioAutenticado = $rootScope.usuarioAutenticado;
	usuarioAutenticado = usuarioAutenticado ? usuarioAutenticado : {};
	$scope.mes = now.getMonth();
	$scope.ano = now.getFullYear();

	$scope.offset = usuarioAutenticado.offset;

	$scope.nav = new FolhaPontoNav(now, function (control) {
		$scope.mes = control.getMonth();
		$scope.ano = control.getFullYear();
		getFolhaMensal();
	});

	$scope.horarioSemanal = [
		usuarioAutenticado.domingo,
		usuarioAutenticado.segunda,
		usuarioAutenticado.terca,
		usuarioAutenticado.quarta,
		usuarioAutenticado.quinta,
		usuarioAutenticado.sexta,
		usuarioAutenticado.sabado
	];

	$scope.isDataHoje = function (date) {
		return now.getDate() == date.getDate() && now.getMonth() == date.getMonth() && now.getFullYear() == date.getFullYear();
	};

	$scope.getIconeCalendario = function (registro) {
		if(registro.trabalha) {
			if(registro.ativo) {
				if(registro && registro.entrada && registro.almoco && registro.retorno && registro.saida) {
					return 'fa-calendar-check-o text-success';
				} else {
					return 'fa-calendar-plus-o text-primary';
				}
			} else {
				if(now.getDate() > registro.date.getDate() && now.getMonth() >= registro.date.getMonth() && now.getFullYear() >= registro.date.getFullYear()) {
					return 'fa-calendar-times-o text-danger';
				} else {
					return 'fa-calendar-o';
				}
			}
		} else {
			return 'fa-calendar-minus-o';
		}
	};

	var iniciarRegistros = function () {
		$scope.dias = {};
		$scope.total = '0s';
		var d = 1;
		var date;
		while (!date || (date = new Date($scope.ano, $scope.mes, d)).getMonth() == $scope.mes) {
			date = new Date($scope.ano, $scope.mes, d);
			$scope.dias[d] = {dia: d, date: date, ativo: false, trabalha: $scope.horarioSemanal[date.getDay()], entrada: '', almoco: '', retorno: '', saida: '', totalExpediente: 0, totalAlmoco: 0};
			d++;
		};
	};

	var atualizarRegistros = function (folhaMensal) {
		$scope.total = DateService.formatTotalTimeString(folhaMensal.total * $scope.offset);
		for (var _dia in folhaMensal.registros) {
			if (folhaMensal.registros.hasOwnProperty(_dia)) {
				var diario = folhaMensal.registros[_dia];
				$scope.dias[_dia] = diario;
				$scope.dias[_dia].ativo = true;
				$scope.dias[_dia].trabalha = true;
				$scope.dias[_dia].date = new Date(folhaMensal.ano, folhaMensal.mes, _dia);
			}
		}
	};

	var getFolhaMensal = function () {
		PontoService.getFolhaMensal($scope.mes, $scope.ano)
					.then(function (response) {
						iniciarRegistros();
						return atualizarRegistros(response.data);
					}, function (errorResponse) {
						return PontoService.checkError(errorResponse);
					});
	};

	getFolhaMensal();

});
