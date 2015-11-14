angular.module('PontoControlFX')

.factory('DateService', function($filter) {
	return {
		parse : function(dataStr) {
			if (!dataStr) {
				return;
			}
			var d = dataStr.split('-');
			try {
				return new Date(d[2], d[1] - 1, d[0]);
			} catch (e) {
				return;
			}
		},
		parseFimDoDia: function(dataStr) {
			if (!dataStr) {
				return;
			}
			var d = dataStr.split('-');
			try {
				return new Date(d[2], d[1] - 1, d[0], 23, 59, 59);
			} catch (e) {
				return;
			}
		},
		format : function(data) {
			return $filter('date')(data, 'dd-MM-yyyy');
		}
	}
})
