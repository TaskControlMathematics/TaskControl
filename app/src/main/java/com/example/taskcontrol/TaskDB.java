package com.example.taskcontrol;

public class TaskDB {
    public String date;
    public String time;
    public String timelong;
    public String text;
    public Integer priority;
    public String timelongtask;
    public String taskname;
    public String id_to;
    public String id_from;
    public String uname;
    public String status;

    public TaskDB() {
    }

    public TaskDB( String date, String time, String timelong, String text, Integer priority, String timelongtask, String taskname,String id_to,String id_from,String uname,String status) {
        this.date = date;
        this.time = time;
        this.timelong = timelong;
        this.text = text;
        this.priority = priority;
        this.timelongtask = timelongtask;
        this.taskname = taskname;
        this.id_to = id_to;
        this.id_from = id_from;
        this.uname = uname;
        this.status = status;

    }
}
