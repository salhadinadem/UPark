package edu.tacoma.uw.guilbb.courseswebservicesapp.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {

    private String mCourseId;
    private String mCourseShortDesc;
    private String mCourseLongDesc;
    private String mCoursePrereqs;

    public static final String ID = "id";
    public static final String SHORT_DESC = "shortdesc";
    public static final String LONG_DESC = "longdesc";
    public static final String PRE_REQS = "prereqs";
    public static final String AVAILABILITY = "availability";

    public Course(String theCourseId, String theCourseShortDesc, String theCourseLongDesc,
           String theCoursePrereqs){//, String theAvailability){
        mCourseId = theCourseId;
        mCourseShortDesc = theCourseShortDesc;
        mCourseLongDesc = theCourseLongDesc;
        mCoursePrereqs = theCoursePrereqs;
        //mAvailability = theAvailability;
    }

    public String getmCourseShortDesc() {
        return mCourseShortDesc;
    }

    public String getmCoursePrereqs() {
        return mCoursePrereqs;
    }

    public String getmCourseLongDesc() {
        return mCourseLongDesc;
    }

    public String getmCourseId() {
        return mCourseId;
    }

    public void setmCourseShortDesc(String mCourseShortDesc) {
        this.mCourseShortDesc = mCourseShortDesc;
    }

    public void setmCoursePrereqs(String mCoursePrereqs) {
        this.mCoursePrereqs = mCoursePrereqs;
    }

    public void setmCourseLongDesc(String mCourseLongDesc) {
        this.mCourseLongDesc = mCourseLongDesc;
    }

    public void setmCourseId(String mCourseId) {
        this.mCourseId = mCourseId;
    }

    public static List<Course> parseCourseJson(String courseJson) throws JSONException {
        List<Course> courseList = new ArrayList<>();
        if(courseJson != null){

            JSONArray arr = new JSONArray(courseJson);

            for(int i = 0; i < arr.length(); i++){
                JSONObject obj = arr.getJSONObject(i);
                Course course = new Course(obj.getString(Course.ID), obj.getString(Course.SHORT_DESC),
                        obj.getString(Course.LONG_DESC), obj.getString(Course.PRE_REQS));
                courseList.add(course);
            }
        }
        return courseList;
    }



}
