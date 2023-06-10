package com.ate.alergiapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CompleteUser extends AppCompatActivity {

    private CheckBox checkBoxLac, checkBoxGlu, checkBoxFru, checkBoxSac;
    private Button create_user;
    private String http_call = "http://192.168.1.35:5000";
    String allergies = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_user);

        checkBoxLac = findViewById(R.id.checkBoxLac);
        checkBoxGlu = findViewById(R.id.checkBoxGlu);
        checkBoxFru = findViewById(R.id.checkBoxFru);
        checkBoxSac = findViewById(R.id.checkBoxSac);

        create_user = findViewById(R.id.create_account_btn);

        create_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBoxLac.isChecked() == false && !checkBoxGlu.isChecked() == false && !checkBoxFru.isChecked() == false && !checkBoxSac.isChecked() == false){
                    Utility.showToast(CompleteUser.this, "Marcar al menos una opción");
                }else{
                    if (checkBoxLac.isChecked()){
                        allergies = allergies + "Lactosa-";
                    }
                    if (checkBoxGlu.isChecked()){
                        allergies = allergies + "Gluten-";
                    }
                    if (checkBoxFru.isChecked()){
                        allergies = allergies + "Fructosa-";
                    }
                    if (checkBoxSac.isChecked()){
                        allergies = allergies + "Sacarosa-";
                    }

                    Intent intent = getIntent();
                    String username = intent.getStringExtra("username");
                    String password = intent.getStringExtra("password");
                    String is_current_user = intent.getStringExtra("is_current_user");

                    OkHttpClient okHttpClient = new OkHttpClient();

                    RequestBody formbody = RequestBody.create(MediaType.parse("application/json"),
                            "{\"username\": \"" + username + "\",\n" +
                                    "    \"user_password\" : \"" + password + "\",\n" +
                                    "    \"is_current_user\" : \"" + is_current_user + "\",\n" +
                                    "    \"allergy_type\" : \""+ allergies +"\"}");

                    Request request = new Request.Builder().url(http_call+"/post_new_user").post(formbody).build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Utility.showToast(CompleteUser.this, "Network not found - Usuario NO registrado");
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Utility.showToast(CompleteUser.this, "Usuario registrado exitosamente");
                                    Intent intento = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intento);
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}

