package dsmith86.github.io.kolega;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Daniel on 3/21/2015.
 */
public class ParseInterfaceWrapper {
    public static void registerUser(String username, String password, SignUpCallback callback) {
        ParseUser user = new ParseUser();

        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(callback);
    }
}
