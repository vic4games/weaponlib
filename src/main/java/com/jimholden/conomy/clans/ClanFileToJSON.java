package com.jimholden.conomy.clans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public final class ClanFileToJSON {
    @Nullable
    public static JsonObject readJsonFile(File file) {
    	//System.out.println("ClanFileToJSON: " + file);
        JsonParser jsonParser = new JsonParser();
        try(BufferedReader br = new BufferedReader(new FileReader(file), Short.MAX_VALUE)) {
            JsonElement jsonElement = jsonParser.parse(br);
            if (jsonElement instanceof JsonObject) {
                return (JsonObject) jsonElement;
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
