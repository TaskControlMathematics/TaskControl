package com.example.taskcontrol;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Login extends AppCompatActivity{

    private static ConnectPGSQL con = new ConnectPGSQL();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDataBase;
    private String USER_KEY = "USER";
    Button btnLogin, btnRegistration;
    EditText editPhone,editUserName,editPassword;
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.buttonLogin);
        btnRegistration = findViewById(R.id.buttonRegistration);
        root = findViewById(R.id.root_element);

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference(USER_KEY);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out

                }

            }
        };
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot childSnap: snapshot.getChildren()){
                        String id = (String) childSnap.child("id").getValue();
                        if(Uid.equals(id)){
                            Intent changeOnTaskIntent = new Intent(Login.this, Configurate.class);
                            changeOnTaskIntent.putExtra("USERNAME",(String) childSnap.child("username").getValue());
                            changeOnTaskIntent.putExtra("ID",Uid);
                            startActivity(changeOnTaskIntent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        };
    }

    public String getHash(String text){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            md.update(text.getBytes(StandardCharsets.UTF_8));
        }
        byte[] digest = md.digest();

        String hex = String.format("%064x", new BigInteger(1, digest));
        return hex;
    }

    public void changeOnTask(View view) {
        Intent changeOnNowIntent = new Intent(Login.this, Task.class);
        startActivity(changeOnNowIntent);
    }

    public void changeOnNow(View view) {
        Intent changeOnNowIntent = new Intent(Login.this, MainActivity.class);
        startActivity(changeOnNowIntent);
    }

    public void createRegisterAlert(View view) {
        List<String> arrayPhones = new ArrayList<String>();
        List<String> arrayusernames = new ArrayList<String>();
        List<Integer> arrayid = new ArrayList<Integer>();
        try{
            Statement st;
            st = con.conexionBD().createStatement();
            String sql;
            sql = "select * from public.profiles;";
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                Integer id = Integer.parseInt(rs.getString("id"));
                String username = rs.getString("username");
                String phone = rs.getString("phone");
                arrayPhones.add(phone);
                arrayusernames.add(username);
                arrayid.add(id);
            }
        }catch (Exception ex){

            Log.d("CONNECT","FUCK YOU MAN, ERROR is : "+ex.toString());
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Регистариця");
        dialog.setMessage("Введите данные");

        LayoutInflater inflater = LayoutInflater.from(this);

        View register_window = inflater.inflate(R.layout.register_layout,null);
        editPhone = register_window.findViewById(R.id.editEmail);
        editUserName = register_window.findViewById(R.id.editUserName);
        editPassword = register_window.findViewById(R.id.editPassword);
        dialog.setView(register_window);

        dialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

            }
        });
        dialog.setPositiveButton("Зарегестрироваться", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                String email = editPhone.getText().toString();
                String userName = editUserName.getText().toString();
                String password = editPassword.getText().toString();
//                phone = phone.replaceAll("( +)"," ").trim();
//                userName = userName.replaceAll("( +)"," ").trim();
//                password = password.replaceAll("( +)"," ").trim();
//                boolean result = phone.matches("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$");
//                if(TextUtils.isEmpty(editPhone.getText().toString())){
//                    Snackbar.make(root,"edit phone",Snackbar.LENGTH_LONG).show();
//                    return;
//                }
//                if (userName.length() == 0 || password.length() ==0 || phone.length() == 0){
//                    Log.d("LENGTH","No length man. FUCK U!");
//                    return;
//                }
//                if (!result){
////                    Snackbar.make(root_layout_register,"its not phone",Snackbar.LENGTH_SHORT).show();
//                    Log.d("REGEX","No phone man. FUCK U!");
//                    return;
//                }
//                if(TextUtils.isEmpty(editUserName.getText().toString())){
//                    Snackbar.make(root,"edit user name",Snackbar.LENGTH_SHORT).show();
//                    return;
//                }
//                if(TextUtils.isEmpty(editPassword.getText().toString())){
//                    Snackbar.make(root,"edit password",Snackbar.LENGTH_SHORT).show();
//                    return;
//                }
//                if (Arrays.asList(arrayPhones).contains(editPhone.getText().toString())){
//                    Snackbar.make(root,"this phone is busy",Snackbar.LENGTH_SHORT).show();
//                    return;
//                }
//                if (Arrays.asList(arrayusernames).contains(editUserName.getText().toString())){
//                    Snackbar.make(root,"this user name is busy",Snackbar.LENGTH_SHORT).show();
//                    return;
//                }
//                String maxId = String.valueOf(Collections.max(arrayid) + 1);
//                Statement st;
//
//                String hashPassword = getHash(editPassword.getText().toString());
//                try {
//                    st = con.conexionBD().createStatement();
//                    String sql;
//                    sql = "insert into  public.profiles(id,phone,username,password) values ("+ maxId+",\'"+editPhone.getText().toString()
//                +"\',\'"+editUserName.getText().toString()+"\',\'"+hashPassword+"\');";
//                    st.executeUpdate(sql);
//                } catch (SQLException throwables) {
//                    throwables.printStackTrace();
//                }
                mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        Snackbar.make(root,"Регистрация прошла успешно",Snackbar.LENGTH_SHORT).show();
                        String id  = authResult.getUser().getUid();
                        User newUser = new User(id,userName,email);
                        mDataBase.push().setValue(newUser);
                        Intent changeOnTaskIntent = new Intent(Login.this, Configurate.class);
                        changeOnTaskIntent.putExtra("USERNAME",userName);
                        changeOnTaskIntent.putExtra("ID",id);
                        startActivity(changeOnTaskIntent);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root,"Регистрация не удалась.Убедитесь в правильности  email,пароль состоит из 8 и более символов.",Snackbar.LENGTH_LONG).show();

                    }
                });


            }
        });

        dialog.show();
    }


    public void createSignInAlert(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Авторизация");
        dialog.setMessage("Введите данные");

        LayoutInflater inflater = LayoutInflater.from(this);

        View signin_window = inflater.inflate(R.layout.signin_layout,null);

        editPhone = signin_window.findViewById(R.id.editPhone);
        editPassword = signin_window.findViewById(R.id.editPassword);
        dialog.setView(signin_window);


        dialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

            }
        });


        dialog.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String email = editPhone.getText().toString();

                if(TextUtils.isEmpty(editPhone.getText().toString())){
                    Snackbar.make(root,"edit phone",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(editPassword.getText().toString())){
                    Snackbar.make(root,"edit password",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email,editPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String Uid = authResult.getUser().getUid();
                        mDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot childSnap: snapshot.getChildren()){
                                    String id = (String) childSnap.child("id").getValue();
                                    if(Uid.equals(id)){
                                        Intent changeOnTaskIntent = new Intent(Login.this, Configurate.class);
                                        changeOnTaskIntent.putExtra("USERNAME",(String) childSnap.child("username").getValue());
                                        changeOnTaskIntent.putExtra("ID",Uid);
                                        startActivity(changeOnTaskIntent);
                                        break;


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root,"неудача",Snackbar.LENGTH_SHORT).show();
                    }
                });
//                String new_password = "null";
//                String username = "null";
//                String id = "null";
//
//                try {
//                    Statement st = con.conexionBD().createStatement();
//                    String sql;
//                    sql = "select * from public.profiles where phone=\'"+phone+"\';";
//                    ResultSet rs = st.executeQuery(sql);
//                    while(rs.next()){
//                        new_password = rs.getString("password");
//                        username = rs.getString("username");
//                        id = rs.getString("id");
//                        Log.d("new_password",new_password);
//
//                    }
//                } catch (SQLException throwables) {
//                    throwables.printStackTrace();
//                }
//                String old_password = getHash(editPassword.getText().toString());
//                Log.d("old_password",old_password);
//                if (old_password.equals(new_password)){
//                    Intent changeOnTaskIntent = new Intent(Login.this, Configurate.class);
//                    changeOnTaskIntent.putExtra("USERNAME",username);
//                    changeOnTaskIntent.putExtra("ID",id);
//                    startActivity(changeOnTaskIntent);
//                    Log.d("SIGNIN","YES");
//                }else{
//                    Log.d("SIGNIN","NO");
//                }




            }
        });

        dialog.show();
    }


}