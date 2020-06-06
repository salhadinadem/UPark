package edu.tacoma.uw.guilbb.courseswebservicesapp;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import edu.tacoma.uw.guilbb.courseswebservicesapp.model.Member;

import static edu.tacoma.uw.guilbb.courseswebservicesapp.model.Member.isValidEmail;
import static edu.tacoma.uw.guilbb.courseswebservicesapp.model.Member.isValidPassword;
import static edu.tacoma.uw.guilbb.courseswebservicesapp.model.Member.parseCourseJson;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

public class MemberTest {

    @Test
    public void testAccountConstructor() {
        assertNotNull(new Member("mmuppa@uw.edu", "test1@3"));
    }

    @Test
    public void testAccountConstructorBadEmail() {
        try {
            new Member("mmuppauw.edu", "test1@3");
            fail("Account created with invalid email");
        } catch(IllegalArgumentException e) {

        }
    }

    @Test
    public void testAccountConstructorBadPassword() {
        try {
            new Member("mmuppa@uw.edu", "test13");
            fail("Account created with invalid password");
        } catch(IllegalArgumentException e) {

        }
    }

    @Test
    public void testAccountConstructorBadPasswordEmail() {
        try {
            new Member("mmuppauw.edu", "test13");
            fail("Account created with invalid password and email");
        } catch(IllegalArgumentException e) {

        }
    }

    @Test
    public void testAccountSetEmailBadNoAt() {
        try {
            Member a = new Member("mmuppa@uw.edu", "test1@3");
            a.setEmail("mmuppauw.edu");
            fail("Account created with invalid email");
        } catch(IllegalArgumentException e) {

        }
    }

    @Test
    public void testAccountSetEmailBadNoPeriod() {
        try {
            Member a = new Member("mmuppa@uw.edu", "test1@3");
            a.setEmail("mmuppa@uwedu");
            fail("Account created with invalid email");
        } catch(IllegalArgumentException e) {

        }
    }

    @Test
    public void testAccountSetPasswordBadNoSymbol() {
        try {
            Member a = new Member("mmuppa@uw.edu", "test1@3");
            a.setPassword("test13");
            fail("Account created with invalid password");
        } catch(IllegalArgumentException e) {

        }
    }

    @Test
    public void testAccountSetPasswordBadNoNum() {
        try {
            Member a = new Member("mmuppa@uw.edu", "test1@3");
            a.setPassword("test@");
            fail("Account created with invalid password");
        } catch(IllegalArgumentException e) {

        }
    }

    @Test
    public void testAccountSetEmailGood() {
        Member a = new Member("mmuppa@uw.edu", "test1@3");
        a.setEmail("superman@uw.edu");
        assertEquals("superman@uw.edu", a.getmEmail());
    }

    @Test
    public void testAccountSetPasswordGood() {
        Member a = new Member("mmuppa@uw.edu", "test1@3");
        a.setPassword("faried@35");
        assertEquals("faried@35", a.getmPassword());
    }

    @Test
    public void testAccountIsValidEmailTrue() {
        assertEquals(true, isValidEmail("mmuppa@uw.edu"));
    }

    @Test
    public void testAccountIsValidEmailFalse() {
        assertEquals(false, isValidEmail("mmuppauw.edu"));
    }

    @Test
    public void testAccountIsValidPasswordTrue() {
        assertEquals(true, isValidPassword("superman@50"));
    }

    @Test
    public void testAccountIsValidPasswordFalse() {
        assertEquals(false, isValidPassword("love"));
    }


}
