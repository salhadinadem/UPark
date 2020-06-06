package edu.tacoma.uw.guilbb.courseswebservicesapp.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A model of each parking lot
 * NOTE: The "Course" name references are legacy code. All "Course" references can be thought of as
 * "Parking Lot" references
 *
 */
public class Course implements Serializable {

    private String mCourseId;
    private String mCourseShortDesc;
    private String mCourseLongDesc;
    private String mCoursePrereqs;
    private String mLat;
    private String mLong;

    public static final String ID = "id"; // Lot Name
    public static final String SHORT_DESC = "shortdesc"; // Accessible parking availability
    public static final String LONG_DESC = "longdesc"; // Parking Lot Price per hours
    public static final String PRE_REQS = "prereqs"; // Parking Lot availability
    public static final String LAT = "lat"; // Latitude of the lot
    public static final String LONG = "long"; // Longitude of the lot

    public static final String AVAILABILITY = "availability";

    public Course(String theCourseId, String theCourseShortDesc, String theCourseLongDesc,
           String theCoursePrereqs, String theLat, String theLong){//, String theAvailability){
        mCourseId = theCourseId;
        mCourseShortDesc = theCourseShortDesc;
        mCourseLongDesc = theCourseLongDesc;
        mCoursePrereqs = theCoursePrereqs;
        mLat = theLat;
        mLong= theLong;
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

    public String getmLat() {
        return mLat;
    }

    public String getmLong() {
        return mLong;
    }

    public void setmLong(String mLong) {
        this.mLong = mLong;
    }

    public void setmLat(String mLat) {
        this.mLat = mLat;
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
                        obj.getString(Course.LONG_DESC), obj.getString(Course.PRE_REQS),
                        obj.getString(Course.LAT), obj.getString(Course.LONG));
                courseList.add(course);
            }
        }
        return courseList;
    }



}
