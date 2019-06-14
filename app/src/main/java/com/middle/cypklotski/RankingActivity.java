package com.middle.cypklotski;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RankingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"font/huarong.ttf");
        TextView rank_title = findViewById(R.id.rank_title);
        rank_title.setTypeface(typeface);
        RelativeLayout panel = findViewById(R.id.rank_panel);
        int childCount = panel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView child = (TextView) panel.getChildAt(i);
            child.setTypeface(typeface);
            }
        String android_id = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        DBHelper dbHelper = new DBHelper(this,"usr_db",null,1);
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cursor = db.query("user", new String[]{"id", "name", "pass"}, null, null, null, null, "pass");
        int count = cursor.getCount();
        cursor.moveToLast();
        for (int j=1; j <= 5; j++) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int pass = cursor.getInt(cursor.getColumnIndex("pass"));
            int rank_id = getResources().getIdentifier("rank_"+j, "id", getPackageName());
            int name_id = getResources().getIdentifier("name_"+j, "id", getPackageName());
            int pass_id = getResources().getIdentifier("pass_"+j, "id", getPackageName());
            TextView this_rank = findViewById(rank_id);
            TextView this_name = findViewById(name_id);
            TextView this_pass = findViewById(pass_id);
            this_rank.setText(j+"");
            this_name.setText(name);
            this_pass.setText(pass+"关");
            Log.i("outputrank", name+pass+"..."+j);
            if(cursor.isFirst()) break;
            cursor.moveToPrevious();
        }
        cursor.moveToLast();
        int index = 1;
        while(true) {
            if(cursor.getString(cursor.getColumnIndex("id")).equals(android_id)) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int pass = cursor.getInt(cursor.getColumnIndex("pass"));
                int rank_id = getResources().getIdentifier("my_rank", "id", getPackageName());
                int name_id = getResources().getIdentifier("my_name", "id", getPackageName());
                int pass_id = getResources().getIdentifier("my_pass", "id", getPackageName());
                TextView this_rank = findViewById(rank_id);
                TextView this_name = findViewById(name_id);
                TextView this_pass = findViewById(pass_id);
                this_rank.setText(index+"");
                this_name.setText(name);
                this_pass.setText(pass+"关");
                break;
            }
            index++;
            cursor.moveToPrevious();
        }
        db.close();
    }
}
