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
 */
public class ParkingAddFragment extends Fragment {

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddListener = (AddListener) getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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
                Course course = new Course (courseId, courseShortDesc, courseLongDesc, coursePrereqs);
                if (mAddListener != null) {
                    mAddListener.addCourse(course);
                }
            }
        });

        return v;
    }
}
