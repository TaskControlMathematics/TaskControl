package com.example.taskcontrol;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Configurate extends AppCompatActivity {

    private ConnectPGSQL con = new ConnectPGSQL();

    TextView usernameTextView, count_requ;
    private FirebaseDatabase mDataBase;
    LinearLayout root_layout_send_request;
    RelativeLayout root;
    int post;
    int count_req;
    String id_from;
    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurate);

        root = findViewById(R.id.root_element_configurate);
        Bundle arguments = getIntent().getExtras();
        String username = arguments.get("USERNAME").toString();
        id_from = arguments.get("ID").toString();
        mDataBase = FirebaseDatabase.getInstance();
        DatabaseReference ref = mDataBase.getReference("REQUESTS");
        count_req = 0;

        Log.d("req",""+count_req);
        count_requ = findViewById(R.id.count_requests);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnap: snapshot.getChildren()){
                    String id_to  = (String) childSnap.child("id_to").getValue();
                    if (id_to.equals(id_from)){
                        Log.d("count_req","asd");
                        count_req = count_req + 1;
                        count_requ.setText("Количество:"+String.valueOf(count_req));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        DatabaseReference ref = mDataBase.getReference();
//        Log.d("USERNAME",username);

//        String count_requests = "null";
//        try {
//            Statement st = con.conexionBD().createStatement();
//            String sql;
//            sql = "select count(*) from public.requests where id_to=" + id_from + ";";
//            ResultSet rs = st.executeQuery(sql);
//            while (rs.next()){
//                count_requests = rs.getString("count");
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        Log.d("count_requests",count_requests);
//        TextView count_TV = findViewById(R.id.count_requests);
//        count_TV.setText("<-----("+count_requests+")----->");

        usernameTextView = findViewById(R.id.USERNAME);
        usernameTextView.setText(username);

        root_layout_send_request = findViewById(R.id.root_layout_send_request);

    }
    public void changeOnTask(View view) {
        Intent changeOnNowIntent = new Intent(Configurate.this, Task.class);
        startActivity(changeOnNowIntent);
    }

    public void changeOnNow(View view) {
        Intent changeOnNowIntent = new Intent(Configurate.this, MainActivity.class);
        startActivity(changeOnNowIntent);
    }

    public void sendRequestBtn(View view) {
        post = 0;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Отправить запрос");
        dialog.setMessage("Введите email и выберите должность");

        LayoutInflater inflater = LayoutInflater.from(this);

        View send_request_window = inflater.inflate(R.layout.send_request,null);

        EditText editPhone = send_request_window.findViewById(R.id.phone_send_request);
        Switch switchPost = send_request_window.findViewById(R.id.post_send_request);

        switchPost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean checked = ((Switch) v).isChecked();
                if (checked) {
                    post = 1;
                }else{
                    post = 0;
                }
            }
        });




        dialog.setView(send_request_window);


        dialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

            }
        });


        dialog.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String phone = editPhone.getText().toString();
                Log.d("on_click_phone",""+phone);
                Log.d("on_click_post",""+post);
                if(TextUtils.isEmpty(editPhone.getText().toString())){
                    Snackbar.make(root_layout_send_request,"edit phone",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                List<String> arrayFrom= new ArrayList<String>();
                List<String> arrayTo = new ArrayList<String>();
                List<Integer> arrayId = new ArrayList<Integer>();
                DatabaseReference ref = mDataBase.getReference("REQUESTS");
                DatabaseReference users = mDataBase.getReference("USER");
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot childSnap: snapshot.getChildren()){
                            String email_to = (String) childSnap.child("email").getValue();
                            if (phone.equals(email_to)){
                                String id_to = (String) childSnap.child("id").getValue();
                                requests reqest = new requests(id_to,id_from,post);
                                ref.push().setValue(reqest);
                                Snackbar.make(root,"Запрос отправлен",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                Integer id_to = 0;
//                try {
//                    Statement st = con.conexionBD().createStatement();
//                    String sql;
//                    sql = "select id from public.profiles where phone=\'"+ phone +"\';";
//                    ResultSet rs = st.executeQuery(sql);
//
//                    while (rs.next()){
//                        Integer id_request = Integer.parseInt(rs.getString("id"));
//                        id_to = id_request;
//                    }
//                } catch (SQLException throwables) {
//                    throwables.printStackTrace();
//                }

//                boolean not_request = true;
//                try {
//                    Statement st = con.conexionBD().createStatement();
//                    String sql;
//                    sql = "select * from public.requests;";
//                    ResultSet rs = st.executeQuery(sql);
//                    while (rs.next()){
//
//                        Integer id_request = Integer.parseInt(rs.getString("id"));
//                        String from = rs.getString("id_from");
//                        String to = rs.getString("id_to");
//                        Log.d("asdasdasd","asdasdasdasdasd");
//                        arrayFrom.add(from);
//                        arrayTo.add(to);
//                        arrayId.add(id_request);
//                    }
//                    String new_sql = "select * from public.requests where id_from="+ Integer.parseInt(id_from) + " and id_to=" + id_to+";";
//                    ResultSet rs1 = st.executeQuery(new_sql);
//                    while (rs1.next()){
//                        not_request = false;
//                        Log.d("IS","такой запрос уже существует");
//                        Snackbar.make(root,"такой запрос уже существует",Snackbar.LENGTH_LONG).show();
//
//                    }
//
//                } catch (SQLException throwables) {
//                    throwables.printStackTrace();
//                }
//
//
//                String maxId = String.valueOf(Collections.max(arrayId) + 1);
//                Log.d("ID_",""+maxId);
//                Log.d("ID_FROM",""+id_from);
//                Log.d("ID_TO",""+id_to);
//                Log.d("STATUS",""+post);
//                if (not_request){
//                    try {
//                    Statement st = con.conexionBD().createStatement();
//                    String sql;
//                    sql = "insert into public.requests(id,id_from,id_to,status) values ("+maxId+","
//                            + id_from + ","+id_to + ","+post+");";
//
//                    st.executeUpdate(sql);
//                    Snackbar.make(root,"Запрос отправлен",Snackbar.LENGTH_LONG).show();
//
//                } catch (SQLException throwables) {
//                    throwables.printStackTrace();
//                }
//
//                }
            }
        });

        dialog.show();
    }

    public void myRequestsBtn(View view){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Входящие запросы");

        LayoutInflater inflater = LayoutInflater.from(this);

        View my_requests_window = inflater.inflate(R.layout.my_requests,null);
        dialog.setView(my_requests_window);


        DatabaseReference ref = mDataBase.getReference("REQUESTS");
        DatabaseReference users = mDataBase.getReference("USER");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            final List<String> arrayFrom= new ArrayList<String>();
            final List<String> arrayRelate = new ArrayList<String>();
            final List<String> usernames = new ArrayList<String>();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnap: snapshot.getChildren()){
                    String id_to = (String) childSnap.child("id_to").getValue();
                    if(id_to.equals(id_from)){
                        String relate = String.valueOf(childSnap.child("status").getValue());
                        String from = (String) childSnap.child("id_from").getValue();
                        arrayFrom.add(from);
                        arrayRelate.add(relate);
                        for (int i=0;i<arrayRelate.size();i++){
                            int finalI = i;
                            users.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot childSnap: snapshot.getChildren()){
                                        String id = (String) childSnap.child("id").getValue();
                                        if (id.equals(arrayFrom.get(finalI))){
                                            String uname = (String) childSnap.child("username").getValue();
                                            usernames.add(uname);
                                            Log.d("relate",""+arrayRelate);
                                            LinearLayout requests_layout = my_requests_window.findViewById(R.id.linear_requests);
                                            Log.d("CHECKpoiun","asd"+arrayRelate.size());
                                            for (int j=0;j<arrayRelate.size();j++){
                                                String relate = "null";
                                                if (arrayRelate.get(j).equals("0")){
                                                    relate = "Подчиненный";
                                                }else{
                                                    relate = "Начальник";
                                                }
                                                String username = usernames.get(j);

                                                LinearLayout linearTask = new LinearLayout(Configurate.this);
                                                linearTask.setOrientation(LinearLayout.HORIZONTAL);
                                                Button btnOk = new Button(Configurate.this);
                                                btnOk.setText("Да");
                                                int finalJ = j;
                                                btnOk.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        DatabaseReference rel = mDataBase.getReference("RELATIONS");
                                                        requests relationship = new requests(id_from,from,Integer.parseInt(arrayRelate.get(finalJ)));
                                                        rel.push().setValue(relationship);
                                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for (DataSnapshot childSnap: snapshot.getChildren()){
                                                                    String TO = (String) childSnap.child("id_to").getValue();
                                                                    String FROM = (String) childSnap.child("id_from").getValue();
                                                                    if (TO.equals(id_from) & FROM.equals(from)){
                                                                        childSnap.getRef().setValue(null);
                                                                        Intent intent = getIntent();
                                                                        finish();
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                            });
                                                Button btnNo = new Button(Configurate.this);
                                                btnNo.setText("Нет");
                                                btnNo.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for (DataSnapshot childSnap: snapshot.getChildren()){
                                                                    String TO = (String) childSnap.child("id_to").getValue();
                                                                    String FROM = (String) childSnap.child("id_from").getValue();
                                                                    if (TO.equals(id_from) & FROM.equals(from)){
                                                                        childSnap.getRef().setValue(null);
                                                                        Intent intent = getIntent();
                                                                        finish();
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                });
                                                TextView textViewName = new TextView(Configurate.this);
                                                textViewName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
                                                textViewName.setTextColor(Color.BLACK);
                                                textViewName.setText("От: "+username + " Отношение: "+ relate);
                                                linearTask.addView(textViewName);
                                                linearTask.addView(btnOk);
                                                linearTask.addView(btnNo);
                                                requests_layout.addView(linearTask);
                                            }
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        try {
//            Statement st = con.conexionBD().createStatement();
//            String sql = "select * from public.relationships;";
//            ResultSet rs = st.executeQuery(sql);
//            while (rs.next()){
//                Integer id = Integer.parseInt(rs.getString("id"));
//                arrayId.add(id);
//            }
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        String maxId = String.valueOf(Collections.max(arrayId) + 1);
//
//
//        try {
//            Statement st = con.conexionBD().createStatement();
//            String sql;
//            sql = "select * from public.requests where id_to="+id_from+" ;";
//            ResultSet rs = st.executeQuery(sql);
//            while(rs.next()){
//                Integer id = Integer.parseInt(rs.getString("id"));
//                String from = rs.getString("id_from");
//                String relate = rs.getString("status");
//                arrayRequestsId.add(id);
//                arrayFrom.add(from);
//                arrayRelate.add(relate);
//
//
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//
//        }
////        LinearLayout requests_layout = my_requests_window.findViewById(R.id.linear_requests);
//        for (int i=0;i<arrayFrom.size();i++){
//            String username = "null";
//            String phone = "null";
//            String relate = "null";
//            if (arrayRelate.get(i).equals(0)){
//                relate = "Подчиненный";
//            }else{
//                relate = "Начальник";
//            }
//            try {
//                Statement st = con.conexionBD().createStatement();
//                String sql = "select username,phone from public.profiles where id="+arrayFrom.get(i)+";";
//                ResultSet rs = st.executeQuery(sql);
//                while (rs.next()){
//                    username = rs.getString("username");
//                    phone = rs.getString("phone");
//
//                }
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//
//            LinearLayout linearTask = new LinearLayout(Configurate.this);
//            linearTask.setOrientation(LinearLayout.HORIZONTAL);
//
//            TextView textViewName = new TextView(this);
//            textViewName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
//            textViewName.setTextColor(Color.BLACK);
//            textViewName.setText("От: "+username + " Телефон: "+phone + " Отношение: "+ relate);
//
//            Button btnOk = new Button(this);
//            int finalI = i;
//            btnOk.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        Statement st = con.conexionBD().createStatement();
//                        String sql;
//                        sql = "insert into public.relationships(id,id_from,id_to,relation) values ("
//                                +maxId+","+ arrayFrom.get(finalI) +","+ id_from +","+arrayRelate.get(finalI)+ ");";
//                        st.executeUpdate(sql);
//                        String sql1 = "delete from public.requests where id in("+arrayRequestsId.get(finalI)+");";
//                        st.executeUpdate(sql1);
//
//                    } catch (SQLException throwables) {
//                        throwables.printStackTrace();
//                    }
//
//                }
//            });
//            btnOk.setText("YES");
//
//            Button btnNo = new Button(this);
//            btnNo.setText("NO");
//            btnNo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        Statement st = con.conexionBD().createStatement();
//                        String sql = "delete from public.requests where id in("+arrayRequestsId.get(finalI)+");";
//                        st.executeUpdate(sql);
//                        requests_layout.removeView((View)btnNo.getParent());
//                        return;
//                    } catch (SQLException throwables) {
//                        throwables.printStackTrace();
//                    }
//
//                }
//            });
//
//            linearTask.addView(textViewName);
//            linearTask.addView(btnOk);
//            linearTask.addView(btnNo);
//            requests_layout.addView(linearTask);
//
//        }
        dialog.show();

    }

    public void signOutUser(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent changeOnNowIntent = new Intent(Configurate.this, MainActivity.class);
        startActivity(changeOnNowIntent);
    }

    public void reload_activity(View view) {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void reqTask(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Отправленые задачи");

        LayoutInflater inflater = LayoutInflater.from(this);

        View my_requests_window = inflater.inflate(R.layout.my_requests,null);
        dialog.setView(my_requests_window);

        DatabaseReference ref = mDataBase.getReference("TASK");




        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            LinearLayout requests_layout = my_requests_window.findViewById(R.id.linear_requests);
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnap: snapshot.getChildren()){
                    LinearLayout linearTask = new LinearLayout(Configurate.this);
                    linearTask.setOrientation(LinearLayout.HORIZONTAL);
                    String from = (String) childSnap.child("id_from").getValue();
                    if (from.equals(id_from)){

                        TextView textViewName = new TextView(Configurate.this);
                        textViewName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
                        textViewName.setTextColor(Color.BLACK);
                        textViewName.setText("Задача: "+(String) childSnap.child("taskname").getValue()+" Исполнитель:"+(String) childSnap.child("uname").getValue() + " Статус:"+(String) childSnap.child("status").getValue());
                        linearTask.addView(textViewName);
                        requests_layout.addView(linearTask);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        dialog.show();

    }
}