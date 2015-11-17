package br.com.lhs.pontocontrol.web.servlet;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import br.com.lhs.pontocontrol.web.util.AcceptJson;
import br.com.lhs.pontocontrol.web.util.AcceptMediaType;
import br.com.lhs.pontocontrol.web.util.JsonParser;
import br.com.lhs.pontocontrol.web.vo.ServletErrorVO;

public class BaseServlet extends HttpServlet {

	private static final long serialVersionUID = -4035906773460637669L;

	protected interface MEDIA_TYPE {
		public static final String JSON = "application/json";
		public static final String TEXT = "text/plain";
		public static final String HTML = "text/html";
		public static final String CSS = "text/css";
		public static final String JAVASCRIPT = "text/javascript";
		public static final String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	}

	protected <T extends Serializable> void acceptJson(final HttpServletRequest request, final HttpServletResponse response, final Class<T> type, final AcceptJson<T> accept) throws IOException, ServletException {
		accept(MEDIA_TYPE.JSON, request, response, () -> {
			final T content = JsonParser.parse(IOUtils.toString(request.getReader()), type);
			if (content != null) {
				accept.accept(content);
			} else {
				respondError(response, HttpServletResponse.SC_BAD_REQUEST, "O corpo da requisição não pode ser vazio ou nulo.");
			}
		});
	}

	protected void accept(final String mediaType, final HttpServletRequest request, final HttpServletResponse response, final AcceptMediaType accept) throws IOException, ServletException {
		if (mediaType == null) {
			accept.accept();
			return;
		} else if (mediaType.equalsIgnoreCase(mediaType)) {
			accept.accept();
			return;
		} else {
			respondError(response, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "O ContentType \"" + mediaType + "\" não é suportado.");
		}
	}

	protected <T extends Serializable> void respondJson(final HttpServletResponse response, final T content) throws IOException {
		response.setContentType(MEDIA_TYPE.JSON);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(JsonParser.format(content));
	}

	protected void respondError(final HttpServletResponse response, final int status, final String mensagem) throws IOException {
		final ServletErrorVO error = new ServletErrorVO(status, mensagem);
		response.setContentType(MEDIA_TYPE.JSON);
		response.sendError(status, JsonParser.format(error));
	}

	protected void respondNoContent(final HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

}
