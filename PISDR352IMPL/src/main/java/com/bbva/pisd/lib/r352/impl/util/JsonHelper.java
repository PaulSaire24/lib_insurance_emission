package com.bbva.pisd.lib.r352.impl.util;

import com.bbva.pisd.lib.r352.impl.PISDR352Impl;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class JsonHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonHelper.class);
    private static final String DATE = "yyyy-MM-dd";
    private static final JsonHelper INSTANCE = new JsonHelper();
    private final Gson gson;

    private JsonHelper() {
        gson = new GsonBuilder()
                .setDateFormat(DATE)
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

}

class LocalDateAdapter implements JsonSerializer<LocalDate> {

    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

}
