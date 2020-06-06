package edu.tacoma.uw.guilbb.courseswebservicesapp;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.concurrent.ExecutionException;

import edu.tacoma.uw.guilbb.courseswebservicesapp.model.Member;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText emailText;
    private EditText pwdText;

    JSONObject myJsonObject;

    private JSONObject mMemberJSON;
    private static final String TAG = "DisplayMessageActivity";
    public static final String LOGIN = "LOGIN";
    private boolean isLoggedIn = false;

    private String mParam1;
    private String mParam2;

    private LoginFragmentListener mLoginFragmentListener;

    private RegisterFragmentListener mRegisterFragmentListener;

    public interface LoginFragmentListener {
        public void login(String email, String pwd);
    }

    public interface RegisterFragmentListener {
        public void register();
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle("Sign In");
        mLoginFragmentListener = (LoginFragmentListener) getActivity();
        mRegisterFragmentListener = (RegisterFragmentListener) getActivity();
        emailText = v.findViewById(R.id.email_address_id);
        pwdText = v.findViewById(R.id.password_id);
        final EditText emailTextf = emailText;
        final EditText pwdTextf = pwdText;

        Button loginButton = v.findViewById(R.id.btn_sign_in);
        Button registerButton = v.findViewById(R.id.btn_create_acc);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegisterFragmentListener.register();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTextf.getText().toString();
                String pwd = pwdTextf.getText().toString();
                if (TextUtils.isEmpty(email) || !email.contains("@")) {
                    Toast.makeText(v.getContext(), "Enter valid email address", Toast.LENGTH_SHORT).show();
                    emailTextf.requestFocus();
                }else if (TextUtils.isEmpty(pwd) || pwd.length() < 6) {
                    Toast.makeText(v.getContext(), "Enter a valid password (at least 6 characters)", Toast.LENGTH_SHORT).show();
                    pwdTextf.requestFocus();
                } else {
                    loginMember(email, pwd);
                    //if(isLoggedIn){
                        //mLoginFragmentListener.login(email, pwd);
                       // isLoggedIn = false;
                    //}else{
                     //   Toast.makeText(v.getContext(), "Email or password not recognized!", Toast.LENGTH_SHORT).show();
                        //e.printStackTrace();
                    //}

                }
            }
        });
        return v;
    }

    private class LoginMemberAsyncTask extends AsyncTask<String, Void, String> {
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
                    Log.i(TAG, "Printing Member JSON POST");
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
                    Log.i(TAG, "BackendResponse: ");
                    Log.i(TAG, response);

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
                Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                myJsonObject = jsonObject;
                if (jsonObject.getBoolean("success")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Course Added successfully"
                            , Toast.LENGTH_SHORT).show();
                    isLoggedIn = true;
                    mLoginFragmentListener.login(emailText.getText().toString(), pwdText.getText().toString());
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Course couldn't be added: "
                                    + jsonObject.getString("error")
                            , Toast.LENGTH_LONG).show();
                    Log.e(LOGIN, jsonObject.getString("error"));
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), "JSON Parsing error on Adding course"
                                + e.getMessage()
                        , Toast.LENGTH_LONG).show();
                Log.e(LOGIN, e.getMessage());
            }
        }
    }


    /*public void loginMember(String email, String password){
        StringBuilder url = new StringBuilder(getString(R.string.login));
        //Log.i(TAG, "entered registerMember method");
        //Construct a JSONObject to build a formatted message to send
        mMemberJSON = new JSONObject();
        String response = "error";
        try{

            Log.i(TAG, "entered loginMember method");
            mMemberJSON.put(Member.EMAIL, email);
            mMemberJSON.put(Member.PASSWORD, password);
            Log.i(TAG, "Put Email/Pass into MemberJSON");
            Log.i(TAG, "Starting Async Task...");
            new LoginMemberAsyncTask().execute(url.toString());
            Log.i(TAG, "Completed Async Task");
        }catch (JSONException e){
            //Toast.makeText(this, "Error with JSON creation on adding a course: " + e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }*/


    public void loginMember(String email, String password){
        StringBuilder url = new StringBuilder(getString(R.string.login));
        //Log.i(TAG, "entered registerMember method");
        //Construct a JSONObject to build a formatted message to send
        mMemberJSON = new JSONObject();
        String response = "error";
        try{

            Log.i(TAG, "entered loginMember method");
            mMemberJSON.put(Member.EMAIL, email);
            mMemberJSON.put(Member.PASSWORD, password);
            Log.i(TAG, "Put Email/Pass into MemberJSON");
            Log.i(TAG, "Starting Task...");

            String str_result= new LoginMemberAsyncTask().execute(url.toString()).get();
            //while (str_result == null){

            //}
            //loginHelper(url.toString());
            Log.i(TAG, "Completed Task");
        }catch (JSONException e){
            //Toast.makeText(this, "Error with JSON creation on adding a course: " + e.getMessage(),Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void loginHelper(String... urls) {

        String s = doInBackgroundHelper(urls);
        onPostExecuteHelper(s);
    }


    protected String doInBackgroundHelper(String... urls) {
        String response = "";
        HttpURLConnection urlConnection = null;
        Log.i(TAG, "In Helper");
        for (String url : urls) {
            try {
                Log.i(TAG, url);
                URL urlObject = new URL(url);
                urlConnection = (HttpURLConnection) urlObject.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();
                Log.i(TAG, "Creating OutputStreamWriter");
                OutputStreamWriter wr =
                        new OutputStreamWriter(urlConnection.getOutputStream());
                Log.i(TAG, "OutputStreamWriter created");
                // For Debugging
                Log.i(TAG, "Printing Member JSON POST");
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
                Log.i(TAG, "BackendResponse: ");
                Log.i(TAG, response);

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

    protected void onPostExecuteHelper(String s) {
        if (s.startsWith("Unable to add the new course")) {
            Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(s);
            myJsonObject = jsonObject;
            if (jsonObject.getBoolean("success")) {
                Toast.makeText(getActivity().getApplicationContext(), "Course Added successfully"
                        , Toast.LENGTH_SHORT).show();
                isLoggedIn = true;
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(), "Course couldn't be added: "
                                + jsonObject.getString("error")
                        , Toast.LENGTH_LONG).show();
                Log.e(LOGIN, jsonObject.getString("error"));
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity().getApplicationContext(), "JSON Parsing error on Adding course"
                            + e.getMessage()
                    , Toast.LENGTH_LONG).show();
            Log.e(LOGIN, e.getMessage());
        }
    }












}

