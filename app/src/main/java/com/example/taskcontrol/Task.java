package com.example.taskcontrol;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Data.DataBaseHandler;

public class Task extends AppCompatActivity {

    TextView calendar_date,notify_tv;
    String user_to;
    EditText dateTime, taskText, TaskName,notify_date,notify_time;
    TextView time_long;
    Spinner spinner;
    Button button,notify_button;
    String uname;
    String uuuuname;
    private DatabaseReference mDataBase, usersDB,taskDB;

    HashMap<String, String> map = new HashMap<>();
    public static int id_notification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id_notification = 1;
        setContentView(R.layout.activity_task);
        button = findViewById(R.id.createTaskButton);
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

        createNotificationChannel();

        notify_tv = findViewById(R.id.notify_long_time);
        notify_button = findViewById(R.id.notify_button);
        calendar_date = findViewById(R.id.calendar_date);
        dateTime = findViewById(R.id.editTextTime2);
        taskText = findViewById(R.id.editTextTextPersonName);
        time_long = findViewById(R.id.time_long);
        spinner = findViewById(R.id.spinner);
        TaskName = findViewById(R.id.editTextTaskName);



        String date = String.valueOf(DateFormat.format("dd.MM.yyyy", new Date()));

        calendar_date.setText(""+date);
        long threeHours = 3*1000*60*60;
        long now = System.currentTimeMillis();
        long today = now - now % 86400000 - threeHours;
        time_long.setText(""+today);


        CompactCalendarView compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        compactCalendar.shouldSelectFirstDayOfMonthOnScroll(false);
        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDayClick(Date dateClicked) {
                String[] words = (dateClicked.toString()).split(" ");
                String date =  words[2] + "." + map.get(words[1]) + "." +  words[5];
                Log.d("DATE","DATE:" + date);
//                long threeHours = 3*1000*60*60;
//                long niceDate = dateClicked.getTime() - threeHours;
                calendar_date.setText(""+date);
                time_long.setText(""+dateClicked.getTime());
                dateTime.setText("");
                taskText.setText("");

//
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
            }
        });






    }

    public void nowButton(View view) {
        Intent changeOnNowIntent = new Intent(Task.this, MainActivity.class);
        startActivity(changeOnNowIntent);
    }

    public void createTeak(View view) {

        String date = (String) calendar_date.getText();
        String time = dateTime.getText().toString();
        String taskDesc = taskText.getText().toString();
        String timeLong = time_long.getText().toString();
        String priority = spinner.getSelectedItem().toString();
        String taskName = TaskName.getText().toString();
        String notify_time = notify_tv.getText().toString();

        int keyPriority;
        if (priority.equals("Наименее важная")){
            keyPriority = 3;
        } else if(priority.equals("Важная")){
            keyPriority = 2;
        }else{
            keyPriority = 1;
        }
        long threeHourses = 3*1000*60*60;
        long now = System.currentTimeMillis();
        long today = now - now % 86400000 - threeHourses;

        if (Long.parseLong(timeLong) < today){
            Toast.makeText(this,"Нельзя ставить задачу задним числом",Toast.LENGTH_SHORT).show();
            return;
        }
        if(time.equals("")){
            String[] dateArray = date.split("\\.");
            int day =  Integer.parseInt(dateArray[0]);
            int month = Integer.parseInt(dateArray[1]);
            int year = Integer.parseInt(dateArray[2]);
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month-1, day,00,00,00);
            Date happyNewYearDate = calendar.getTime();
            long timeMilli = happyNewYearDate.getTime();
            time = "00:00";
            timeLong = String.valueOf(timeMilli);

        }

        Toast.makeText(this,"Задача создана",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Task.this, ReminderBroadcast.class);
        intent.putExtra("taskName",taskName);
        intent.putExtra("taskDesc", taskDesc);
        intent.putExtra("time",time);
        intent.putExtra("id_notification",id_notification);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Task.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long timeButtonClick = System.currentTimeMillis();
        Log.d("this time:",""+timeButtonClick);
        long threeHours = 3*1000*60*60;
        long tenSec = 1000 * 1;
        long not_time;
        if (!notify_time.equals("")){
            not_time = Long.parseLong(notify_time);
            Log.d("asdasdasdasdasdsa","asdasdasd"+not_time);
            alarmManager.set(AlarmManager.RTC_WAKEUP,not_time, pendingIntent);
        }else{
            not_time = 0L;
        }



        long getTime = Long.parseLong(timeLong);
        String[] hoursMinutes = (time).split(":");
        int hour = Integer.parseInt(hoursMinutes[0]);
        int minutes = Integer.parseInt(hoursMinutes[1]);

        long niceTimeHour = hour * 1000 * 60 * 60;
        long niceTimeMinute = minutes * 1000 * 60;


        long niceTime = getTime + niceTimeHour + niceTimeMinute - threeHours;
        String NotifyTime = String.valueOf(not_time);



        DataBaseHandler dataBaseHandler = new DataBaseHandler(this);
        if (user_to != null) {
            taskDB = FirebaseDatabase.getInstance().getReference("TASK");
            String my_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            taskDB.push().setValue(new TaskDB(date,time,timeLong,taskDesc,keyPriority,NotifyTime,taskName,user_to,my_id,uuuuname,"Отправлено"));
        } else {
            dataBaseHandler.addTask(new Record.Task(date,time,timeLong,taskDesc,keyPriority,NotifyTime,taskName));
        }


//
//        List<Record.Task> taskList = dataBaseHandler.getAllTasks();
//
//        for (Record.Task task : taskList){
//            Log.d("TaskInfo","\nDate: "+task.getDate() + "\nTime For Event : "+task.getTime()
//                    +"\nTime Long: "+ task.getTimelong() + "\nTask Name: " + task.getTaskname()
//                    + "\nTask Text: " + task.getText()
//                    + "\nTask Priority: " + task.getPriority() + "\nTime For Notification:  " + NotifyTime
//                    + "\n----------------------------"
//            );
//        }
        id_notification ++;




    }


    public void changeOnLogin(View view) {
        Intent changeOnNowIntent = new Intent(Task.this, Login.class);
        startActivity(changeOnNowIntent);

    }

    private void  createNotificationChannel(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "ReminderChannel";
            String description = "Chanel for Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("zxc",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void notify_button_click(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Уведомление");
        dialog.setMessage("Введите дату у время уведомления");

        LayoutInflater inflater = LayoutInflater.from(this);
        View notify_window = inflater.inflate(R.layout.notify_layout,null);
        notify_date = notify_window.findViewById(R.id.date_notify);
        notify_time = notify_window.findViewById(R.id.time_notify);
        dialog.setView(notify_window);
        dialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

            }
        });
        dialog.setPositiveButton("Принять", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String not_date = notify_date.getText().toString();
                String not_time = notify_time.getText().toString();
                String[] dateArray = not_date.split("/");
                int day =  Integer.parseInt(dateArray[0]);
                int month = Integer.parseInt(dateArray[1]);
                int year = Integer.parseInt(dateArray[2]);
                String[] timeArray = not_time.split(":");
                int hour =  Integer.parseInt(timeArray[0]);
                int min = Integer.parseInt(timeArray[1]);
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month-1, day,hour,min,00);
                Date happyNewYearDate = calendar.getTime();
                long timeMilli = happyNewYearDate.getTime();
                notify_tv.setText(String.valueOf(timeMilli));
            }
        });

        dialog.show();
    }

    public void where_task_button(View view) {
        if( FirebaseAuth.getInstance().getCurrentUser() != null){
            mDataBase = FirebaseDatabase.getInstance().getReference("RELATIONS");
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Исполнитель задачи");
            dialog.setMessage("Выберите исполнителя задачи");
            LayoutInflater inflater = LayoutInflater.from(this);
            View where_window = inflater.inflate(R.layout.wheretasklayout,null);
            dialog.setView(where_window);
            String my_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            usersDB = FirebaseDatabase.getInstance().getReference("USER");
            mDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot childSnap: snapshot.getChildren()){
                        String id_to = (String) childSnap.child("id_from").getValue();
                        if (id_to.equals(my_id)){
                            String id_from = (String) childSnap.child("id_to").getValue();
                            Log.d("asdasd","qweqweqwe");
                            usersDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot childSnaps: snapshot.getChildren()){
                                        String id = (String) childSnaps.child("id").getValue();
                                        if (id.equals(id_from)){
                                            Log.d("asdasd","YES");
                                            String username = (String) childSnaps.child("username").getValue();
                                            uuuuname = username;
                                            uname = id_from;
                                            List<String> spinnerArray =  new ArrayList<String>();
                                            spinnerArray.add(username);
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, spinnerArray);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Spinner sItems = (Spinner) where_window.findViewById(R.id.spinneruname);
                                            sItems.setAdapter(adapter);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            dialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.dismiss();

                }
            });
            dialog.setPositiveButton("Принять", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    user_to = uname;
                }
            });
            dialog.show();
        }
    }
}