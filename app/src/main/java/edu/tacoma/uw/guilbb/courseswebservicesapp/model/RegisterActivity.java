package edu.tacoma.uw.guilbb.courseswebservicesapp.model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import edu.tacoma.uw.guilbb.courseswebservicesapp.R;

/**
 * An activity representing the register page. Allows users to create a new account with a valid
 * email and password. Basic email and password validation is done here.
 */
public class RegisterActivity extends AppCompatActivity {

    public static final String REGISTER_MEMBER = "REGISTER_MEMBER";
    private JSONObject mMemberJSON;
    private static final String TAG = "DisplayMessageActivity";


    /**
     * Creates the Register Activity from the saved instance state
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText emailEditText = this.findViewById(R.id.add_email);
        final EditText passwordEditText = this.findViewById(R.id.add_password);


        Button registerButton = this.findViewById(R.id.btn_register);
        View.OnClickListener registerButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "adding member...");
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (TextUtils.isEmpty(email) || !email.contains("@")) {
                    Toast.makeText(v.getContext(), "Enter valid email address", Toast.LENGTH_SHORT).show();
                    emailEditText.requestFocus();
                }else if (TextUtils.isEmpty(password) || password.length() < 6) {
                    Toast.makeText(v.getContext(), "Enter a valid password (at least 6 characters)", Toast.LENGTH_SHORT).show();
                    passwordEditText.requestFocus();
                } else {
                    registerMember(email, password);

                }
            }
        };
        registerButton.setOnClickListener (registerButtonOnClickListener);

    }

    /**
     * Returns to the sign in page if the back button is clicked
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private class RegisterMemberAsyncTask extends AsyncTask<String, Void, String> {
        /**
         * Sends login information to specified URL in JSON format
         * @param urls
         * @return
         */
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
                    Log.i(REGISTER_MEMBER, mMemberJSON.toString());
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

        /**
         * Displays a Toast based on the results of the POST request, and returns the user to the
         * Sign In activity if they were successfully registered
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            if (s.startsWith("Unable to add the new course")) {
                Toast.makeText(getApplicationContext(), "Unable to Update User", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("success")) {
                    Toast.makeText(getApplicationContext(), "User Added Successfully!"
                            , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "User Failed to Add (Email Already Exists!)"
                            , Toast.LENGTH_LONG).show();
                    Log.e(REGISTER_MEMBER, jsonObject.getString("error"));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Parsing Error While Updating User: "
                                + e.getMessage()
                        , Toast.LENGTH_LONG).show();
                Log.e(REGISTER_MEMBER, e.getMessage());
            }
        }
    }


    /**
     * Creates a JSON Member Object based on the inputted email and password and sends it to
     * the backend to try to create a new user.
     * @param email
     * @param password
     */
    public void registerMember(String email, String password){
        StringBuilder url = new StringBuilder(getString(R.string.register_course));
        mMemberJSON = new JSONObject();

        try{

            Log.i(TAG, "entered registerMember method");
            mMemberJSON.put(Member.EMAIL, email);
            mMemberJSON.put(Member.PASSWORD, password);
            new RegisterActivity.RegisterMemberAsyncTask().execute(url.toString());


        }catch (JSONException e){

            Toast.makeText(this, "JSON Creation Error While Updating Parking Lot: "

                            + e.getMessage()
                    ,Toast.LENGTH_SHORT).show();
        }
    }
}
