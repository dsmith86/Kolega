package dsmith86.github.io.kolega;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Daniel on 3/21/2015.
 */
public class KolegaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, getResources().getString(R.string.parse_application_id), getResources().getString(R.string.parse_client_key));
        ParseObject.registerSubclass(ParseUser.class);
    }
}
