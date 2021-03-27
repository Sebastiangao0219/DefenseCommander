package com.sebastian.defensecommander;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TopScoreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_score);

        Utils.setupFullScreen(this);
        TextView textView = findViewById(R.id.textSheets);
        Intent intent = getIntent();
        String message= intent.getStringExtra("SCOREINFO");
        textView.setText(message);
    }

    public void clickExit(View v){
        SoundPlayer.getInstance().stopBackgroundSounds();
        finish();
    }
}
