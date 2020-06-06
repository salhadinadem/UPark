package edu.tacoma.uw.guilbb.courseswebservicesapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.tacoma.uw.guilbb.courseswebservicesapp.model.Course;
import edu.tacoma.uw.guilbb.courseswebservicesapp.model.Member;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ParkingListActivity}.
 */
public class ParkingDetailActivity extends AppCompatActivity
        implements  ParkingAddFragment.AddListener {

    public static final String ADD_COURSE = "ADD_COURSE";
    public static final String UPDATE_COURSE = "UPDATE_COURSE";
    public static final String REGISTER_MEMBER = "REGISTER_MEMBER";
    private JSONObject mCourseJSON;
    private JSONObject mMemberJSON;
    private static final String TAG = "DisplayMessageActivity";
    ParkingDetailFragment myFragment;
    private FragmentRefreshListener fragmentRefreshListener;


    private class UpdateCourseAsyncTask extends AsyncTask<String, Void, String> {
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
                    Log.i(UPDATE_COURSE, mCourseJSON.toString());
                    wr.write(mCourseJSON.toString());
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
            if (s.startsWith("Unable to update the course")) {
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
                    Log.e(UPDATE_COURSE, jsonObject.getString("error"));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Parsing error on Adding course"
                                + e.getMessage()
                        , Toast.LENGTH_LONG).show();
                Log.e(UPDATE_COURSE, e.getMessage());
            }
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            if(getIntent().getSerializableExtra(ParkingDetailFragment.ARG_ITEM_ID) != null) {
                arguments.putSerializable(ParkingDetailFragment.ARG_ITEM_ID,
                        getIntent().getSerializableExtra(ParkingDetailFragment.ARG_ITEM_ID));
                ParkingDetailFragment fragment = new ParkingDetailFragment();
                myFragment = fragment;
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.item_detail_container, fragment)
                        .commit();
            }else if(getIntent().getBooleanExtra(ParkingDetailActivity.ADD_COURSE, false)){
                ParkingAddFragment fragment = new ParkingAddFragment();
                //myFragment = fragment;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.item_detail_container, fragment)
                        .commit();
            }
        }

        Log.i(TAG, "running");
        //View v = inflater.inflate(R.layout.activity_item_detail, container, false);

        //View v = inflater.inflate(R.layout.activity_item_detail, container, false);
        //public static final String ARG_ITEM_ID = "item_id";
        //mItem = (Course) getArguments().getSerializable(ARG_ITEM_ID);
        final Course thisCourse = (Course) getIntent().getSerializableExtra(ParkingDetailFragment.ARG_ITEM_ID);
        Activity v = this;


        Button yesButton = this.findViewById(R.id.add_course_prereqs_yes);
        View.OnClickListener yesButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parkingAvailability = "Yes";

                Log.i(TAG, "yes was entered");
                updateCourse(thisCourse, parkingAvailability);
                getFragmentRefreshListener().onRefresh();
                //refresh does not have working content yet
            }
        };
        yesButton.setOnClickListener (yesButtonOnClickListener);

        Button noButton = this.findViewById(R.id.add_course_prereqs_no);
        View.OnClickListener noButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parkingAvailability = "No";
                updateCourse(thisCourse, parkingAvailability);
                Log.i(TAG, "no was entered");
            }
        };
        noButton.setOnClickListener (noButtonOnClickListener);

        Button yesButtonHandicap = this.findViewById(R.id.add_course_shortdesc_yes);
        View.OnClickListener yesButtonHandicapOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parkingAvailability = "Yes";

                Log.i(TAG, "yes was entered");
                updateHandicapParking(thisCourse, parkingAvailability);
                getFragmentRefreshListener().onRefresh();
                //refresh does not have working content yet
            }
        };
        yesButtonHandicap.setOnClickListener (yesButtonHandicapOnClickListener);

        Button noButtonHandicap = this.findViewById(R.id.add_course_shortdesc_no);
        View.OnClickListener noButtonHandicapOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parkingAvailability = "No";
                updateHandicapParking(thisCourse, parkingAvailability);
                Log.i(TAG, "no was entered");
            }
        };
        noButtonHandicap.setOnClickListener (noButtonHandicapOnClickListener);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, ParkingListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class AddCourseAsyncTask extends AsyncTask<String, Void, String> {
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
                    Log.i(ADD_COURSE, mCourseJSON.toString());
                    wr.write(mCourseJSON.toString());
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
                    Log.e(ADD_COURSE, jsonObject.getString("error"));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Parsing error on Adding course"
                                + e.getMessage()
                        , Toast.LENGTH_LONG).show();
                Log.e(ADD_COURSE, e.getMessage());
            }
        }
    }





    /*private class RegisterMemberAsyncTask extends AsyncTask<String, Void, String> {
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
                    Log.e(ADD_COURSE, jsonObject.getString("error"));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Parsing error on Adding course"
                                + e.getMessage()
                        , Toast.LENGTH_LONG).show();
                Log.e(ADD_COURSE, e.getMessage());
            }
        }
    }*/


    @Override
    public void addCourse(Course course) {
        StringBuilder url = new StringBuilder(getString(R.string.add_course));

        //Construct a JSONObject to build a formatted message to send
        mCourseJSON = new JSONObject();
        try{
            mCourseJSON.put(Course.ID, course.getmCourseId());
            mCourseJSON.put(Course.SHORT_DESC, course.getmCourseShortDesc());
            mCourseJSON.put(Course.LONG_DESC, course.getmCourseLongDesc());
            mCourseJSON.put(Course.PRE_REQS, course.getmCoursePrereqs());
            new AddCourseAsyncTask().execute(url.toString());

        }catch (JSONException e){
            Toast.makeText(this, "Error with JSON creation on adding a course: "
            + e.getMessage()
            ,Toast.LENGTH_SHORT).show();
        }
    }


    public void updateCourse(Course course, String newAvailability){
        StringBuilder url = new StringBuilder(getString(R.string.update_course));

        //Construct a JSONObject to build a formatted message to send
        mCourseJSON = new JSONObject();
        try{
            mCourseJSON.put(Course.ID, course.getmCourseId());
            mCourseJSON.put(Course.SHORT_DESC, course.getmCourseShortDesc());
            mCourseJSON.put(Course.LONG_DESC, course.getmCourseLongDesc());
            mCourseJSON.put(Course.PRE_REQS, newAvailability);
            new UpdateCourseAsyncTask().execute(url.toString());

        }catch (JSONException e){
            Toast.makeText(this, "Error with JSON creation on adding a course: "
                            + e.getMessage()
                    ,Toast.LENGTH_SHORT).show();
        }
    }

    /*public void registerMember(Member member){
        StringBuilder url = new StringBuilder(getString(R.string.register_course));

        //Construct a JSONObject to build a formatted message to send
        mCourseJSON = new JSONObject();
        try{
            mCourseJSON.put(Member.NAME, member.getmName());
            mCourseJSON.put(Member.EMAIL, member.getmEmail());
            mCourseJSON.put(Member.PASSWORD, member.getmPassword());
            mCourseJSON.put(Member.USERNAME, member.getmUsername());
            new UpdateCourseAsyncTask().execute(url.toString());

        }catch (JSONException e){
            Toast.makeText(this, "Error with JSON creation on adding a course: "
                            + e.getMessage()
                    ,Toast.LENGTH_SHORT).show();
        }
    }*/

    public void updateHandicapParking(Course course, String newHandicapAvailability){
        StringBuilder url = new StringBuilder(getString(R.string.update_course));

        //Construct a JSONObject to build a formatted message to send
        mCourseJSON = new JSONObject();
        try{
            mCourseJSON.put(Course.ID, course.getmCourseId());
            mCourseJSON.put(Course.SHORT_DESC, newHandicapAvailability);
            mCourseJSON.put(Course.LONG_DESC, course.getmCourseLongDesc());
            mCourseJSON.put(Course.PRE_REQS, course.getmCoursePrereqs());
            new UpdateCourseAsyncTask().execute(url.toString());

        }catch (JSONException e){
            Toast.makeText(this, "Error with JSON creation on adding a course: "
                            + e.getMessage()
                    ,Toast.LENGTH_SHORT).show();
        }

    }


    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    public interface FragmentRefreshListener{
        void onRefresh();
    }



}
