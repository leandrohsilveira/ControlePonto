package br.com.lhs.pontocontrol.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.pontocontrol.controleponto.controller.ControllerFactory;
import br.com.pontocontrol.controleponto.controller.IFolhaPontoController;
import br.com.pontocontrol.controleponto.controller.json.impl.FolhaMensalPontoJSON;
import br.com.pontocontrol.controleponto.controller.json.impl.RegistroDiarioPontoJSON;
import br.com.pontocontrol.controleponto.model.RegistroDiarioPonto;
import br.com.pontocontrol.controleponto.util.DateUtils;

public class RegistroDiarioServlet extends BaseServlet {

	private static final long serialVersionUID = -2167947735392512684L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final Integer dia = getParameterAsIntegerOrDefault(req, "dia", DateUtils.getActualDayOfMonth());
		final Integer mes = getParameterAsIntegerOrDefault(req, "mes", DateUtils.getActualMonth());
		final Integer ano = getParameterAsIntegerOrDefault(req, "ano", DateUtils.getActualYear());

		final IFolhaPontoController controller = ControllerFactory.localizar(IFolhaPontoController.class);
		final FolhaMensalPontoJSON folhaMensal = controller.recuperarFolhaMensal(ano, mes);
		if (folhaMensal != null) {
			final RegistroDiarioPontoJSON registroDiario = controller.recuperarRegistroDiario(dia, folhaMensal);
			if (registroDiario != null) {
				respondJson(resp, registroDiario);
				return;
			}
		}
		respondJson(resp, new RegistroDiarioPontoJSON(new RegistroDiarioPonto(dia)));
	}

}
