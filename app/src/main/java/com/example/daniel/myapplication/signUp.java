package com.example.daniel.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;


public class signUp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        final REST services = new REST();
        final String sign_up_url = "http://104.131.82.38:3000/signup";
        final Button signup_button = (Button) findViewById(R.id.button);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()){
                    String email = ((EditText) findViewById(R.id.email)).getText().toString();
                    String name = ((EditText) findViewById(R.id.name)).getText().toString();
                    String pass = ((EditText) findViewById(R.id.password)).getText().toString();
                    new SignUpTask().execute(sign_up_url, email, pass, name);
                }
                else{
                    System.out.println("No network connection available");
                }

            }
        });
    }

    private class SignUpTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... info) {
            Integer result = 0;
            try {
                try {
                    result = postSignUp(info[0], info[1], info[2], info[3]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(Integer Result){
            super.onPostExecute(Result);
            Context context = getApplicationContext();
            //Toast.makeText(context, Result, Toast.LENGTH_LONG).show();
            // if successful login start test homepage
            if(Result == HttpURLConnection.HTTP_OK){
                Intent homepage = new Intent(context, Home.class);
                homepage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(homepage);
                startActivity(homepage);
            } else Log.w("POST return", Result.toString());

        }

        private int postSignUp(String signup_url, String user, String pass, String name) throws IOException, JSONException {
            InputStream is = null;
            int len = 500;
            final REST services = new REST();
            StringBuilder sb = new StringBuilder();
            HttpURLConnection conn = services.buildConnection(signup_url, "POST");


            // create json
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("email", user);
            jsonParam.put("pass", pass);
            jsonParam.put("name", name);
            return services.POST_JSON(jsonParam, conn);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
