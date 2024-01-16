package com.bbva.pisd.lib.r352.impl.util;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class JsonHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonHelper.class);
    private static final String DATE = "yyyy-MM-dd";
    private static final JsonHelper INSTANCE = new JsonHelper();
    private final Gson gson;

    private JsonHelper() {
        gson = new GsonBuilder()
                .setDateFormat(DATE)
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    public static JsonHelper getInstance() {
        LOGGER.info("getInstance START *****");
        return INSTANCE;
    }
    public String toJsonString(Object o) {
        LOGGER.info("toJsonString START *****");
        return this.gson.toJson(o);
    }

    public <T> T fromString(String src, Class<T> clazz) {
        return this.gson.fromJson(src, clazz);
    }
}

class LocalDateAdapter implements JsonSerializer<LocalDate> {

    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

}
