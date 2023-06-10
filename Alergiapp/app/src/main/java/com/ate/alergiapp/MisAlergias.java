package com.ate.alergiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MisAlergias extends AppCompatActivity {

    CheckBox checkBoxLac, checkBoxGlu, checkBoxFru, checkBoxSac;
    Button updateAllergies;
    ImageButton backHome;
    String allergies = "";
    private String http_call = "http://192.168.1.35:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_alergias);

        updateAllergies = findViewById(R.id.update_allergies_btn);
        backHome = findViewById(R.id.back_to_home_btn);
        checkBoxLac = findViewById(R.id.checkBoxLac);
        checkBoxGlu = findViewById(R.id.checkBoxGlu);
        checkBoxFru = findViewById(R.id.checkBoxFru);
        checkBoxSac = findViewById(R.id.checkBoxSac);

        updateAllergies.setOnClickListener((v)-> updateAllergiesFunction());
        backHome.setOnClickListener((v)-> backHomeFunction());
    }

    private void backHomeFunction() {
        startActivity(new Intent(MisAlergias.this,HomeActivity.class));
    }

    private void updateAllergiesFunction() {
        if (!checkBoxLac.isChecked() == false && !checkBoxGlu.isChecked() == false && !checkBoxFru.isChecked() == false && !checkBoxSac.isChecked() == false){
            Utility.showToast(MisAlergias.this, "Marcar al menos una opción");
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
            OkHttpClient okHttpClient = new OkHttpClient();

            RequestBody formbody = RequestBody.create(MediaType.parse("application/json"),
                    "{\"allergy_type\": \" " + allergies +"\"}");

            Request request = new Request.Builder().url(http_call+"/update_allergies").put(formbody).build();
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
                            Utility.showToast(MisAlergias.this, "Alergias modificadas correctamente");
                            startActivity(new Intent(MisAlergias.this,HomeActivity.class));
                        }
                    });
                }
            });
        }
    }
}