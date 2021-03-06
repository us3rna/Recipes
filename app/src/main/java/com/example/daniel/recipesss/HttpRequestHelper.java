/*
 * Copyright (C) 2015 Daniel Jacob
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.daniel.recipesss;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import xdroid.toaster.Toaster;

/* gathers data from yummly api endpoint */
public class HttpRequestHelper {
    /* downloads recipes from yummly api */
    protected static synchronized String downloadFromServer(String... params) {
        String result = "";
        String url;
        // gets context from static environment
        Context context = MyApplication.getAppContext();
        // shared preferences instance
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // checks activity
        int activity = preferences.getInt("Activity", 0);
        // gets correct url
        url = HttpRequestHelper.returnCorrectUrl(activity, params[0]);
        // creates url object
        URL urlObject = createUrlObject(url);
        // returns json data from yummly
        result = getJsonDataFromApi(urlObject);
        return result;
    }

    /* returns correct url that needs to be requested from api based on what activity the user is in */
    public static String returnCorrectUrl(int activity, String query){
        String url = "";
        String[] elements;
        String onComplete;
        // general recipe search
        if (activity == 3) {
            url = "http://api.yummly.com/v1/api/recipes?_app_id=6e1ce2e1&_app_key=63ef2eaf3a8986a3a1e3d5872857c2fa&q="
                    + query + "&maxResult=40" + "&requirePictures=true";
        }
        // recipes by ingredient search
        else if (activity == 4) {
            String allowedIngredient = "&allowedIngredient[]=";
            // splits entered words
            elements = query.split(" ");
            // adds allowedingredient string before the word
            for (int i = 0; i < elements.length; i++) {
                elements[i] = allowedIngredient + elements[i];
            }
            // creates string from elements array
            onComplete = "";
            for (int i = 0; i < elements.length; i++) {
                onComplete += elements[i];
            }
            // full url for recipes by ingredient search
            url = "http://api.yummly.com/v1/api/recipes?_app_id=6e1ce2e1&" +
                    "_app_key=63ef2eaf3a8986a3a1e3d5872857c2fa&q=" + query + onComplete +
                    "&maxResult=40" + "&requirePictures=true";
        }
        return url;
    }

    /* creates an url object */
    public static URL createUrlObject(String url){
        URL urlObject = null;
        try {
            urlObject = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return urlObject;
    }

    /* gets json data from yummly endpoint with url object */
    public static String getJsonDataFromApi(URL url){
        String result = "";
        HttpURLConnection connection;
        if (url != null) {
            try {
                // make connection to endpoint
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                Integer responseCode = connection.getResponseCode();
                // succesfull response
                if (responseCode >= 200 && responseCode < 300) {
                    // inputreader
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    String data;
                    // append data to result string
                    while ((data = reader.readLine()) != null) {
                        result += data;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toaster.toast("Error connecting to server");
            }
        }
        return result;
    }
}