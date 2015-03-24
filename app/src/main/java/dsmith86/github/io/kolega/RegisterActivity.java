package dsmith86.github.io.kolega;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.SignUpCallback;


public class RegisterActivity extends ActionBarActivity {

    private EditText usernameEditText, passwordEditText, confirmEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
        Button registerButton, signInButton;

        usernameEditText = (EditText)findViewById(R.id.usernameEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        confirmEditText = (EditText)findViewById(R.id.confirmEditText);
        registerButton = (Button)findViewById(R.id.registerButton);
        signInButton = (Button)findViewById(R.id.signInButton);

        registerButton.setOnClickListener(onRegisterButtonClickListener);
        signInButton.setOnClickListener(onSignInButtonClickListener);
    }

    private View.OnClickListener onRegisterButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String username, password, confirm;

            username = usernameEditText.getText().toString();
            password = passwordEditText.getText().toString();
            confirm = confirmEditText.getText().toString();

            if (!username.trim().isEmpty() && !password.trim().isEmpty() && !confirm.trim().isEmpty()) {
                if (password.equals(confirm)) {
                    ParseInterfaceWrapper.registerUser(username, password, new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Intent intent = new Intent(RegisterActivity.this, DispatchActivity.class);
                                intent.putExtra(DispatchActivity.EXTRA_USERNAME, username);
                                intent.putExtra(DispatchActivity.EXTRA_PASSWORD, password);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.d("registrationStatus", "failure");
                            }
                        }
                    });
                }
            }
        }
    };

    private View.OnClickListener onSignInButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
            startActivity(intent);
        }
    };
}
