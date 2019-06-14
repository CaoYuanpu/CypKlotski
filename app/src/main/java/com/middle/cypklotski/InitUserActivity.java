package com.middle.cypklotski;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class InitUserActivity extends AppCompatActivity {
    public static InitUserActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_user);
        instance = this;
        Typeface typeface = Typeface.createFromAsset(getAssets(),"font/huarong.ttf");
        TextView title = findViewById(R.id.user_title);
        TextView query = findViewById(R.id.query);
        query.bringToFront();
        title.setTypeface(typeface);
        query.setTypeface(typeface);
        EditText name = findViewById(R.id.name);
        name.setTypeface(typeface);
        name.bringToFront();
        TextView ok = findViewById(R.id.ok);
        ok.setTypeface(typeface);
    }

    public void register(View view) {
        EditText name = findViewById(R.id.name);
        String usr_name = name.getText().toString();
        String android_id = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        DBHelper dbHelper = new DBHelper(this, "usr_db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", android_id);
        cv.put("name", usr_name);
        cv.put("pass", 0);
        cv.put("score", "");
        db.insert("user", null, cv);
        db.close();
        Intent intent = new Intent(this, InitActivity.class);
        startActivity(intent);
        finish();
    }
}
