package com.example.daniel.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;


public class MyActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mTitle = getTitle();
        final EditText userName = (EditText) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.password);
        // Set up the drawer.
        final Context context = getApplicationContext();
        final Button login_button = (Button) findViewById(R.id.login);
        login_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()){
                    new LoginPostTask().execute(getString(R.string.url), userName.getText().toString(), password.getText().toString());
                }
                else{
                    System.out.println("No network connection available");
                }
                // send user and password to verify page

            }

        });
        final Button signup_button = (Button) findViewById(R.id.signup);
        signup_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent signup = new Intent(context, signUp.class);
                startActivity(signup);
            }

        });

    }
    private class LoginPostTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... info) {
            Integer result = 0;
            try {
                try {
                    result = postLogin(info[0], info[1], info[2]);
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

        private int postLogin(String login_url, String user, String pass) throws IOException, JSONException {
            InputStream is = null;
            int len = 500;
            final REST services = new REST();
            StringBuilder sb = new StringBuilder();
            HttpURLConnection conn = services.buildConnection(login_url, "POST");


            // create json
            JSONObject jsonParam = new JSONObject();
            jsonParam.put(getString(R.string.json_user), user);
            jsonParam.put(getString(R.string.json_pass), pass);
            return services.POST_JSON(jsonParam, conn);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public JSONObject gen_loginJSON(String pass, String user){
        JSONObject json = new JSONObject();
        try{
            json.put(getString(R.string.json_pass), pass);
            json.put(getString(R.string.json_user), user);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return json;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MyActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
