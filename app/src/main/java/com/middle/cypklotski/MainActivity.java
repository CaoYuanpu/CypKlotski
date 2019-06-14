package com.middle.cypklotski;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    public static final String SCENE = "scene";
    public static final String SCENE_BEST = "scene_best";
    public static final int TEXT_REQUEST = 1;
    String android_id;
    DBHelper dbHelper;
    int pass;
    Vector<String> score_arr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("test order", "enter Create");
        Typeface typeface = Typeface.createFromAsset(getAssets(),"font/huarong.ttf");
        //改变字体
        for (int i = 0; i < 7; i++) {
            String scene = "scene"+i;
            Log.d("scene", scene);
            int scene_id = getResources().getIdentifier(scene, "id", getPackageName());
            Button button = findViewById(scene_id);
            button.setTypeface(typeface);
        }
        TextView title = findViewById(R.id.level0);
        title.setTypeface(typeface);
        score_arr = new Vector<String>();
        android_id = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        dbHelper = new DBHelper(this, "usr_db", null, 1);
        getPassAndScore();
        unlockScene(pass);

    }

    // 改变字体
    static public void changeFonts(Context context, ViewGroup root, String font){
        Typeface tf = Typeface.createFromAsset(context.getAssets(),font);
        for (int i=0;i<root.getChildCount();i++){
            View v = root.getChildAt(i);
            if (v instanceof TextView){
                ((TextView)v).setTypeface(tf);
            }else if (v instanceof Button){
                ((Button)v).setTypeface(tf);
            }else if (v instanceof EditText){
                ((TextView)v).setTypeface(tf);
            }else if (v instanceof ViewGroup){
                changeFonts(context, (ViewGroup)v, font);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        Log.i("test order", "enter Result");
        super.onActivityResult(requestCode, resultCode, data);
        String scene_num = data.getStringExtra(MainGame.reply);
        String scene_score = data.getStringExtra(MainGame.reply_score);
        Log.d("return num & score", scene_num+">>>"+scene_score);
        int scene = Integer.parseInt(String.valueOf(scene_num.charAt(0))) + 1;
        if(scene > pass){
            int button_id = getResources().getIdentifier("scene"+scene, "id", getPackageName());
            Button button = findViewById(button_id);
            button.setEnabled(true);
            int tint = Color.parseColor("black");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("black")));
            }
            pass++;
            score_arr.add(scene_score);
            updatePassAndScore();
        }
        else {
            int score_now = Integer.parseInt(scene_score);
            int score_old = Integer.parseInt(score_arr.get(scene-1));
            if( score_now < score_old) {
                score_arr.set(scene-1, score_now+"");
                updateScore();
            }
        }
    }

    public void chooseScene(View view) {
        Intent intent = new Intent(this, MainGame.class);
        Button button = findViewById(view.getId());
        String scene = button.getHint().toString();
        intent.putExtra(SCENE, scene);
        if(Integer.parseInt(scene) < score_arr.size())
            intent.putExtra(SCENE_BEST, score_arr.get(Integer.parseInt(scene)));
        else
            intent.putExtra(SCENE_BEST, "无");
        startActivityForResult(intent, TEXT_REQUEST);
    }

    public void getPassAndScore() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("user", new String[] { "pass", "score"}, "id=?", new String[]{android_id}, null, null, null);
        cursor.move(1);
        pass = cursor.getInt(0);
        String score = cursor.getString(1);
        if (score.length() != 0) {
            String[] arr = score.split(",");
            for (int i = 0; i < arr.length; i++) {
                score_arr.add(arr[i]);
            }
        }
    }

    public void unlockScene(int num) {
        for ( int i = 1; i <= num; i++) {
            int button_id = getResources().getIdentifier("scene"+i, "id", getPackageName());
            Button button = findViewById(button_id);
            button.setEnabled(true);
            int tint = Color.parseColor("black");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("black")));
            }
        }
    }

    public void updatePassAndScore() {
        String score = "";
        for (int i = 0; i < score_arr.size(); i++) {
            score += (score_arr.get(i)+",");
        }
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("pass", pass);
        values.put("score", score);
        Log.i("update score", score);
        db.update("user", values, "id=?", new String[]{android_id});
        db.close();
    }

    public void updateScore() {
        String score = "";
        for (int i = 0; i < score_arr.size(); i++) {
            score += (score_arr.get(i)+",");
        }
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("score", score);
        Log.i("update score", score);
        db.update("user", values, "id=?", new String[]{android_id});
        db.close();
    }


}
