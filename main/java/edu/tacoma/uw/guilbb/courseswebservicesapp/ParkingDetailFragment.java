package edu.tacoma.uw.guilbb.courseswebservicesapp;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.tacoma.uw.guilbb.courseswebservicesapp.model.Course;


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ParkingListActivity}
 * in two-pane mode (on tablets) or a {@link ParkingDetailActivity}
 * on handsets.
 */
public class ParkingDetailFragment extends Fragment {

    private static final String TAG = "DisplayMessageActivity";
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Course mItem;
    private Handler mHandler;


    Button yesButton ;
    View.OnClickListener yesButtonOnClickListener;
    View myRootView;
    TextView longdesc;
    TextView shortdesc;
    TextView id;
    TextView prereqs;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ParkingDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = (Course) getArguments().getSerializable(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
                    activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getmCourseId());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        // Show the course content as text in a TextView.
        if (mItem != null) {
            longdesc = ((TextView) rootView.findViewById(R.id.item_detail_long_desc));
            longdesc.setText(
                    mItem.getmCourseLongDesc());

            shortdesc =((TextView) rootView.findViewById(R.id.item_detail_short_desc));
            shortdesc.setText(
                    mItem.getmCourseShortDesc());

            id = ((TextView) rootView.findViewById(R.id.item_detail_id));
            id.setText(
                    mItem.getmCourseId());

            prereqs = ((TextView) rootView.findViewById(R.id.item_detail_prereqs));
            prereqs.setText(
                    mItem.getmCoursePrereqs());

        }
        final View theRootView = rootView;
        myRootView = rootView;

        ((ParkingDetailActivity)getActivity()).setFragmentRefreshListener(new ParkingDetailActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler = new Handler();
                mHandler.post(mUpdate);

            }
        });
        rootView = theRootView;


        return rootView;
    }


    private Runnable mUpdate = new Runnable() {
        public void run() {

            if (mItem != null) {
                longdesc.setText( mItem.getmCourseLongDesc());

                shortdesc.setText(
                        mItem.getmCourseShortDesc());

                id.setText(
                        mItem.getmCourseId());

                prereqs.setText(
                        mItem.getmCoursePrereqs());

            }
            Toast.makeText(getActivity().getApplicationContext(), "REFRESH!!" + mItem.getmCoursePrereqs(), Toast.LENGTH_SHORT).show();



        }
    };


}