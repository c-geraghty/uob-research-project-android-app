package com.example.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HealthAdviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_advice_activity);

    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(this, MainActivity.class));
}
}
