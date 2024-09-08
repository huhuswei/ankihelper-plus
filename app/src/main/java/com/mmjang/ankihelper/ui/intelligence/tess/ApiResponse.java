package com.mmjang.ankihelper.ui.intelligence.tess;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ApiResponse {
    @SerializedName("url")
    private String url;
    @SerializedName("languages")
    private List<TesseractDataInfo> languages;

    // Getters
    public String getUrl() {
        return url;
    }

    public List<TesseractDataInfo> getLanguages() {
        return languages;
    }

    // Setters
    public void setUrl(String url) {
        this.url = url;
    }

    public void setLanguages(List<TesseractDataInfo> languages) {
        this.languages = languages;
    }

    // Other methods if needed
    // ...

    public static ApiResponse parseJsonFromAssets(Context context, String jsonName) {
        AssetManager assetManager = context.getAssets();
        String json = null;
        try {
            InputStream inputStream = assetManager.open(jsonName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            json = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create a Gson instance and parse the JSON string
        Gson gson = new Gson();
        ApiResponse apiResponse = gson.fromJson(json, ApiResponse.class);
        return apiResponse;
    }
}