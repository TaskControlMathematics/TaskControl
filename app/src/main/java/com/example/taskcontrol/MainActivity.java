package com.example.taskcontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.usage.UsageEvents;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import Data.DataBaseHandler;

import static com.example.taskcontrol.R.drawable.*;

public class MainActivity extends AppCompatActivity {
    DataBaseHandler dataBaseHandler = new DataBaseHandler(this);
    HashMap<String, String> map = new HashMap<>();
    CompactCalendarView compactCalendar;
    private DatabaseReference mDataBase, usersDB,taskDB;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MM", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources res = getResources();
        Drawable redBorder = ResourcesCompat.getDrawable(res, red_border, null);
        Drawable yellowBorder = ResourcesCompat.getDrawable(res, yellow_border, null);
        Drawable greenBorder = ResourcesCompat.getDrawable(res, green_border, null);
        map.put("Jan", "01");
        map.put("Feb", "02");
        map.put("Mar", "03");
        map.put("Apr", "04");
        map.put("May", "05");
        map.put("Jun", "06");
        map.put("Jul", "07");
        map.put("Aug", "08");
        map.put("Sep", "09");
        map.put("Oct", "10");
        map.put("Nov", "11");
        map.put("Dec", "12");



        if( FirebaseAuth.getInstance().getCurrentUser() != null){
            String my_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            taskDB = FirebaseDatabase.getInstance().getReference("TASK");
            taskDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot childSnaps: snapshot.getChildren()){
                        String id = (String) childSnaps.child("id_to").getValue();
                        if (id.equals(my_id)){
                            DataBaseHandler dataBaseHandler = new DataBaseHandler(MainActivity.this);
                            String date = (String) childSnaps.child("date").getValue();
                            String time = (String) childSnaps.child("time").getValue();
                            String timeLong = (String) childSnaps.child("timelong").getValue();
                            String taskDesc = (String) childSnaps.child("text").getValue();
                            String keyPriority = String.valueOf((Long) childSnaps.child("priority").getValue());
                            String NotifyTime = (String) childSnaps.child("timelongtask").getValue();
                            String taskName = (String) childSnaps.child("taskname").getValue();
                            Log.d("TASK",date+"/"+time+"/"+timeLong+"/"+taskDesc+"/"+keyPriority+"/"+NotifyTime+"/"+taskName);
                            int flag = 0;
                            List<Record.Task> taskList = dataBaseHandler.getAllTasks();
                            for (Record.Task task : taskList){
                                if (task.getTaskname().equals(taskName) & task.getDate().equals(date)){
                                    flag = 1;
                                }
                            }
                            if (flag == 0){
                                dataBaseHandler.addTask(new Record.Task(date,time,timeLong,taskDesc,Integer.parseInt(keyPriority),NotifyTime,taskName));
                                childSnaps.child("status").getRef().setValue("Просмотрено");
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        List<Record.Task> taskList = dataBaseHandler.getAllTasks();
        LinearLayout layoutTasks = findViewById(R.id.linearLayoutTasks);
        layoutTasks.removeAllViews();
        String curr_date = String.valueOf(android.text.format.DateFormat.format("dd.MM.yyyy", new java.util.Date()));
        String currMonthDate = String.valueOf(android.text.format.DateFormat.format("MM", new java.util.Date()));

        TextView currMonth = findViewById(R.id.thisMonth);
        currMonth.setText(currMonthDate);
        for (Record.Task task : taskList){
            if ((task.getDate()).equals(curr_date)){

                LinearLayout linearTask = new LinearLayout(MainActivity.this);
                linearTask.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(0, 0, 0, 10);
                linearTask.setLayoutParams(layoutParams);
                int priority = task.getPriority();
                if (priority == 3){
                    linearTask.setBackground(greenBorder);
                }else if(priority == 2){
                    linearTask.setBackground(yellowBorder);
                }else{
                    linearTask.setBackground(redBorder);
                }
//                create Views
                TextView textViewName = new TextView(this);
                TextView textViewTime = new TextView(this);
                TextView textViewText = new TextView(this);

//                counter view setting
                textViewName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                textViewName.setTypeface(Typeface.DEFAULT_BOLD);
                textViewName.setTextColor(Color.BLACK);
                textViewName.setText(""+task.getTaskname());

//                time view settings
                textViewTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                if (task.getTime().equals("00:00")){

                }else{
                    textViewTime.setText("Time: "+task.getTime());
                }


//                description view setting
                textViewText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                textViewText.setText("Task Description:\n"+task.getText()+"\n");

//               add views on layout
                linearTask.addView(textViewName);
                linearTask.addView(textViewTime);
                linearTask.addView(textViewText);
                layoutTasks.addView(linearTask);


            }

        }


        compactCalendar = (CompactCalendarView) findViewById(R.id.nowCalendarView);
        compactCalendar.shouldSelectFirstDayOfMonthOnScroll(false);

        HashMap<String, String> countMap = dataBaseHandler.getCountDate();
        ArrayList<HashMap.Entry> entries = new ArrayList<>(countMap.entrySet());
        for (HashMap.Entry entry : entries) {
            int count = Integer.parseInt((String) entry.getValue());
            long date = Long.parseLong((String) entry.getKey())  ;
            if (count > 7){

                Event event1 = new Event(Color.argb(70,255,0,0),date,"event1");
                compactCalendar.addEvent(event1);

            }else if(count >= 1 & count <4 ){
                Event event2 = new Event(Color.argb(70,0,255,0),date,"event2");
                compactCalendar.addEvent(event2);
            }else if(count >=4 & count <=7){
                Event event3 = new Event(Color.argb(70,255,255,0),date,"event3");
                compactCalendar.addEvent(event3);
            }
            Log.d("COUNT","COUNT:" + count);
            Log.d("COUNT","VALUE:" + date);
        }



        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                LinearLayout layoutTasks = findViewById(R.id.linearLayoutTasks);
                layoutTasks.removeAllViews();
                String[] words = (dateClicked.toString()).split(" ");
                String date =  words[2] + "." + map.get(words[1]) + "." +  words[5];
                for (Record.Task task:taskList ){
                    if ((task.getDate()).equals(date)){
                        LinearLayout linearTask = new LinearLayout(MainActivity.this);
                        linearTask.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        layoutParams.setMargins(0, 0, 0, 10);
                        linearTask.setLayoutParams(layoutParams);
                        int priority = task.getPriority();
                        if (priority == 3){
                            linearTask.setBackground(greenBorder);
                        }else if(priority == 2){
                            linearTask.setBackground(yellowBorder);
                        }else{
                            linearTask.setBackground(redBorder);
                        }

//                create Views

                        TextView textViewName = new TextView(MainActivity.this);
                        TextView textViewTime = new TextView(MainActivity.this);
                        TextView textViewText = new TextView(MainActivity.this);

//                counter view setting
                        textViewName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                        textViewName.setTypeface(Typeface.DEFAULT_BOLD);
                        textViewName.setTextColor(Color.BLACK);
                        textViewName.setText(""+task.getTaskname());

//                time view settings
                        textViewTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                        if(task.getTime().equals("00:00")) {

                        }else{
                            textViewTime.setText("Time: "+task.getTime());
                        }


//                description view setting
                        textViewText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                        textViewText.setText("Task Description:\n"+task.getText()+"\n");

//               add views on layout
                        linearTask.addView(textViewName);
                        linearTask.addView(textViewTime);
                        linearTask.addView(textViewText);
                        layoutTasks.addView(linearTask);


                    }
                }

//
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                currMonth.setText(""+dateFormatMonth.format(firstDayOfNewMonth));

            }
        });

        long now = System.currentTimeMillis();
        long threeHours = 3*1000*60*60;
        long today = now - now % 86400000 - threeHours;
        Log.d("TODAY",""+today);
        long todayPlusOne = today + 86400000;
        Log.d("PLUS ONE",""+todayPlusOne);

        long todayPlusTwo = todayPlusOne + 86400000;
        long todayPlusThree = todayPlusTwo + 86400000;
        long todayPlusFour = todayPlusThree + 86400000;
        long todayPlusFive = todayPlusFour + 86400000;
        long todayPlusSix = todayPlusFive + 86400000;

        long[] weekTasks = { today, todayPlusOne, todayPlusTwo, todayPlusThree, todayPlusFour, todayPlusFive, todayPlusSix };
        LinearLayout linearWeekTasks = findViewById(R.id.linearWeekTasks);

        for (long dayTask : weekTasks){
            List<Record.Task> taskListToday = dataBaseHandler.getImportantTasks(String.valueOf(dayTask));
            for (Record.Task task : taskListToday) {
                TextView textViewName = new TextView(this);
                TextView textViewDate = new TextView(this);
                TextView textViewTime = new TextView(this);
                TextView textViewText = new TextView(this);

                textViewName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                textViewName.setTypeface(Typeface.DEFAULT_BOLD);
                textViewName.setTextColor(Color.BLACK);
                textViewName.setText(""+task.getTaskname());

                textViewDate.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                textViewDate.setText("Date: " + task.getDate());


                textViewTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                if (task.getTime().equals("00:00")){

                }else{
                    textViewTime.setText("Time: "+task.getTime());
                }


//                description view setting
                textViewText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                textViewText.setText("Task Description:\n"+task.getText()+"\n");

//               add views on layout
                linearWeekTasks.addView(textViewName);
                linearWeekTasks.addView(textViewDate);
                linearWeekTasks.addView(textViewTime);
                linearWeekTasks.addView(textViewText);


                }

            }
    }


    public void changeOnTask(View view) {
        Intent changeOnTaskIntent = new Intent(MainActivity.this, Task.class);
        startActivity(changeOnTaskIntent);
    }

    public void changeOnLogin(View view) {
        Intent changeOnTaskIntent = new Intent(MainActivity.this, Login.class);
        startActivity(changeOnTaskIntent);
    }

    public void prevMonth(View view) {
        compactCalendar.scrollLeft();
    }

    public void nextMonth(View view) {
        compactCalendar.scrollRight();
    }
}