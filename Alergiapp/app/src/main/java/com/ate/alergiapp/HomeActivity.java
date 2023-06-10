package com.ate.alergiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    FloatingActionButton addRestBtn;
    ImageButton menuBtn;
    private String http_call = "http://192.168.1.35:5000";

    ListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addRestBtn = findViewById(R.id.add_rest_btn);
        menuBtn = findViewById(R.id.menu_btn);

        menuBtn.setOnClickListener((v)-> showMenu());

        addRestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateValRestActivity.class);
                startActivity(intent);
            }
        });

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(http_call+"/get_recommendations_for_current_user").build();
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
                            listView = (ListView) findViewById(R.id.list_view);
                            JSONObject json = null;
                            return_response = response.body().string();
                            json = StringToJsonObject.convertToJson(return_response);
                            JSONObject root = new JSONObject(String.valueOf(json));
                            JSONArray array = root.getJSONArray("recommendations");

                            ArrayList<CustomObject> customObjects = new ArrayList<>();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                CustomObject customObject = new CustomObject(object.getString("restaurant_name"),
                                        object.getString("restaurant_address"),
                                        object.getString("pag_web"),
                                        object.getString("restaurant_score"));
                                customObjects.add(customObject);
                            }

                            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), customObjects);

                            if (listView != null) {
                                listView.setAdapter(customAdapter);
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(HomeActivity.this, menuBtn);
        popupMenu.getMenu().add("Mis alergias");
        popupMenu.getMenu().add("Mis valoraciones");
        popupMenu.getMenu().add("Cerrar sesión");
        popupMenu.show();


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            OkHttpClient okHttpClient = new OkHttpClient();
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "Mis alergias") {
                    startActivity(new Intent(HomeActivity.this, MisAlergias.class));
                }
                if (item.getTitle() == "Mis valoraciones") {
                    startActivity(new Intent(HomeActivity.this, MisValoraciones.class));
                }
                if (item.getTitle() == "Cerrar sesión") {
                    listView.removeAllViewsInLayout();
                    Request request = new Request.Builder().url(http_call + "/get_current_username_with_password").build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Utility.showToast(getApplicationContext(), "Network not found");
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
                                        JSONArray jsonArray = json.getJSONArray("current_username_with_password");

                                        if (jsonArray.length() > 1) {
                                            Utility.showToast(getApplicationContext(), "Más de un usuario activo");
                                        } else if (jsonArray.length() == 1) {
                                            try {
                                                JSONObject json_item = jsonArray.getJSONObject(0);
                                                String username = json_item.getString("username");
                                                String password = json_item.getString("user_password");

                                                RequestBody formbody = RequestBody.create(MediaType.parse("application/json"),
                                                        "{\"is_current_user\": 0\"}");
                                                Request request = new Request.Builder().url(http_call +
                                                        "/update_is_current_user?username=" + "'" + username + "'"
                                                        + "&user_password=" + "'" + password + "'"
                                                        + "&is_current_user=0").put(formbody).build();
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
                                                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                                                finish();
                                                            }
                                                        });
                                                    }
                                                });

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Utility.showToast(getApplicationContext(), "Ningún usuario activo");
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
                return false;
            }
        });
    }
}