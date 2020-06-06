package edu.tacoma.uw.guilbb.courseswebservicesapp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to help put parking lots into the database
 */
public class CourseContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Course> ITEMS = new ArrayList<Course>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Course> ITEM_MAP = new HashMap<String, Course>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createCourseItem(i));
        }
    }

    private static void addItem(Course item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getmCourseId(), item);
    }

    private static Course createCourseItem(int position) {
        return new Course("Id" + position, "short desc " + position,
                "Long desc" + position, "Pre reqs" + position,
                Course.LAT + position, Course.LONG + position);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }


}
