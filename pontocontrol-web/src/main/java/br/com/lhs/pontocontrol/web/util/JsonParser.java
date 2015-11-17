package br.com.lhs.pontocontrol.web.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JsonParser {

	public static final SimpleDateFormat DEFAULT_DATE_FORMAT;

	private static final Logger logger = Logger.getLogger(JsonParser.class.getName());

	private static final GsonBuilder GSON_BUILDER = new GsonBuilder().registerTypeHierarchyAdapter(Date.class, new JsonParser.DateSerializer()).registerTypeHierarchyAdapter(Date.class, new JsonParser.DateDeserializer());

	static {
		DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		DEFAULT_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	private JsonParser() {
	}

	public static <T> T parse(String json, Class<T> type) {
		return GSON_BUILDER.create().fromJson(json, type);
	}

	public static <T> String format(T obj) {
		return GSON_BUILDER.create().toJson(obj);
	}

	private static class DateSerializer implements JsonSerializer<Date> {
		@Override
		public JsonElement serialize(Date src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(DEFAULT_DATE_FORMAT.format(src));
		}
	}

	private static class DateDeserializer implements JsonDeserializer<Date> {

		@Override
		public Date deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			try {
				return DEFAULT_DATE_FORMAT.parse(json.getAsString());
			} catch (final ParseException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				return null;
			}
		}
	}

}