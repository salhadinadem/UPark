package edu.tacoma.uw.guilbb.courseswebservicesapp.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A class for building a member object to put into the database
 */
public class Member implements Serializable {

    private String email;
    private String password;

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    public Member(String theEmail, String thePassword){
        if (isValidEmail(theEmail)) {
            this.email = theEmail;
        } else {
            throw new IllegalArgumentException("Invalid email");
        }

        if (isValidPassword(thePassword)) {
            this.password = thePassword;
        } else {
            throw new IllegalArgumentException("Invalid password");
        }
    }

    /**
     * Email validation pattern.
     */
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    /**
     * Validates if the given input is a valid email address.
     *
     * @param email        The email to validate.
     * @return {@code true} if the input is a valid email. {@code false} otherwise.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    private final static int PASSWORD_LEN = 6;
    /**
     * Validates if the given password is valid.
     * Valid password must be at last 6 characters long
     * with at least one digit and one symbol.
     *
     * @param password        The password to validate.
     * @return {@code true} if the input is a valid password.
     * {@code false} otherwise.
     */
    public static boolean isValidPassword(String password) {
        boolean foundDigit = false, foundSymbol = false;
        if  (password == null ||
                password.length() < PASSWORD_LEN)
            return false;
        for (int i=0; i<password.length(); i++) {
            if (Character.isDigit(password.charAt(i)))
                foundDigit = true;
            if (!Character.isLetterOrDigit(password.charAt(i)))
                foundSymbol = true;
        }
        return foundDigit && foundSymbol;
    }

    public String getmEmail() {
        return email;
    }


    public String getmPassword() {
        return password;
    }


    public void setEmail(String email) {
        if (isValidEmail(email)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Invalid email");
        }
    }

    public void setPassword(String password) {
        if (isValidPassword(password)) {
            this.password = password;
        } else {
            throw new IllegalArgumentException("Invalid password");
        }
    }


    public static List<Member> parseCourseJson(String memberJson) throws JSONException {
        List<Member> memberList = new ArrayList<>();
        if(memberJson != null){

            JSONArray arr = new JSONArray(memberJson);

            for(int i = 0; i < arr.length(); i++){
                JSONObject obj = arr.getJSONObject(i);
                Member member = new Member( obj.getString(Member.EMAIL), obj.getString(Member.PASSWORD));
                memberList.add(member);
            }
        }
        return memberList;
    }



}
