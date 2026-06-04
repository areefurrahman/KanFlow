package com.kanflow.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.kanflow.model.Task;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage {

    private static final Logger LOG = AppLogger.get(JsonStorage.class);

    private static final String DATA_DIR  = "data";
    private static final String FILE_PATH = DATA_DIR + "/tasks.json";

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>)   (src, type, ctx) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class,
                    (JsonDeserializer<LocalDate>) (json, type, ctx) -> LocalDate.parse(json.getAsString()))
            .setPrettyPrinting()
            .create();

    public static void saveTasks(List<Task> tasks) {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Type listType = new TypeToken<List<Task>>() {}.getType();
            Files.writeString(Paths.get(FILE_PATH), GSON.toJson(tasks, listType));
            LOG.info("Saved {} task(s) to {}", tasks.size(), FILE_PATH);
        } catch (IOException e) {
            LOG.error("Failed to save tasks: {}", e.getMessage(), e);
            throw new RuntimeException("Could not save tasks. Check disk permissions.", e);
        }
    }

    public static List<Task> loadTasks() {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) {
                LOG.info("No task file found at {}. Starting fresh.", FILE_PATH);
                return new ArrayList<>();
            }
            String json = Files.readString(path);
            Type listType = new TypeToken<List<Task>>() {}.getType();
            List<Task> tasks = GSON.fromJson(json, listType);
            List<Task> result = tasks != null ? tasks : new ArrayList<>();
            LOG.info("Loaded {} task(s) from {}", result.size(), FILE_PATH);
            return result;
        } catch (IOException e) {
            LOG.error("Failed to load tasks: {}", e.getMessage(), e);
            throw new RuntimeException("Could not load tasks. File may be corrupted.", e);
        }
    }
}