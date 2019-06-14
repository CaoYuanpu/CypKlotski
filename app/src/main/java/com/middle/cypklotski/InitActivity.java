package com.middle.cypklotski;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"font/huarong.ttf");
        Button begin_button = findViewById(R.id.begin_button);
        Button rank_button = findViewById(R.id.rank_button);
        Button quit_button = findViewById(R.id.quit_button);
        Button login_button = findViewById(R.id.login_button);
        begin_button.setTypeface(typeface);
        rank_button.setTypeface(typeface);
        quit_button.setTypeface(typeface);
        login_button.setTypeface(typeface);
        TextView title = findViewById(R.id.title);
        title.setTypeface(typeface);
        String android_id = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        Log.i("android_id", android_id);
        DBHelper dbHelper = new DBHelper(InitActivity.this, "usr_db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //db.delete("user", "id=?", new String[] {android_id});
        /*db.delete("user", "name=?", new String[] {"LiuBei1"});
        db.delete("user", "name=?", new String[] { "Guanyu1"});
        db.delete("user", "name=?", new String[] {"CaoCao1"});
        db.delete("user", "name=?", new String[] { "ZhaoYun"});
        db.delete("user", "name=?", new String[] {"MaChao1"});*/
        Cursor cursor = db.query("user", new String[] {"id", "name"}, "id=?", new String[]{android_id}, null, null, null);
        if(!cursor.moveToNext()) {
            Intent intent = new Intent(this, InitUserActivity.class);
            startActivity(intent);
        }
    }

    public void beginGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void quitGame(View view) {
        //InitUserActivity.instance.finish();
        /*android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);*/
        ActivityManager activityManager = (ActivityManager) this.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
        for (ActivityManager.AppTask appTask : appTaskList) {
            appTask.finishAndRemoveTask();
        }
        // appTaskList.get(0).finishAndRemoveTask();
        System.exit(0);
    }

    public void enterRanking(View view) {
        Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }

    public void enterHelp(View view) {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }
}
