package dsmith86.github.io.kolega;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Daniel on 3/21/2015.
 */
public class ParseInterfaceWrapper {
    public final static String KEY_SCHOOL_NAME = "KEY_SCHOOL_NAME";
    public final static String KEY_REAL_NAME = "KEY_REAL_NAME";
    public final static String KEY_MAJOR = "KEY_MAJOR";

    public static void registerUser(String username, String password, SignUpCallback callback) {
        ParseUser user = new ParseUser();

        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(callback);
    }
}
