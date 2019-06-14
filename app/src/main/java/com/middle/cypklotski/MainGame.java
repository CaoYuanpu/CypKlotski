package com.middle.cypklotski;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;

public class MainGame extends AppCompatActivity implements View.OnTouchListener {
    private int button_width;
    private int button_height;
    private int global_id = 0;
    private static final int up = 0;
    private static final int right = 1;
    private static final int down = 2;
    private static final int left = 3;
    private int downX;
    private int downY;
    private int current_position[];

    private int button_position[][];
    public TextView name;
    public TextView current_score;
    public TextView best_score;
    public Button withdraw;
    public String[] scene_name_list = {"七步成诗", "海阔天空", "诸葛羽扇", "似远实近", "高枕无忧", "没落兵种", "近在一步"};
    public int score;
    public int last_id;
    public Stack<int[]> records;
    public static final String reply = "reply";
    public static final String reply_score = "score";
    String scene_num;
    String scene_best;
    Typeface typeface;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        typeface = Typeface.createFromAsset(getAssets(),"font/huarong.ttf");
        name = findViewById(R.id.guankia_name);
        current_score = findViewById(R.id.current_score);
        best_score = findViewById(R.id.best_score);
        withdraw = findViewById(R.id.withdraw);
        name.setTypeface(typeface);
        current_score.setTypeface(typeface);
        best_score.setTypeface(typeface);
        withdraw.setTypeface(typeface);
        Button back = findViewById(R.id.back);
        back.setTypeface(typeface);
        button_width = dip2px(this,80);
        button_height = dip2px(this,80);
        button_position = new int[5][4];
        Intent intent = getIntent();
        scene_num = intent.getStringExtra(MainActivity.SCENE);
        scene_best = intent.getStringExtra(MainActivity.SCENE_BEST);
        best_score.setText("最佳步数: "+ scene_best);
        name.setText(scene_name_list[Integer.parseInt(scene_num)]);
        Log.d("receive", scene_num);
        score = 0;
        records = new Stack<int[]>();
        loadScene(scene_num);
    }

    //根据参数添加单个相应按钮
    @SuppressLint("ClickableViewAccessibility")
    public void addButton(Context context, String name, String position) {
        RelativeLayout panel = findViewById(R.id.game_panel);
        int drawable_id = getResources().getIdentifier(name, "drawable", getPackageName());
        int row = Integer.parseInt(String.valueOf(position.charAt(0)));
        int column = Integer.parseInt(String.valueOf(position.charAt(1)));
        int type = Integer.parseInt(String.valueOf(position.charAt(2)));
        int panel_width = 4*button_width;
        int panel_height = 5*button_height;
        int width = 0;
        int height = 0;
        if (type==1) {width=button_width; height=button_height;}
        else if(type==2) {width=button_width; height=button_height*2;}
        else if(type==3) {width=button_width*2; height=button_height;}
        else if(type==4) {width=button_width*2; height=button_height*2;}
        RelativeLayout.LayoutParams layout_params=new RelativeLayout.LayoutParams(width, height);
        Button button = new Button(context);
        button.setBackgroundResource(drawable_id);
        int left = column*button_width;
        int top = row*button_height;
        int right = panel_width - left - width;
        int bottom = panel_height - top - height;
        layout_params.setMargins(left, top, right, bottom);
        button.setId(global_id);
        global_id++;
        button.setHint(position);
        button.setHintTextColor(getResources().getColor(R.color.transparent));
        button.setOnTouchListener(this);
        //Log.d("Yuanpu", button.getText().toString());
        panel.addView(button, layout_params);
    }

    //把dp根据不同屏幕尺寸转化成px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }



    //动态加载不同关卡的布局
    public void loadScene(String scene_num) {
        InputStream is = null;
        try {
            is = getAssets().open("SceneLayout"+scene_num+".json");
            JsonReader reader = new JsonReader(new InputStreamReader(is));
            reader.beginObject();
            while(reader.hasNext()) {
                String tag = reader.nextName();
                if(tag.equals("scene"+scene_num)) {
                    reader.beginArray();
                    while(reader.hasNext()) {
                        reader.beginArray();
                        int flag = 0;
                        String drawble_name = "";
                        String position;
                        while(reader.hasNext()) {
                            if(flag==0) drawble_name = reader.nextString();
                            else {
                                position = reader.nextString();
                                addButton(this, drawble_name, position);
                            }
                            flag++;
                        }
                        reader.endArray();
                    }
                    reader.endArray();
                }
                else {
                    reader.beginArray();
                    int i = 0;
                    while(reader.hasNext()) {
                        reader.beginArray();
                        int j = 0;
                        while(reader.hasNext()) {
                            int tmp = reader.nextInt();
                            button_position[i][j] = tmp;
                            j++;
                        }
                        reader.endArray();
                        i++;
                    }
                    reader.endArray();
                }
            }
            reader.endObject();
            reader.close();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        Button current_button = findViewById(view.getId());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int)event.getRawX();
                downY = (int)event.getRawY();
                current_position = getPosition(current_button);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int distanceX = (int)event.getRawX() - downX;
                int distanceY = (int)event.getRawY() - downY;
                int direction = getDirection(distanceX, distanceY);
                int block_num = Integer.parseInt(String.valueOf(current_button.getHint().charAt(2)));
                moveButton(current_button,block_num,current_position,direction);
                Log.i("next", "enter next"+current_button.getHint());
                String info = current_button.getHint().toString();
                if(info.equals("314")) {
                    Log.i("next", "enter next");
                   Button win = findViewById(R.id.win);
                   win.bringToFront();
                   win.setVisibility(View.VISIBLE);
                   win.setTypeface(typeface);
                }
                int this_id = view.getId();
                //存栈
                int[] record = {this_id, block_num, direction, score};
                records.push(record);
                String this_score = "当前步数: ";
                if (score == 0) {
                    last_id = this_id;
                    score += 1;
                    current_score.setText(this_score+score);
                }
                else {
                    if (last_id != this_id) {
                        score += 1;
                        current_score.setText(this_score+score);
                        last_id = this_id;
                    }
                }
        }
        return true;
    }

    //根据坐标返回对应数组位置
    public int[] getPosition(Button button) {
        //Log.d("Yuanpu", "enter getPosition");
        int position[] = new int[2];
        position[0] = (Integer.parseInt(String.valueOf(button.getHint().charAt(0))));
        position[1] = (Integer.parseInt(String.valueOf(button.getHint().charAt(1))));
        return position;
    }

    //根据手势滑动判断方向
    public int getDirection(int distancex, int distancey) {
        if(Math.abs((float)distancex/button_width) >= Math.abs((float)distancey/button_height)){
            if(distancex > 0) return right;
            else return left;
        }
        else {
            if(distancey > 0) return down;
            else return up;
        }
    }

    //根据方向移动按钮
    public void moveButton(Button button, int block_num, int[] position, int direction) {
        int row = position[0];
        int col = position[1];
        switch (direction) {
            case up:
                switch (block_num) {
                    case 1:
                        try {
                            if(button_position[row-1][col] == 0) {
                                button.layout(button.getLeft(), button.getTop()-button_height, button.getRight(), button.getBottom()-button_height);
                                button_position[row-1][col] = 1;
                                button_position[row][col] = 0;
                                String temp_position = (row-1) + "" + col + "" + 1;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                    case 2:
                        try {
                            if(button_position[row-1][col] == 0) {
                                button.layout(button.getLeft(), button.getTop()-button_height, button.getRight(), button.getBottom()-button_height);
                                button_position[row-1][col] = 1;
                                button_position[row+1][col] = 0;
                                String temp_position = (row-1) + "" + col + "" + 2;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                    case 3:
                        try {
                            if(button_position[row-1][col] == 0 && button_position[row-1][col+1] == 0) {
                                button.layout(button.getLeft(), button.getTop()-button_height, button.getRight(), button.getBottom()-button_height);
                                button_position[row-1][col] = 1;
                                button_position[row-1][col+1] = 1;
                                button_position[row][col] = 0;
                                button_position[row][col+1] = 0;
                                String temp_position = (row-1) + "" + col + "" + 3;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                    case 4:
                        try {
                            if(button_position[row-1][col] == 0 && button_position[row-1][col+1] == 0) {
                                button.layout(button.getLeft(), button.getTop()-button_height, button.getRight(), button.getBottom()-button_height);
                                button_position[row-1][col] = 1;
                                button_position[row-1][col+1] = 1;
                                button_position[row+1][col] = 0;
                                button_position[row+1][col+1] = 0;
                                String temp_position = (row-1) + "" + col + "" + 4;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                }
                break;
            case right:
                switch (block_num) {
                    case 1:
                        try {
                            if(button_position[row][col+1] == 0) {
                                button.layout(button.getLeft()+button_width, button.getTop(), button.getRight()+button_width, button.getBottom());
                                button_position[row][col+1] = 1;
                                button_position[row][col] = 0;
                                String temp_position = row + "" + (col+1) + "" + 1;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                    case 2:
                        try {
                            if(button_position[row][col+1] == 0 && button_position[row+1][col+1] == 0) {
                                button.layout(button.getLeft()+button_width, button.getTop(), button.getRight()+button_width, button.getBottom());
                                button_position[row][col+1] = 1;
                                button_position[row+1][col+1] = 1;
                                button_position[row][col] = 0;
                                button_position[row+1][col] = 0;
                                String temp_position = row + "" + (col+1) + "" + 2;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                    case 3:
                        try {
                            if(button_position[row][col+2] == 0) {
                                button.layout(button.getLeft()+button_width, button.getTop(), button.getRight()+button_width, button.getBottom());
                                button_position[row][col+2] = 1;
                                button_position[row][col] = 0;
                                String temp_position = row + "" + (col+1) + "" + 3;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                    case 4:
                        try {
                            if(button_position[row][col+2] == 0 && button_position[row+1][col+2] == 0) {
                                button.layout(button.getLeft()+button_width, button.getTop(), button.getRight()+button_width, button.getBottom());
                                button_position[row][col+2] = 1;
                                button_position[row+1][col+2] = 1;
                                button_position[row][col] = 0;
                                button_position[row+1][col] = 0;
                                String temp_position = row + "" + (col+1) + "" + 4;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                }
                break;
            case down:
                switch (block_num) {
                    case 1:
                        try {
                            if(button_position[row+1][col] == 0) {
                                button.layout(button.getLeft(), button.getTop()+button_height, button.getRight(), button.getBottom()+button_height);
                                button_position[row+1][col] = 1;
                                button_position[row][col] = 0;
                                String temp_position = (row+1) + "" + col + "" + 1;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                    case 2:
                        try {
                            if(button_position[row+2][col] == 0) {
                                button.layout(button.getLeft(), button.getTop()+button_height, button.getRight(), button.getBottom()+button_height);
                                button_position[row+2][col] = 1;
                                button_position[row][col] = 0;
                                String temp_position = (row+1) + "" + col + "" + 2;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                    case 3:
                        try {
                            if(button_position[row+1][col] == 0 && button_position[row+1][col+1] == 0) {
                                button.layout(button.getLeft(), button.getTop()+button_height, button.getRight(), button.getBottom()+button_height);
                                button_position[row+1][col] = 1;
                                button_position[row+1][col+1] = 1;
                                button_position[row][col] = 0;
                                button_position[row][col+1] = 0;
                                String temp_position = (row+1) + "" + col + "" + 3;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                    case 4:
                        try {
                            if(button_position[row+2][col] == 0 && button_position[row+2][col+1] == 0) {
                                button.layout(button.getLeft(), button.getTop()+button_height, button.getRight(), button.getBottom()+button_height);
                                button_position[row+2][col] = 1;
                                button_position[row+2][col+1] = 1;
                                button_position[row][col] = 0;
                                button_position[row][col+1] = 0;
                                String temp_position = (row+1) + "" + col + "" + 4;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                }
                break;
            case left:
                switch (block_num) {
                    case 1:
                        try {
                            if(button_position[row][col-1] == 0) {
                                button.layout(button.getLeft()-button_width, button.getTop(), button.getRight()-button_width, button.getBottom());
                                button_position[row][col-1] = 1;
                                button_position[row][col] = 0;
                                String temp_position = row + "" + (col-1) + "" + 1;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                    case 2:
                        try {
                            if(button_position[row][col-1] == 0 && button_position[row+1][col-1] == 0) {
                                button.layout(button.getLeft()-button_width, button.getTop(), button.getRight()-button_width, button.getBottom());
                                button_position[row][col-1] = 1;
                                button_position[row+1][col-1] = 1;
                                button_position[row][col] = 0;
                                button_position[row+1][col] = 0;
                                String temp_position = row + "" + (col-1) + "" + 2;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                    case 3:
                        try {
                            if(button_position[row][col-1] == 0) {
                                button.layout(button.getLeft()-button_width, button.getTop(), button.getRight()-button_width, button.getBottom());
                                button_position[row][col-1] = 1;
                                button_position[row][col+1] = 0;
                                String temp_position = row + "" + (col-1) + "" + 3;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                        break;
                    case 4:
                        try {
                            if(button_position[row][col-1] == 0 && button_position[row+1][col-1] == 0) {
                                button.layout(button.getLeft()-button_width, button.getTop(), button.getRight()-button_width, button.getBottom());
                                button_position[row][col-1] = 1;
                                button_position[row+1][col-1] = 1;
                                button_position[row][col+1] = 0;
                                button_position[row+1][col+1] = 0;
                                String temp_position = row + "" + (col-1) + "" + 4;
                                button.setHint(temp_position);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage()); }break;
                }break;
        }
    }

    public void withdrawOperation(View view) {
        if(!records.isEmpty()) {
            int[] record = records.pop();
            Button button = findViewById(record[0]);
            int[] new_position = getPosition(button);
            int row = new_position[0];
            int col = new_position[1];
            int type = record[1];
            int direction = record[2];
            int old_score = record[3];
            if(old_score != score) {
                current_score.setText("当前步数: "+ old_score);
                score = old_score;
            }
            if(direction == right) {
                button.setHint(row + "" + (col-1) + "" + type);
                button.layout(button.getLeft()-button_width, button.getTop(), button.getRight()-button_width, button.getBottom());
                switch (type) {
                    case 1:
                        button_position[row][col-1] = 1;
                        button_position[row][col] = 0;
                        break;
                    case 2:
                        button_position[row][col-1] = 1;
                        button_position[row+1][col-1] = 1;
                        button_position[row][col] = 0;
                        button_position[row+1][col] = 0;
                        break;
                    case 3:
                        button_position[row][col-1] = 1;
                        button_position[row][col+1] = 0;
                        break;
                    case 4:
                        button_position[row][col-1] = 1;
                        button_position[row+1][col-1] = 1;
                        button_position[row][col+1] = 0;
                        button_position[row+1][col+1] = 0;
                        break;
                }

            }
            else if(direction == left) {
                button.setHint(row + "" + (col+1) + "" + type);
                button.layout(button.getLeft()+button_width, button.getTop(), button.getRight()+button_width, button.getBottom());
                switch (type) {
                    case 1:
                        button_position[row][col+1] = 1;
                        button_position[row][col] = 0;
                        break;
                    case 2:
                        button_position[row][col+1] = 1;
                        button_position[row+1][col+1] = 1;
                        button_position[row][col] = 0;
                        button_position[row+1][col] = 0;
                        break;
                    case 3:
                        button_position[row][col+2] = 1;
                        button_position[row][col] = 0;
                        break;
                    case 4:
                        button_position[row][col+2] = 1;
                        button_position[row+1][col+2] = 1;
                        button_position[row][col] = 0;
                        button_position[row+1][col] = 0;
                        break;
                }

            }
            else if(direction == up) {
                button.setHint((row+1) + "" + col + "" + type);
                button.layout(button.getLeft(), button.getTop()+button_height, button.getRight(), button.getBottom()+button_height);
                switch (type) {
                    case 1:
                        button_position[row+1][col] = 1;
                        button_position[row][col] = 0;
                        break;
                    case 2:
                        button_position[row+2][col] = 1;
                        button_position[row][col] = 0;
                        break;
                    case 3:
                        button_position[row+1][col] = 1;
                        button_position[row+1][col+1] = 1;
                        button_position[row][col] = 0;
                        button_position[row][col+1] = 0;
                        break;
                    case 4:
                        button_position[row+2][col] = 1;
                        button_position[row+2][col+1] = 1;
                        button_position[row][col] = 0;
                        button_position[row][col+1] = 0;
                        break;
                }
            }
            else {
                button.layout(button.getLeft(), button.getTop()-button_height, button.getRight(), button.getBottom()-button_height);
                button.setHint((row-1) + "" + col + "" + type);
                switch (type) {
                    case 1:
                        button_position[row-1][col] = 1;
                        button_position[row][col] = 0;
                        break;
                    case 2:
                        button_position[row-1][col] = 1;
                        button_position[row+1][col] = 0;
                        break;
                    case 3:
                        button_position[row-1][col] = 1;
                        button_position[row-1][col+1] = 1;
                        button_position[row][col] = 0;
                        button_position[row][col+1] = 0;
                        break;
                    case 4:
                        button_position[row-1][col] = 1;
                        button_position[row-1][col+1] = 1;
                        button_position[row+1][col] = 0;
                        button_position[row+1][col+1] = 0;
                        break;
                }
            }
        }
    }

    public void returnReply(View view) {
        Intent replyIntent = new Intent();
        Log.i("returnReply", scene_num);
        replyIntent.putExtra(reply, scene_num);
        replyIntent.putExtra(reply_score, score+"");
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    public void backChoose(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
