var FolhaPontoNav = (function () {

	// vars
	var _agora;
	var _controle;
	var _listener;

	// construtor
	FolhaPontoNav = function (dataInicial, changeListener) {
		_agora = new Date();

		if(!changeListener) changeListener = function (date) {};
		if(!dataInicial) dataInicial = now;

		_controle = dataInicial;
		_listener = changeListener;
	};

	// funções privadas
	var _getDate = function (controle, mes, ano) {
		return new Date(controle.getFullYear() + ano, controle.getMonth() + mes);
	}

	var _adicionarAoControle = function (mes, ano) {
		_controle = _getDate(_controle, mes, ano);
		_listener(_controle);
	}

	// funções publicas
	FolhaPontoNav.prototype.controle = function () {
		return _controle;
	};

	FolhaPontoNav.prototype.atual = function () {
		return _agora;
	};

	FolhaPontoNav.prototype.isAtualSelecionado = function () {
		return _agora.getFullYear() == _controle.getFullYear() && _agora.getMonth() == _controle.getMonth();
	};

	FolhaPontoNav.prototype.mesAnterior = function () {
		return _getDate(_controle, -1, 0);
	};

	FolhaPontoNav.prototype.mesSeguinte = function () {
		return _getDate(_controle, 1, 0);
	};

	FolhaPontoNav.prototype.anoAnterior = function () {
		return _getDate(_controle, 0, -1);
	};

	FolhaPontoNav.prototype.anoSeguinte = function () {
		return _getDate(_controle, 0, -1);
	};

	FolhaPontoNav.prototype.irParaAtual = function () {
		_controle = _agora;
		_listener(_controle);
	};

	FolhaPontoNav.prototype.irParaMesAnterior = function () {
		_adicionarAoControle(-1, 0);
	};

	FolhaPontoNav.prototype.irParaMesSeguinte = function () {
		_adicionarAoControle(1, 0);
	};

	FolhaPontoNav.prototype.irParaAnoAnterior = function () {
		_adicionarAoControle(0, -1);
	};

	FolhaPontoNav.prototype.irParaAnoSeguinte = function () {
		_adicionarAoControle(0, 1);
	};

	return FolhaPontoNav;

}());
