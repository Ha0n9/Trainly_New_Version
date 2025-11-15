package com.example.trainly;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.ivSplashLogo);
        TextView title = findViewById(R.id.tvSplashTitle);
        TextView subtitle = findViewById(R.id.tvSplashSubtitle);

        Animation fade = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Apply animation to ALL items
        logo.startAnimation(fade);
        title.startAnimation(fade);
        subtitle.startAnimation(fade);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }, 2000);
    }
}
