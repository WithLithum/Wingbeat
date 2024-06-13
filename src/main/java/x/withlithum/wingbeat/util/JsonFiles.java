package x.withlithum.wingbeat.util;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class JsonFiles {
    private JsonFiles() {
        throw new AssertionError("No JsonFiles instances for you!");
    }

    private static final Gson gson = new Gson();

    public static <T> T readCodecObject(Codec<T> codec, File file) throws IOException {
        JsonElement jsonObj;
        try (var reader = new FileReader(file)) {
            jsonObj = JsonParser.parseReader(reader);
        }

        return codec.parse(JsonOps.INSTANCE, jsonObj).getOrThrow(JsonParseException::new);
    }

    public static <T> T readJsonObject(File file, Class<T> type) throws IOException {
        try (var reader = new FileReader(file)) {
            return gson.fromJson(reader, type);
        }
    }

    public static <T> void writeCodecObject(T obj, Codec<T> codec, File file) throws IOException {
        var jsonObj = codec.encodeStart(JsonOps.INSTANCE, obj)
                .getOrThrow(IllegalArgumentException::new)
                .getAsJsonObject();

        try (var writer = new FileWriter(file)) {
            gson.toJson(jsonObj, writer);
        }
    }

    public static void writeJsonObject(Object obj, File file) throws IOException {
        try (var writer = new FileWriter(file)) {
            gson.toJson(obj, writer);
        }
    }
}
