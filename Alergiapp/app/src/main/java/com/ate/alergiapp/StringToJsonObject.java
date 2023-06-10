package com.ate.alergiapp;

import org.json.JSONException;
import org.json.JSONObject;

public class StringToJsonObject {
    public static JSONObject convertToJson(String str){
        JSONObject json = null;
        try {
            JSONObject jsonObject = new JSONObject(str);
            System.out.println("OBJECT : "+jsonObject.toString());
            json = jsonObject;
        } catch (JSONException err) {
            System.out.println("Exception : "+err.toString());
        }
        return json;
    }
}