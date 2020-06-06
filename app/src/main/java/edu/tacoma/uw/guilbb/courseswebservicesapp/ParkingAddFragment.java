package edu.tacoma.uw.guilbb.courseswebservicesapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import edu.tacoma.uw.guilbb.courseswebservicesapp.model.Course;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParkingAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * This class is currently not used in the application
 * Adding custom parking lots is now done through a request system to prevent abuse and duplicates
 * of parking lots.
 *
 * If a user wants to add a parking lot, they can click the FAB on the map to request a lot at their
 * current position.
 *
 * This class may be used in the future once a permissions/verification system is put in place for users
 */
public class ParkingAddFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "DisplayMessageActivity";

    private String mParam1;
    private String mParam2;

    private AddListener mAddListener;

    public interface AddListener{
        public void addCourse(Course course);
    }

    public ParkingAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseAddFragment.
     */

    public static ParkingAddFragment newInstance(String param1, String param2) {
        ParkingAddFragment fragment = new ParkingAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates the Parking add fragment from the saved instance state
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddListener = (AddListener) getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Sets up View for the parking add screen
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_course_add, container, false);
        getActivity().setTitle("Add a New Course");
        final EditText courseIdEditText = v.findViewById(R.id.add_course_id);
        final EditText courseShortDescEditText = v.findViewById(R.id.add_course_short_desc);
        final EditText courseLongDescEditText = v.findViewById(R.id.add_course_long_desc);
        final EditText coursePrereqsEditText = v.findViewById(R.id.add_course_prereqs);

        Button addButton = v.findViewById(R.id.btn_add_course);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "add was entered");
                String courseId = courseIdEditText.getText().toString();
                String courseShortDesc = courseShortDescEditText.getText().toString();
                String courseLongDesc = courseLongDescEditText.getText().toString();
                String coursePrereqs = coursePrereqsEditText.getText().toString();
                String courseLat = "doesnt matter there is no button for this";
                String courseLong = "doesnt matter there is no button for this";
                Course course = new Course (courseId, courseShortDesc, courseLongDesc, coursePrereqs, courseLat, courseLong);
                if (mAddListener != null) {
                    mAddListener.addCourse(course);
                }
            }
        });

        return v;
    }
}
