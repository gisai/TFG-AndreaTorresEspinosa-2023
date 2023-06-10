package com.ate.alergiapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MisValoraciones extends AppCompatActivity {

    ImageButton backHome;
    private String http_call = "http://192.168.1.35:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_valoraciones);

        backHome = findViewById(R.id.back_to_home_btn);
        backHome.setOnClickListener((v)-> backHomeFunction());

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(http_call+"/get_restaurants_from_current_user").build();
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
                            ListView listView = (ListView) findViewById(R.id.list_view);

                            JSONObject json = null;
                            return_response = response.body().string();
                            json = StringToJsonObject.convertToJson(return_response);
                            JSONObject root = new JSONObject(String.valueOf(json));
                            JSONArray array= root.getJSONArray("restaurants_current_user");

                            ArrayList<CustomObject> customObjects = new ArrayList<>();

                            for(int i=0;i<array.length();i++)
                            {
                                JSONObject object= array.getJSONObject(i);
                                CustomObject customObject = new CustomObject(object.getString("restaurant_name"),
                                        object.getString("restaurant_address"),
                                        object.getString("pag_web"),
                                        object.getString("rating"));
                                customObjects.add(customObject);
                            }

                            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), customObjects);

                            if (listView != null) {
                                listView.setAdapter(customAdapter);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void backHomeFunction() {
        startActivity(new Intent(MisValoraciones.this,HomeActivity.class));
    }
}