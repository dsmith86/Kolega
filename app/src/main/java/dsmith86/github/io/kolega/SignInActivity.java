package dsmith86.github.io.kolega;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class SignInActivity extends ActionBarActivity {

    private EditText usernameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
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

    private void init() {
        Button signInButton;

        usernameEditText = (EditText)findViewById(R.id.usernameEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        signInButton = (Button)findViewById(R.id.signInButton);

        signInButton.setOnClickListener(signInButtonOnClickListener);
    }

    private View.OnClickListener signInButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String username, password;

            username = usernameEditText.getText().toString();
            password = passwordEditText.getText().toString();

            if (!username.trim().isEmpty() && !password.trim().isEmpty()) {
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            new AlertDialog.Builder(SignInActivity.this)
                                    .setMessage(getResources().getString(R.string.sign_in_error))
                                    .setPositiveButton(getResources().getString(R.string.generic_ok), null)
                                    .create()
                                    .show();
                        }
                    }
                });
            }
        }
    };
}
