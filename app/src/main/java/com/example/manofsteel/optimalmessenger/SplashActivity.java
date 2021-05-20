package com.example.manofsteel.optimalmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Simple Splash activity with some Transition
 * You can validate your Login details and send to
 * activity accordingly
 */
public class SplashActivity extends AppCompatActivity {

    //Variables
    private Animation topAnim, bottomAnim, rightToCenter;

    private Handler handler = new Handler();
    private MyRunnable myRunnable = new MyRunnable();
    private ImageView appLogo;
    private TextView appTitle;
    private TextView tagLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To hide Status bar or we can do with Theme also
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        sendToNextActivity();
        getSupportActionBar().hide();
    }



    private void sendToNextActivity() {
        handler.postDelayed(myRunnable, 2000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (handler != null) {
            handler.removeCallbacks(myRunnable);
        }
        finish();
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {


            Intent intent = new Intent(SplashActivity.this, MainActivity.class);


            startActivity(intent);

            onBackPressed();
        }
    }
}
