package com.jimholden.conomy.clans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public interface JsonWriteable {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    JsonObject toJson();
    default void writeToJson(File file) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file), Short.MAX_VALUE)) {
            GSON.toJson(this.toJson(), bw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}