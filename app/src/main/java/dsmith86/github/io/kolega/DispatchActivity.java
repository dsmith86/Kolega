package dsmith86.github.io.kolega;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import dsmith86.github.io.kolega.registration.RegisterActivity;


public class DispatchActivity extends ActionBarActivity {

    public final static String EXTRA_USERNAME = "dsmith86.github.io.kolega.extra.USERNAME";
    public final static String EXTRA_PASSWORD = "dsmith86.github.io.kolega.extra.PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        if (extras == null) {
            Intent intent;

            if (ParseUser.getCurrentUser() == null) {
                intent = new Intent(this, RegisterActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
        } else {
            String username = extras.getString(EXTRA_USERNAME);
            String password = extras.getString(EXTRA_PASSWORD);
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {
                        startActivity(new Intent(DispatchActivity.this, MainActivity.class));
                    }
                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
