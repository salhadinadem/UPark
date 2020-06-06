package edu.tacoma.uw.guilbb.courseswebservicesapp.model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.tacoma.uw.guilbb.courseswebservicesapp.LoginFragment;
import edu.tacoma.uw.guilbb.courseswebservicesapp.ParkingAddFragment;
import edu.tacoma.uw.guilbb.courseswebservicesapp.ParkingDetailActivity;
import edu.tacoma.uw.guilbb.courseswebservicesapp.ParkingListActivity;
import edu.tacoma.uw.guilbb.courseswebservicesapp.R;

public class SignInActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener, LoginFragment.RegisterFragmentListener {

    private SharedPreferences mSharedPreferences;
    private JSONObject mMemberJSON;
    private static final String TAG = "DisplayMessageActivity";
    public static final String LOGIN = "LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //Toast.makeText(this, "You have signed in", Toast.LENGTH_SHORT).show();
        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        if (!mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.sign_in_fragment_id, new LoginFragment())
                    .commit();

        } else {
            Intent intent = new Intent(this, ParkingListActivity.class);
            startActivity(intent);
            finish();
        }

        //final EditText emailEditText = this.findViewById(R.id.email_address_id);
        //final EditText passwordEditText = this.findViewById(R.id.password_id);

        /*Button signInButton = this.findViewById(R.id.btn_sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "adding member...");
                String password = passwordEditText.getText().toString();
                String email = emailEditText.getText().toString();

                Log.i(TAG, "email is:");
                Log.i(TAG, email);
                loginMember(email, password);
                Log.i(TAG, "memberAdded");
                //refresh does not have working content yet
            }
        });*/

        /*View.OnClickListener signInButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "adding member...");
                String password = passwordEditText.getText().toString();
                String email = emailEditText.getText().toString();

                Log.i(TAG, "email is:");
                Log.i(TAG, email);
                loginMember(email, password);
                Log.i(TAG, "memberAdded");
                //refresh does not have working content yet
            }
        };
        signInButton.setOnClickListener (signInButtonOnClickListener);*/




    }

    @Override
    public void login(String email, String pwd) {
        //Toast.makeText(this, "You have signed in", Toast.LENGTH_SHORT).show();
        mSharedPreferences
                .edit()
                .putBoolean(getString(R.string.LOGGEDIN), true)
                .commit();
        Intent i = new Intent(this, ParkingListActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void register() {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
        finish();
    }

    /*private class LoginMemberAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);
                    OutputStreamWriter wr =
                            new OutputStreamWriter(urlConnection.getOutputStream());

                    // For Debugging
                    Log.i(LOGIN, mMemberJSON.toString());
                    wr.write(mMemberJSON.toString());
                    wr.flush();
                    wr.close();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to add the new course, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.startsWith("Unable to add the new course")) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("success")) {
                    Toast.makeText(getApplicationContext(), "Course Added successfully"
                            , Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Course couldn't be added: "
                                    + jsonObject.getString("error")
                            , Toast.LENGTH_LONG).show();
                    Log.e(LOGIN, jsonObject.getString("error"));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Parsing error on Adding course"
                                + e.getMessage()
                        , Toast.LENGTH_LONG).show();
                Log.e(LOGIN, e.getMessage());
            }
        }
    }


    public void loginMember(String email, String password){
        StringBuilder url = new StringBuilder(getString(R.string.login));
        //Log.i(TAG, "entered registerMember method");
        //Construct a JSONObject to build a formatted message to send
        mMemberJSON = new JSONObject();

        try{

            Log.i(TAG, "entered registerMember method");
            mMemberJSON.put(Member.EMAIL, email);
            mMemberJSON.put(Member.PASSWORD, password);
            new LoginMemberAsyncTask().execute(url.toString());

        }catch (JSONException e){
            Toast.makeText(this, "Error with JSON creation on adding a course: "
                            + e.getMessage()
                    ,Toast.LENGTH_SHORT).show();
        }
    }*/

}

