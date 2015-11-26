angular.module('PontoControlFX')

.controller('RegistroDiarioController', function($scope, $rootScope, $location, PontoService, MaskedInputService) {

	MaskedInputService.apply();

	$scope.folhaDiaria = {
		data: new Date(),
		entrada: '',
		almoco: '',
		retorno: '',
		saida: ''
	};

	$rootScope.$on('_timeMaskedInputUpdated', function (event, arg) {
		//alert('Event Broadcast Recieved: '+ JSON.stringify({arg: arg}));
		$scope.$apply(function () {
			$scope.folhaDiaria[arg.inputId] = arg.inputValue
		});
	});

	$scope.salvar = function () {
		if($scope.registroDiario) {

		} else {

		}
	};

	var carregarFolhaDiaria  = function () {
		var dia = $location.search().dia;
		var mes = $location.search().mes;
		var ano = $location.search().ano;

		return PontoService.getFolhaDiaria(dia, mes, ano)
			.then(function (response) {
				var folha = response.data;
				var data = new Date();
				data.setDate(folha.dia);
				if(mes) data.setMonth(mes);
				if(ano) data.setFullYear(ano);
				$scope.folhaDiaria = {
					data: data,
					entrada: folha.entrada,
					almoco: folha.almoco,
					retorno: folha.retorno,
					saida: folha.saida
				};
				return folha;
			}, function (error) {
				return PontoService.checkError(error);
			});

	};

	$scope.registroDiario = carregarFolhaDiaria();

});
