package com.ate.alergiapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginBtn;
    ProgressBar progressBar;
    TextView createAccountBtnTextView;
    ArrayList<String> username_passw_list = new ArrayList<>();
    private String http_call = "http://192.168.1.35:5000";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginBtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progress_bar);
        createAccountBtnTextView = findViewById(R.id.create_account_text_view_btn);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                Integer is_current_user = 1;

                if(username.equals("") || password.equals("")){
                    Utility.showToast(LoginActivity.this, "Por favor, introduzca sus credenciales");
                }else{
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder().url(http_call+"/get_usernames_and_passwords").build();
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
                                        JSONArray jsonArray = json.getJSONArray("usernames_and_passw");

                                        for(int i=0;i<jsonArray.length();i++){
                                            try {
                                                JSONObject json_item = jsonArray.getJSONObject(i);
                                                String username = json_item.getString("username");
                                                String password = json_item.getString("user_password");
                                                username_passw_list.add(""+username+"-"+password+"");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (username_passw_list.contains(
                                            ""+usernameEditText.getText().toString()+"-"+passwordEditText.getText().toString()+"") == true){
                                        RequestBody formbody = RequestBody.create(MediaType.parse("application/json"),
                                                "{\"is_current_user\": 1\"}");
                                        Request request = new Request.Builder().url(http_call+
                                                "/update_is_current_user?username="+"'"+usernameEditText.getText().toString()+"'"
                                                +"&user_password="+"'"+passwordEditText.getText().toString()+"'"
                                                +"&is_current_user=1").put(formbody).build();
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
                                                        Utility.showToast(getApplicationContext(), "Usuario logeado exitosamente");
                                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        });
                                    }else{
                                        Utility.showToast(LoginActivity.this, "Usuario no encontrado");
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });




        createAccountBtnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}