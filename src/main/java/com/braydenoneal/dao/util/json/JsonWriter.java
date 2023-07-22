package com.braydenoneal.dao.util.json;

import com.braydenoneal.dao.util.CompletableFutureStream;
import com.google.gson.Gson;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class JsonWriter {
    DataWriter dataWriter;
    DataOutput dataOutput;

    public JsonWriter(DataWriter dataWriter, DataOutput dataOutput) {
        this.dataWriter = dataWriter;
        this.dataOutput = dataOutput;
    }

    public static JsonWriter create(DataWriter dataWriter, DataOutput dataOutput) {
        return new JsonWriter(dataWriter, dataOutput);
    }

    public CompletableFuture<?> write(Entry... entries) {
        return CompletableFutureStream.of(Arrays.stream(entries).map(entry ->
                DataProvider.writeToPath(
                        this.dataWriter,
                        new Gson().toJsonTree(entry.object),
                        this.dataOutput.getPath().resolve(entry.path + entry.fileName + ".json"))
        ));
    }

    public static Entry entry(Object object, String path, String fileName) {
        return new Entry(object, path, fileName);
    }

    public static class Entry {
        public Object object;
        public String path;
        public String fileName;

        public Entry(Object object, String path, String fileName) {
            this.object = object;
            this.path = path;
            this.fileName = fileName;
        }
    }
}
