package Record;

import java.sql.Struct;

public class Task {
    public int id ;
    public String date;
    public String time;
    public String timelong;
    public String text;
    public Integer priority;
    public String timelongtask;
    public String taskname;

    public Task() {
    }

    public Task(int id, String date, String time, String timelong, String text, Integer priority, String timelongtask, String taskname) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.timelong = timelong;
        this.text = text;
        this.priority = priority;
        this.timelongtask = timelongtask;
        this.taskname = taskname;
    }

    public Task(String date, String time, String timelong, String text, Integer priority, String timelongtask, String taskname) {
        this.date = date;
        this.time = time;
        this.timelong = timelong;
        this.text = text;
        this.priority = priority;
        this.timelongtask = timelongtask;
        this.taskname = taskname;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimelong() {
        return timelong;
    }

    public void setTimelong(String timelong) {
        this.timelong = timelong;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getTimelongtask() {
        return timelongtask;
    }

    public void setTimelongtask(String timelongtask) {
        this.timelongtask = timelongtask;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }
}
