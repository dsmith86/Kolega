package dsmith86.github.io.kolega.registration;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import dsmith86.github.io.kolega.DispatchActivity;
import dsmith86.github.io.kolega.ParseInterfaceWrapper;
import dsmith86.github.io.kolega.R;
import dsmith86.github.io.kolega.SchoolSelectActivity;


public class SetupActivity extends ActionBarActivity {

    EditText realNameEditText, majorEditText;
    TextView schoolTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        realNameEditText = (EditText)findViewById(R.id.realNameEditText);
        majorEditText = (EditText)findViewById(R.id.majorEditText);
        schoolTextView = (TextView)findViewById(R.id.schoolTextView);

        findViewById(R.id.schoolSelectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetupActivity.this, SchoolSelectActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.continueButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();

                String realName = realNameEditText.getText().toString();
                String major = majorEditText.getText().toString();

                if (!realName.isEmpty() && !major.isEmpty() && user.get(ParseInterfaceWrapper.KEY_SCHOOL_NAME) != null) {
                    user.put(ParseInterfaceWrapper.KEY_REAL_NAME, realName);
                    user.put(ParseInterfaceWrapper.KEY_MAJOR, major);

                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Intent intent = new Intent(SetupActivity.this, DispatchActivity.class);
                            startActivity(intent);
                        }
                    });

                } else {
                    new AlertDialog.Builder(SetupActivity.this)
                            .setMessage(R.string.setup_error_required)
                            .setPositiveButton(getResources().getString(R.string.generic_ok), null)
                            .create()
                            .show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        ParseUser user = ParseUser.getCurrentUser();

        String schoolName = user.get(ParseInterfaceWrapper.KEY_SCHOOL_NAME).toString();

        if (!schoolName.isEmpty()) {
            schoolTextView.setText(schoolName);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup, menu);
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
