package com.middle.cypklotski;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"font/huarong.ttf");
        TextView title = findViewById(R.id.help_title);
        Button help = findViewById(R.id.help_button);
        title.setTypeface(typeface);
        help.setTypeface(typeface);
    }
}
