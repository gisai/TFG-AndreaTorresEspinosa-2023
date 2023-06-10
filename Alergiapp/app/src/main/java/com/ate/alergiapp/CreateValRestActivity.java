package com.ate.alergiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateValRestActivity extends AppCompatActivity {
    ImageButton backBtn, saveValRestBtn;
    EditText nombreRestEditText, dirRestEditText, pagwebRestEditText, valoracionEditText;

    Boolean isEditMode = true;
    Intent intent;

    private String http_call = "http://192.168.1.35:5000";
    OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_val_rest);

        nombreRestEditText = findViewById(R.id.rest_name_text);
        dirRestEditText = findViewById(R.id.rest_dir_text);
        pagwebRestEditText = findViewById(R.id.rest_pagweb_text);
        valoracionEditText = findViewById(R.id.rest_valoracion_text);
        backBtn = findViewById(R.id.back_to_home_btn);
        saveValRestBtn = findViewById(R.id.save_rest_btn);


        saveValRestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strRating = valoracionEditText.getText().toString();
                String name = nombreRestEditText.getText().toString();
                String address = dirRestEditText.getText().toString();
                String pag_web = pagwebRestEditText.getText().toString();

                String rating = valoracionEditText.getText().toString();
                int intRating = Integer.parseInt(rating);

                RequestBody formbody = RequestBody.create(MediaType.parse("application/json"),
                        "{\"restaurant_name\": \"" + name + "\",\n" +
                                "    \"restaurant_address\" : \"" + address + "\",\n" +
                                "    \"pag_web\" : \""+ pag_web +"\"}");

                Request request = new Request.Builder().url(http_call+"/post_new_restaurant").post(formbody).build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utility.showToast(getApplicationContext(), "Network not found");
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            String return_response;
                            @Override
                            public void run() {
                                try {
                                    JSONObject json = null;
                                    return_response = response.body().string();
                                    json = StringToJsonObject.convertToJson(return_response);
                                    Integer restaurant_id_int = (Integer) json.get("restaurant_id");
                                    Integer restaurant_id = restaurant_id_int.intValue();
                                    post_new_rating(restaurant_id);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            public void post_new_rating(Integer restaurant_id) {
                                RequestBody formbody = RequestBody.create(MediaType.parse("application/json"),
                                        "{\"id_restaurant\": \"" + restaurant_id + "\",\n" +
                                                "    \"rating\" : \""+ intRating +"\"}");

                                Request request = new Request.Builder().url(http_call+"/post_new_rating").post(formbody).build();

                                okHttpClient.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Utility.showToast(getApplicationContext(), "Network not found");
                                            }
                                        });
                                    }
                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Utility.showToast(getApplicationContext(), "Restaurante guardado exitosamente");
                                                Intent intento = new Intent(getApplicationContext(), HomeActivity.class);
                                                startActivity(intento);
                                            }
                                        });
                                    }
                                });

                            }
                        });
                    }
                });

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }


}