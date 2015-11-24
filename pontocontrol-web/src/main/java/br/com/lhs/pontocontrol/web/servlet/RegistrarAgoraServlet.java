package br.com.lhs.pontocontrol.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.pontocontrol.controleponto.controller.ControllerFactory;
import br.com.pontocontrol.controleponto.controller.IFolhaPontoController;
import br.com.pontocontrol.controleponto.controller.json.impl.FolhaMensalPontoJSON;
import br.com.pontocontrol.controleponto.model.FolhaMensalPonto;
import br.com.pontocontrol.controleponto.model.RegistroDiarioPonto;
import br.com.pontocontrol.controleponto.util.DateUtils;

public class RegistrarAgoraServlet extends BaseServlet {

	private static final long serialVersionUID = 4822343376536712272L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final IFolhaPontoController controller = ControllerFactory.localizar(IFolhaPontoController.class);
		final FolhaMensalPontoJSON folhaMensalJson = controller.recuperarFolhaMensal();
		final FolhaMensalPonto folhaMensal = folhaMensalJson.toModel();
		final int hoje = DateUtils.getActualDayOfMonth();
		final RegistroDiarioPonto registroDiario = folhaMensal.getRegistros().getOrDefault(hoje, new RegistroDiarioPonto(hoje));
		registroDiario.registrarProximoAgora();
		folhaMensal.getRegistros().putIfAbsent(hoje, registroDiario);
		controller.sincronizar(folhaMensal);
		respondJson(resp, controller.recuperarFolhaMensal());
	}

}
