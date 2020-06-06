package edu.tacoma.uw.guilbb.courseswebservicesapp;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.tacoma.uw.guilbb.courseswebservicesapp.data.ParkingDB;
import edu.tacoma.uw.guilbb.courseswebservicesapp.model.Course;
import edu.tacoma.uw.guilbb.courseswebservicesapp.model.SignInActivity;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ParkingDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 *
 * This activity displays a list of parking lots held in the database, as well as a map with markers
 * representing each lot. Users can view details of the lot by selecting them from the list or by
 * double tapping a marker on the map.
 */
public class ParkingListActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ListView listView;
    private List<Course> mCourseList;
    private List<Course> mCourseListForGeo;
    private RecyclerView mRecyclerView;
    private SimpleItemRecyclerViewAdapter adapter;
    private ParkingDB mCourseDB;

    private FusedLocationProviderClient mFusedLocationClient;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mMapView;

    private GoogleMap mGoogleMap;
    private LatLngBounds mMapBoundary;

    private String TAG = "ParkingListActivity";

    /**
     * Creates the Parking List Activity from the saved instance state
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.u_park_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        mRecyclerView = findViewById(R.id.item_list);
        assert mRecyclerView != null;
        setupRecyclerView((RecyclerView) mRecyclerView);

        //MAP Stuff!!

        ///View view = inflater.inflate(R.layout.activity_item_list, container, false);
        mMapView = (MapView) findViewById(R.id.user_list_map);

        initializeGoogleMap(savedInstanceState);
        final LatLng myLocal = getLastKnownLocation();
        boolean locationUnknown = false;
        if(myLocal.longitude == 404 || myLocal.latitude == 404){
            locationUnknown = true;
        }

        final boolean locationUnkownFinal = locationUnknown;
        final Context context = this;

        FloatingActionButton requestLot = (FloatingActionButton) findViewById(R.id.request_lot);

        // This block of commented code uses GeoCoder to convert from the LatLng to an actual human
        //  readable address. Unfortunately we discovered that Geocoder is a paid service, so we
        //  disabled it in this version.
        requestLot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Add the buttons
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });

                if(locationUnkownFinal){
                    builder.setMessage("Your lot request was not set because we could not determine your location")
                            .setTitle("We could not locate you!");
                }else{

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    String shareBody = "Please consider a parking lot at latitude: "+ myLocal.latitude+
                            " longitude: "+ myLocal.longitude;
                    String shareSub = "Parking lot request at latitude: "+ myLocal.latitude+
                            " longitude: "+ myLocal.longitude;
                    String shareEmail = "UParkDevelopers@gmail.com";
                    intent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
                    intent.putExtra(Intent.EXTRA_TEXT, shareSub);
                    intent.putExtra(Intent.EXTRA_EMAIL,shareEmail );
                    startActivity(Intent.createChooser(intent, "ShareUsing"));

                    builder.setMessage("Your request to add a new parking lot at your current location-- latitude: "
                            + myLocal.latitude+ " and longitude: " + myLocal.longitude+
                            " will be reviewed by our team!\n")
                            .setTitle(R.string.dialog_title);

                }
                // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    /**
    * Gets the current location of the user for requesting new parking lots
    */
    //returns 404 404 if error
    private LatLng getLastKnownLocation() {
        Log.e(TAG, "getLastKnownLocation: called");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return new LatLng(404,404);
        }

        final locationObject yourLocation =  new locationObject();
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    Log.e(TAG, "onComplete: latitude: " + location.getLatitude() );
                    Log.e(TAG, "onComplete: longitude: " + location.getLongitude() );
                    yourLocation.setLatitude(location.getLatitude());
                    yourLocation.setLongitude(location.getLongitude());

                }
            }
        });

        return yourLocation.getLatLng();
    }

     /**
     * Handles setting up the activity when it has been resumed
     */
    @Override
    protected void onResume(){
        /*super.onResume();
        if(mCourseList == null){
            new CoursesTask().execute(getString(R.string.get_courses));
        }*/
        super.onResume();
        mMapView.onResume();
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (mCourseList == null) {
                //new CoursesTask().execute(getString(R.string.get_courses));
                new CoursesTask().execute(getString(R.string.get_courses));
            }
        }
        else {
            Toast.makeText(this,
                    "No network connection available. Displaying locally stored data",
                    Toast.LENGTH_SHORT).show();

            if (mCourseDB == null) {
                mCourseDB = new ParkingDB(this);
            }
            if (mCourseList == null) {
                mCourseList = mCourseDB.getCourses();
                setupRecyclerView(mRecyclerView);


            }

        }
    }


    /**
    * Helper class to handle lat lng locations
    */ 
    private class locationObject{
        double longitude = 404;
        double latitude= 404;

        public void setLongitude(double theLong){
            longitude = theLong;
        }

        public void setLatitude(double theLat){
            latitude = theLat;
        }

        public LatLng getLatLng(){
            return new LatLng(latitude, longitude);
        }

    }

    /**
    * Starts the activity for adding a course.
    */
    private void launchCourseAddFragment(){
        ParkingAddFragment courseAddFragment = new ParkingAddFragment();
        if(mTwoPane){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, courseAddFragment).commit();
        }else{
            Intent intent =  new Intent(this, ParkingDetailActivity.class);
            intent.putExtra(ParkingDetailActivity.ADD_COURSE, true);
            startActivity(intent);
        }
    }

    /**
     * Create a recycler view with the list of parking lots.
     * @param recyclerView
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        if(mCourseList != null){
//            adapter = new ArrayAdapter<>(this, R.layout.activity_item_list, mCourseList);
//
//            listView.setAdapter(adapter);
            adapter = new SimpleItemRecyclerViewAdapter(this, mCourseList, mTwoPane);
            mRecyclerView.setAdapter(adapter);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    /**
     * Handles logic when a user selects an option bar item. This could be the search icon or the
     * Sign Out button
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SharedPreferences sharedPreferences =
                    getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), false)
                    .commit();

            Intent i = new Intent(this, SignInActivity.class);
            startActivity(i);
            finish();
        }


        if (item.getItemId() == R.id.action_search) {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setQueryHint("Type here to Search");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //mCourseList
                    if (newText != null) {
                        adapter.getFilter().filter(newText);
                    }
                    return true;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }


    private class CoursesTask extends AsyncTask<String, Void, String> {

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

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to download the list of courses, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;

        }

        /**
         * Displays a Toast based on the results of the POST request. If the request was successful
         * this method updates the local database with the new parking lots
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            if (s.startsWith("Unable to")) {
                Toast.makeText(getApplicationContext(), "Unable to download" + s, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("success")) {
                    mCourseList = Course.parseCourseJson(
                            jsonObject.getString("names"));
                    mCourseListForGeo = Course.parseCourseJson(
                            jsonObject.getString("names"));

                    setCameraView();
                    if (mCourseDB == null) {
                        mCourseDB = new ParkingDB(getApplicationContext());
                    }
                    // Delete old data so that you can refresh the local
                    // database with the network data.
                    mCourseDB.deleteCourses();

                    // Also, add to the local database
                    for (int i=0; i<mCourseList.size(); i++) {
                        Course course = mCourseList.get(i);
                        mCourseDB.insertCourse(course.getmCourseId(),
                                course.getmCourseShortDesc(),
                                course.getmCourseLongDesc(),
                                course.getmCoursePrereqs(),
                                course.getmLat(),
                                course.getmLong());
                    }

                    if (!mCourseList.isEmpty()) {
                        setupRecyclerView((RecyclerView) mRecyclerView);
                    }
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }


    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> implements Filterable {

        private final ParkingListActivity mParentActivity;
        private final List<Course> mValues;
        private final List<Course> mValuesAll;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Course item = (Course) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putSerializable(ParkingDetailFragment.ARG_ITEM_ID, item);
                    ParkingDetailFragment fragment = new ParkingDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ParkingDetailActivity.class);
                    intent.putExtra(ParkingDetailFragment.ARG_ITEM_ID, item);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(ParkingListActivity parent,
                                      List<Course> items,
                                      boolean twoPane) {
            mValues = items;
            mValuesAll = new ArrayList<>(mValues);
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        /**
         * Populates the list view based on the data passed from the holder. Also formats icon colors
         * based on parking lot availability
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).getmCourseId());
            holder.mContentView.setText(mValues.get(position).getmCourseShortDesc());
            if (mValues.get(position).getmCourseShortDesc().equals("Yes")){
                //Available, tint icons green
                holder.mAccessibleView.setColorFilter(Color.parseColor("#4CAF50"));
            } else {
                //tint the icons red
                holder.mAccessibleView.setColorFilter(Color.parseColor("#FFE91E63"));
            }

            if (mValues.get(position).getmCoursePrereqs().equals("Yes")){
                //Available, tint icons green
                holder.mParkingView.setColorFilter(Color.parseColor("#4CAF50"));
            } else {
                //tint the icons red
                holder.mParkingView.setColorFilter(Color.parseColor("#FFE91E63"));
            }
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        @Override
        public Filter getFilter() {

            return myFilter;
        }

        Filter myFilter = new Filter() {

            //Automatic on background thread

            /**
             * Filters listView based on the charsequence passed in
             * @param charSequence
             * @return
             */
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                List<Course> filteredList = new ArrayList<>();

                if (charSequence == null || charSequence.length() == 0) {
                    filteredList.addAll(mValuesAll);
                } else {
                    for (Course course: mValuesAll) {
                        if (course.getmCourseId().toString().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filteredList.add(course);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            //Automatic on UI thread
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mValues.clear();
                mValues.addAll((Collection<? extends Course>) filterResults.values);
                notifyDataSetChanged();
            }
        };


        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final ImageView mParkingView;
            final ImageView mAccessibleView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
                mParkingView = (ImageView) view.findViewById(R.id.parking_icon_list);
                mAccessibleView = (ImageView) view.findViewById(R.id.accessible_icon_list);
            }
        }
    }

//------------------------------------------------------------------------------------------------

    /**
     * Sets the initial position of the camera on the Google Map. Focused around the first parking
     * lot in the mCourseListForGeo.
     */
    private void setCameraView(){

        LatLng firstLotPosition = new LatLng(Double.parseDouble(mCourseListForGeo.get(0).getmLat()),
                Double.parseDouble(mCourseListForGeo.get(0).getmLong()));

        double bottomBoundary = firstLotPosition.latitude - 0.005 ;
        double leftBoundary = firstLotPosition.longitude - 0.002;
        double topBoundary = firstLotPosition.latitude + 0.005;
        double rightBoundary = firstLotPosition.longitude + 0.002;


        mMapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary));

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
        setMarkers();
    }

    /**
     * Places a map marker on each Parking lot
     */
    private void setMarkers(){
        for(Course lot: mCourseListForGeo){
            //public final Course currentLot = lot;
            LatLng lotPosition = new LatLng(Double.parseDouble(lot.getmLat()), Double.parseDouble(lot.getmLong()));
            Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(lotPosition).title(lot.getmCourseId()));
        }

        final Context context = this;

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            /**
             * Handles logic when a marker on the map is clicked. Begins the Parking Detail Activity
             * of the selected parking lot if one was found.
             * @param marker
             */
            @Override
            public void onInfoWindowClick(Marker marker) {

                Course clickedLot = mCourseListForGeo.get(0);
                boolean foundLot= false;

                for(Course lot: mCourseListForGeo){
                    if(marker.getTitle().contentEquals(lot.getmCourseId())){
                        clickedLot = lot;
                        foundLot = true;
                    }
                }

                if(foundLot){
                    foundLot = false;
                    if(mTwoPane){
                        Bundle arguments = new Bundle();
                        arguments.putSerializable(ParkingDetailFragment.ARG_ITEM_ID, clickedLot);
                        ParkingDetailFragment fragment = new ParkingDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {

                        Intent intent = new Intent(context, ParkingDetailActivity.class);
                        intent.putExtra(ParkingDetailFragment.ARG_ITEM_ID, clickedLot);

                        context.startActivity(intent);
                    }

                }



            }
        });
    }


    /**
     * Set up Google Map Window with API key
     */
    private void initializeGoogleMap(Bundle savedInstanceState){
        Bundle mapViewBundle = null;
        if(savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        mMapView.onStop();
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
        mGoogleMap = googleMap;
    }

    @Override
    public void onPause(){
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
