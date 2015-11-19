angular.module('PontoControlFX')

.factory('DateService', function($filter) {
	var _segundo = 1;
	var _minuto = _segundo * 60;
	var _hora = _minuto * 60;

	return {
		formatTotalTimeString: function (totalSeconds) {
			var hora = parseInt(totalSeconds / _hora);
			var rHora = totalSeconds % _hora;
			var minuto = parseInt(rHora / _minuto);
			var rMinuto = rHora % _minuto;
			var segundo = rMinuto;

			var str = '';
			if(hora > 0) {
				str += hora + 'h ';
			}
			if(minuto > 0) {
				str += minuto + 'm ';
			}
			if(segundo > 0) {
				str += segundo + 's ';
			}
			if(!str) {
				str = '0s';
			}
			return str;
		}
	}
})
