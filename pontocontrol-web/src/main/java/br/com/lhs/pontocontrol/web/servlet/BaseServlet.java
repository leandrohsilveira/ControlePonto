package br.com.lhs.pontocontrol.web.servlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonObject;

import br.com.lhs.pontocontrol.web.util.AcceptJson;
import br.com.lhs.pontocontrol.web.util.AcceptMediaType;
import br.com.lhs.pontocontrol.web.util.JsonParser;
import br.com.lhs.pontocontrol.web.util.RespondJson;
import br.com.lhs.pontocontrol.web.vo.ServletErrorVO;

public class BaseServlet extends HttpServlet {

	private static final long serialVersionUID = -4035906773460637669L;

	private static final Logger logger = Logger.getLogger(BaseServlet.class.getName());

	protected interface MEDIA_TYPE {
		public static final String JSON = "application/json";
		public static final String TEXT = "text/plain";
		public static final String HTML = "text/html";
		public static final String CSS = "text/css";
		public static final String JAVASCRIPT = "text/javascript";
		public static final String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	}

	protected <T extends Serializable> void acceptAndRespondJson(final HttpServletRequest request, final HttpServletResponse response, Class<T> type, final RespondJson<T> respond) {
		acceptJson(request, response, type, requestContent -> {
			respondJson(response, respond.respond(requestContent));
		});
	}

	protected <T extends Serializable> void acceptJson(final HttpServletRequest request, final HttpServletResponse response, Class<T> type, final AcceptJson<T> accept) {
		try {
			accept(MEDIA_TYPE.JSON, request, response, () -> {
				final T content = JsonParser.parse(IOUtils.toString(request.getReader()), type);
				if (content != null) {
					accept.accept(content);
				} else {
					respondError(response, HttpServletResponse.SC_BAD_REQUEST, "O corpo da requisição não pode ser vazio ou nulo.");
				}
			});
		} catch (final Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			respondError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
	}

	protected void accept(final String mediaType, final HttpServletRequest request, final HttpServletResponse response, final AcceptMediaType accept) {
		try {
			if (mediaType == null) {
				accept.accept();
				return;
			} else if (mediaType.equalsIgnoreCase(mediaType)) {
				accept.accept();
				return;
			} else {
				respondError(response, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "O ContentType \"" + mediaType + "\" não é suportado.");
			}
		} catch (final Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			respondError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
	}

	protected void respondJson(final HttpServletResponse response, final JsonObject content) {
		try {
			response.setContentType(MEDIA_TYPE.JSON);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(JsonParser.format(content));
		} catch (final IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			respondError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
	}

	protected <T extends Serializable> void respondJson(final HttpServletResponse response, final T content) {
		try {
			response.setContentType(MEDIA_TYPE.JSON);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(JsonParser.format(content));
		} catch (final IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			respondError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
	}

	protected void respondError(final HttpServletResponse response, final int status, final String mensagem) {
		try {
			final ServletErrorVO error = new ServletErrorVO(status, mensagem);
			response.setContentType(MEDIA_TYPE.JSON);
			response.setStatus(status);
			response.getWriter().write(JsonParser.format(error));
		} catch (final IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			response.setStatus(status);
		}
	}

	protected void respondNoContent(final HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	protected Integer getParameterAsIntegerOrDefault(HttpServletRequest req, String name, Integer defaultValue) {
		final String parameterStr = req.getParameter(name);
		if (StringUtils.isNotBlank(parameterStr)) {
			try {
				return Integer.valueOf(parameterStr);
			} catch (final NumberFormatException e) {
				// suppressed.
			}
		}
		return defaultValue;
	}

}
