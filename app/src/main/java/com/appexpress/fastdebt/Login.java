package com.appexpress.fastdebt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Login extends AppCompatActivity {

    private EditText txtuid;
    private SessionManager session;
    private static final String TAG = Login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtuid = findViewById(R.id.uid);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void Check(View v) {
        String uid = txtuid.getText().toString();

        // Check for empty data in the form
        if (!uid.isEmpty()) {

            // save user data to shared preference
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Login.this);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("uid", uid);
            edit.commit();
            //request reset password
            new Check(this).execute(uid);
        } else {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),
                    "Please enter your NRC number", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public class Check extends AsyncTask<String, Void, String> {

        private Context context;



        // Progress Dialog
        private ProgressDialog pDialog;

        public Check(Context context) {
            this.context = context;
        }

        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(this.context);
            pDialog.setMessage("Logging in..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {
            String uid = arg0[0];

            String link;
            String data;
            BufferedReader bufferedReader;
            String result;

            try {
                data = "?uid=" + URLEncoder.encode(uid, "UTF-8");

                link = "http://fastdebt.pe.hu/form/check.php" + data;
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                result = bufferedReader.readLine();
                return result;
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            String jsonStr = result;
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String query_result = jsonObj.getString("query_result");
                    if (query_result.equals("SUCCESS")) {

                        //Set login session to true
                        session.setLogin(true);

                        //Take user to main Activity
                        Intent i = new Intent(context, MainActivity.class);
                        context.startActivity(i);

                        //Toast
                        Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show();

                    } else if (query_result.equals("FAILURE")) {
                        Toast.makeText(context, "Login Failed, Contact Support", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Couldn't connect to remote database.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "No Internet Connection.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Could not connect to server.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
