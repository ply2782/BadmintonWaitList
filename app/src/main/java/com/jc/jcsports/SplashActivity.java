package com.jc.jcsports;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.jc.jcsports.activities.GameScreen;
import com.jc.jcsports.databinding.ActivitySplashBinding;

interface SplashInterface {
    void moveIntent();
}


public class SplashActivity extends AppCompatActivity implements SplashInterface {

    private ActivitySplashBinding binding;
    private NetworkConnectionCheck networkConnectionCheck;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handler = new Handler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    //  LOLLIPOP Version 이상..
            if (networkConnectionCheck == null) {
                networkConnectionCheck = new NetworkConnectionCheck(getApplicationContext());
                networkConnectionCheck.setSplashInterface(this);
                networkConnectionCheck.register();
            }
        }
    }


    private void endConnectionOfNetWork() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (networkConnectionCheck != null) {
                networkConnectionCheck.unregister();
                networkConnectionCheck = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endConnectionOfNetWork();
    }

    @Override
    public void moveIntent() {
        handler.postDelayed(() -> {
            endConnectionOfNetWork();
            Toast.makeText(SplashActivity.this, "network available", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            Intent intent = new Intent(SplashActivity.this, GameScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }, 500);
    }
}