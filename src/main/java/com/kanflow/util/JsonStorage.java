package com.kanflow.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.kanflow.model.Task;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage {

    private static final String DATA_DIR  = "data";
    private static final String FILE_PATH = DATA_DIR + "/tasks.json";

    // Gson needs a custom adapter because LocalDate isn't natively supported
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (src, type, ctx) ->
                            new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class,
                    (JsonDeserializer<LocalDate>) (json, type, ctx) ->
                            LocalDate.parse(json.getAsString()))
            .setPrettyPrinting()
            .create();

    public static void saveTasks(List<Task> tasks) {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Type listType = new TypeToken<List<Task>>() {}.getType();
            String json = GSON.toJson(tasks, listType);
            Files.writeString(Paths.get(FILE_PATH), json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save tasks: " + e.getMessage(), e);
        }
    }

    public static List<Task> loadTasks() {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) {
                return new ArrayList<>(); // first run — no file yet
            }
            String json = Files.readString(path);
            Type listType = new TypeToken<List<Task>>() {}.getType();
            List<Task> tasks = GSON.fromJson(json, listType);
            return tasks != null ? tasks : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load tasks: " + e.getMessage(), e);
        }
    }
}