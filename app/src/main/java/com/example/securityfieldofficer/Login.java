package com.example.securityfieldofficer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.securityfieldofficer.LoadingAnim.LoadingWithAnim;
import com.example.securityfieldofficer.Models.Login_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {

    EditText edt_username,edt_password;
    Button login_button;

    String header;
    String customer_username,customer_password;
    SharedPreferences mSP;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor editor;
    LoadingWithAnim loadingDialog;

    ArrayList<Login_pojo> model;

    String username,password,name,image_url;
    String login_url;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        header = getString(R.string.header);
        mSP = getSharedPreferences("login", Context.MODE_PRIVATE);

        loadingDialog = new LoadingWithAnim(Login.this,2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.light_grey, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.light_grey));
        }

        login_button = findViewById(R.id.login_button);
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_username.getText().toString().length()==0 ) {
                    edt_username.setError("Username can't be empty");
                }else if(edt_password.getText().length()==0){
                    edt_password.setError("Password can't be empty");
                }else{
                    login_url = header+"user_login.php?sfo_id="+edt_username.getText().toString();
                    Log.v("Login",""+login_url);
                    loadingDialog.startLoadingDialog();
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {

                            try
                            {
                                JsonParser o = new JsonParser();
                                result = o.insert(login_url);
                                model = new ArrayList<>();

                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("res");

                                Log.v("Login_DATA",""+result);

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject11 = jsonArray.getJSONObject(i);
                                    Login_pojo p = new Login_pojo();

                                    p.setUsername(jsonObject11.getString("sfo_id"));
                                    p.setPassword(jsonObject11.getString("password"));
                                    p.setName(jsonObject11.getString("name"));
                                    p.setImage_url(jsonObject11.getString("image_url"));
                                    model.add(p);

                                    customer_username = p.getUsername();
                                    customer_password = p.getPassword();

                                    name = p.getName();
                                    image_url = p.getImage_url();

                                    Log.v("username","id: "+username +" pass: "+password);
                                    Log.v("customer_username","id: "+customer_username +" pass: "+customer_password);

                                }
                            }
                            catch ( JSONException e)
                            {
                                e.printStackTrace();
                                //  Toast.makeText(Login.this, "Please check your Internet Connection and Retry", Toast.LENGTH_LONG).show();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //write your UI part here....
                                    if (edt_username.getText().toString().equals(customer_username) && edt_password.getText().toString().equals(customer_password))
                                    {
                                        Toast.makeText(Login.this, " Login Sucessful!!", Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismissDialog();
                                        Intent intent = new Intent(Login.this, Home.class);

                                        startActivity(intent);
                                        finish();

                                        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        editor = mSharedPreferences.edit();
                                        editor.putString("email_id",edt_username.getText().toString());
                                        editor.putString("password",edt_password.getText().toString());
                                        editor.putString("name",""+name);
                                        editor.putString("image_url",""+image_url);
                                        editor.commit();

                                        mSP.edit().putBoolean("logged",true).apply();
                                        mSP.edit().putString("SFO_ID",edt_username.getText().toString()).apply();
                                        mSP.edit().putString("name",name).apply();
                                        mSP.edit().putString("image_url",image_url).apply();

                                        Log.d("sucess",""+customer_password);
                                        Log.d("sucess",""+customer_username);
                                        Log.d("sucess",""+name);

                                    }
                                    else{
                                        loadingDialog.dismissDialog();
                                        Toast.makeText(Login.this, " Email or Password is Incorrect!! ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });



                }


            }
        });

    }

}