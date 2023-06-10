package com.ate.alergiapp;


import android.content.Intent;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;

import android.os.StrictMode;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;
    ArrayList<String> username_list = new ArrayList<>();
    private String http_call = "http://192.168.1.35:5000";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createAccountBtn = findViewById(R.id.create_account_btn);
        progressBar = findViewById(R.id.progress_bar);
        loginBtnTextView = findViewById(R.id.login_text_view_btn);

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String is_current_user = "False";

                if(username.equals("") || password.equals("") || confirmPassword.equals("")){
                    Utility.showToast(MainActivity.this, "Rellene todos los campos");
                }else{
                    if(password.equals(confirmPassword)){
                        if(password.length() < 6){
                            Utility.showToast(MainActivity.this, "La contraseña debe tener al menos 6 carácteres");
                        }else {
                            OkHttpClient okHttpClient = new OkHttpClient();
                            Request request = new Request.Builder().url(http_call+"/get_usernames").build();
                            okHttpClient.newCall(request).enqueue(new Callback() {
                                  @Override
                                  public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              Utility.showToast(MainActivity.this, "Network not found");
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
                                                  JSONArray jsonArray = json.getJSONArray("usernames");

                                                  for(int i=0;i<jsonArray.length();i++){
                                                      try {
                                                          JSONObject json_item = jsonArray.getJSONObject(i);
                                                          username_list.add(json_item.getString("username"));
                                                      } catch (JSONException e) {
                                                          e.printStackTrace();
                                                      }
                                                  }
                                              } catch (IOException e) {
                                                  e.printStackTrace();
                                              } catch (JSONException e) {
                                                  e.printStackTrace();
                                              }

                                              if (username_list.contains(usernameEditText.getText().toString()) == false){
                                                  Intent intento = new Intent(MainActivity.this, CompleteUser.class);
                                                  intento.putExtra("username", username);
                                                  intento.putExtra("password", password);
                                                  intento.putExtra("is_current_user", is_current_user);
                                                  startActivity(intento);
                                              }else{
                                                  Utility.showToast(MainActivity.this, "Nombre de usuario ya existente");
                                              }
                                          }
                                      });
                                  }
                              });
                        }
                    }else{
                        Utility.showToast(MainActivity.this, "Las contraseñas no coinciden");
                    }
                }
            }
        });

        loginBtnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}


