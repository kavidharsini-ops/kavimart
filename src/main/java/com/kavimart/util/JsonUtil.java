package com.kavimart.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kavimart.dto.ApiResponse;
import com.kavimart.exception.AppException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class JsonUtil {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private JsonUtil() {
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }

    public static void write(HttpServletResponse response, int status, ApiResponse<?> body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(GSON.toJson(body));
    }

    public static void writeError(HttpServletResponse response, AppException exception) throws IOException {
        write(response, exception.getStatus(), ApiResponse.failure(exception.getCode(), exception.getMessage()));
    }

    public static void writeInternalError(HttpServletResponse response) throws IOException {
        write(response, 500, ApiResponse.failure("INTERNAL_ERROR", "An unexpected error occurred."));
    }
}