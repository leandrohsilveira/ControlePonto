angular.module('PontoControlFX')

.filter('secondsToDate', function ($filter) {
	return function (seconds) {
		if(seconds < 0) {
			return $filter('date')(new Date(0, 0, 0, 0, 0, seconds * -1), '(-) HH:mm:ss');
		} else {
			return $filter('date')(new Date(0, 0, 0, 0, 0, seconds), 'HH:mm:ss');
		}
	}
});
