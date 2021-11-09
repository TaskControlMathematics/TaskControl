package Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Record.Task;
import Utils.Util;

public class DataBaseHandler extends SQLiteOpenHelper {
    public DataBaseHandler(Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASK_TABLE = "CREATE TABLE " + Util.TABLE_NAME + "("
                + Util.KEY_ID + " INTEGER PRIMARY KEY,"
                + Util.KEY_DATE + " TEXT,"
                + Util.KEY_TIME + " TEXT,"
                + Util.KEY_TIMELONG + " TEXT,"
                + Util.KEY_TEXT + " TEXT,"
                + Util.KEY_PRIORITY + " INT,"
                + Util.KEY_TIMELONGTASK + " TEXT,"
                + Util.KEY_TASKNAME + " TEXT" + ")";

        db.execSQL(CREATE_TASK_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Util.TABLE_NAME);
        onCreate(db);
    }

    public void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.KEY_DATE, task.getDate());
        contentValues.put(Util.KEY_TIME, task.getTime());
        contentValues.put(Util.KEY_TIMELONG, task.getTimelong());
        contentValues.put(Util.KEY_TEXT, task.getText());
        contentValues.put(Util.KEY_PRIORITY, task.getPriority());
        contentValues.put(Util.KEY_TIMELONGTASK, task.getTimelongtask());
        contentValues.put(Util.KEY_TASKNAME,task.getTaskname());

        db.insert(Util.TABLE_NAME, null, contentValues);
        db.close();

    }

    public Task getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Util.TABLE_NAME, new String[]{Util.KEY_ID, Util.KEY_DATE, Util.KEY_TIME, Util.KEY_TIMELONG,
                        Util.KEY_TEXT,Util.KEY_PRIORITY,Util.KEY_TIMELONGTASK,Util.KEY_TASKNAME}, Util.KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null,
                null, null);

        if (cursor != null){
            cursor.moveToFirst();

        }
        Task task  = new Task(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),cursor.getString(2),
                cursor.getString(3),cursor.getString(4),
                Integer.parseInt(cursor.getString(5)),cursor.getString(6),
                cursor.getString(7));
        return task;
    }

    public List<Task> getAllTasks(){
        SQLiteDatabase db = this.getReadableDatabase();

        List<Task> taskList = new ArrayList<>();

        String selectAllTasks = "SELECT * FROM "+ Util.TABLE_NAME + " ORDER BY " + Util.KEY_TIMELONGTASK + "," + Util.KEY_PRIORITY;

        Cursor cursor = db.rawQuery(selectAllTasks,null);
        if (cursor.moveToFirst()){
            do{
                Task task = new Task();
                task.setId(Integer.parseInt(cursor.getString(0)));
                task.setDate(cursor.getString(1));
                task.setTime(cursor.getString(2));
                task.setTimelong(cursor.getString(3));
                task.setText(cursor.getString(4));
                task.setPriority(Integer.parseInt(cursor.getString(5)));
                task.setTimelongtask(cursor.getString(6));
                task.setTaskname(cursor.getString(7));

                taskList.add(task);

            }while (cursor.moveToNext());
        }

        return taskList;
    }
    public List<Task> getImportantTasks(String longTome){
        SQLiteDatabase db = this.getReadableDatabase();

        List<Task> taskList = new ArrayList<>();



        String selectAllTasks = "SELECT * FROM "+ Util.TABLE_NAME + " WHERE " + Util.KEY_TIMELONG
                + " = " + longTome + " AND " + Util.KEY_PRIORITY + " = 1"
                + " ORDER BY " + Util.KEY_TIMELONGTASK;

        Cursor cursor = db.rawQuery(selectAllTasks,null);
        if (cursor.moveToFirst()){
            do{
                Task task = new Task();
                task.setId(Integer.parseInt(cursor.getString(0)));
                task.setDate(cursor.getString(1));
                task.setTime(cursor.getString(2));
                task.setTimelong(cursor.getString(3));
                task.setText(cursor.getString(4));
                task.setPriority(Integer.parseInt(cursor.getString(5)));
                task.setTimelongtask(cursor.getString(6));
                task.setTaskname(cursor.getString(7));

                taskList.add(task);

            }while (cursor.moveToNext());
        }

        return taskList;
    }


    public HashMap getCountDate(){
        SQLiteDatabase db = this.getReadableDatabase();


        String selectAllTasks = "SELECT " + Util.KEY_TIMELONG + ",COUNT(*) AS count FROM " + Util.TABLE_NAME + " GROUP BY "+Util.KEY_TIMELONG ;

        HashMap<String, String> map = new HashMap<>();

        Cursor cursor = db.rawQuery(selectAllTasks,null);
        if (cursor.moveToFirst()){
            do{
                map.put(cursor.getString(0),cursor.getString(1));

            }while (cursor.moveToNext());
        }

        return map;
    }
}
