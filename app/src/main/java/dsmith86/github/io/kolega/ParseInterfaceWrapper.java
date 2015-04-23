package dsmith86.github.io.kolega;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Daniel on 3/21/2015.
 */
public class ParseInterfaceWrapper {
    public final static String ENTITY_SCHOOL = "School";
    public final static String ENTITY_CLASS = "Class";
    public final static String ENTITY_MESSAGE = "Message";

    public final static String KEY_SCHOOL_NAME = "schoolName";
    public final static String KEY_CLASS_DESCRIPTION = "classDescription";
    public final static String KEY_REAL_NAME = "realName";
    public final static String KEY_MAJOR = "major";
    public final static String KEY_PROFILE_IMAGE = "profileImage";
    public final static String KEY_MESSAGE_CONTENTS = "contents";
    public final static String KEY_MESSAGE_ORIGIN = "origin";

    public static void registerUser(String username, String password, SignUpCallback callback) {
        ParseUser user = new ParseUser();

        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(callback);
    }
}
