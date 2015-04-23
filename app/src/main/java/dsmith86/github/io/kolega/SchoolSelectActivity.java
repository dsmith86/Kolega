package dsmith86.github.io.kolega;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import dsmith86.github.io.kolega.registration.RegisterActivity;


public class SchoolSelectActivity extends ActionBarActivity {

    public final static String EXTRA_SELECTED_SCHOOL = "EXTRA_SELECTED_SCHOOL";

    ListView listView;
    ArrayAdapter<String> adapter;

    Button selectButton;

    int selectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_select);

        selectButton = (Button)findViewById(R.id.schoolSelectButton);
        selectButton.setOnClickListener(onSchoolSelectButtonClickListener);

        listView = (ListView)findViewById(android.R.id.list);

        ArrayList<String> schoolList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, schoolList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onSchoolItemClickListener);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseInterfaceWrapper.ENTITY_SCHOOL);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> schools, ParseException e) {
                for (ParseObject school : schools) {
                    adapter.add(school.getString(ParseInterfaceWrapper.KEY_SCHOOL_NAME));
                }
            }
        });

        findViewById(R.id.newSchoolButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText newSchoolEditText = new EditText(SchoolSelectActivity.this);
                newSchoolEditText.setInputType(InputType.TYPE_CLASS_TEXT);

                new AlertDialog.Builder(SchoolSelectActivity.this)
                        .setTitle(getResources().getString(R.string.school_select_new))
                        .setView(newSchoolEditText)
                        .setPositiveButton(getResources().getString(R.string.generic_add), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String newSchool = newSchoolEditText.getText().toString();

                                if (!newSchool.isEmpty()) {
                                    ParseObject newSchoolObject = new ParseObject(ParseInterfaceWrapper.ENTITY_SCHOOL);

                                    newSchoolObject.put(ParseInterfaceWrapper.KEY_SCHOOL_NAME, newSchool);

                                    newSchoolObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            adapter.insert(newSchool, 0);
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.generic_cancel), null)
                        .create().show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_school_select, menu);
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

    private AdapterView.OnItemClickListener onSchoolItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            if (selectedIndex >= 0) {
                View oldItemView = listView.getChildAt(selectedIndex);
                oldItemView.setBackgroundColor(Color.TRANSPARENT);
            }

            String schoolName = adapter.getItem(position);

//            selectButton.setText(getResources().getString(R.string.school_select_button)
//             + " " + schoolName);
            selectButton.setEnabled(true);

            selectedIndex = position;

            view.setBackgroundColor(Color.LTGRAY);
        }
    };

    private View.OnClickListener onSchoolSelectButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ParseUser user = ParseUser.getCurrentUser();

            user.put(ParseInterfaceWrapper.KEY_SCHOOL_NAME, adapter.getItem(selectedIndex));

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    SchoolSelectActivity.this.finish();
                }
            });
        }
    };
}
