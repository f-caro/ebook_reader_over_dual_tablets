package com.fcaro.ebookreaderoverdualtablets;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Splash_screen extends AppCompatActivity {

    TextView slogan ,txt;

    Animation topanim,bottomanim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        slogan=(TextView)findViewById(R.id.tv2);
        txt=(TextView)findViewById(R.id.tv3);

        // Animation

        topanim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomanim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //Hooks

        slogan.setAnimation(bottomanim);
        txt.setAnimation(bottomanim);

        //Intent

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(Splash_screen.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        },6000);

    }
}