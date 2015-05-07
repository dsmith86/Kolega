package dsmith86.github.io.kolega;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ClassDiscussionActivity extends ActionBarActivity {

    ArrayAdapter<String[]> adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_discussion);

        final ParseUser user = ParseUser.getCurrentUser();

        final String schoolName = user.get(ParseInterfaceWrapper.KEY_SCHOOL_NAME).toString();
        final String classDescription = getIntent().getExtras().getString(ParseInterfaceWrapper.KEY_CLASS_DESCRIPTION);

        listView = (ListView)findViewById(android.R.id.list);

        final List<String[]> chatThread = new LinkedList<>();


        adapter = new ArrayAdapter<String[]>(this, android.R.layout.simple_list_item_2, android.R.id.text1, chatThread) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                String[] message = chatThread.get(position);
                TextView text1 = (TextView)view.findViewById(android.R.id.text1);
                TextView text2 = (TextView)view.findViewById(android.R.id.text2);

                String username = message[1];

                if (username.equals(user.getUsername())) {
                    view.setBackgroundColor(Color.parseColor("#2ecc71"));

                    text1.setTextColor(Color.WHITE);
                    text2.setTextColor(Color.WHITE);

                    text1.setGravity(Gravity.RIGHT);
                    text2.setGravity(Gravity.RIGHT);

                } else {
                    view.setBackgroundColor(Color.parseColor("#ecf0f1"));
                }

                text1.setText(message[0]);

                text2.setText(username);

                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(ClassDiscussionActivity.this)
                        .setMessage(getResources().getString(R.string.class_discussion_report))
                        .setNegativeButton(getResources().getString(R.string.generic_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.generic_report), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String objectId = adapter.getItem(position)[2];

                                ParseQuery<ParseObject> messageQuery = ParseQuery.getQuery(ParseInterfaceWrapper.ENTITY_MESSAGE);

                                messageQuery.whereEqualTo(ParseInterfaceWrapper.KEY_OBJECT_ID, objectId);

                                messageQuery.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> parseObjects, ParseException e) {
                                        ParseObject message = parseObjects.get(0);

                                        message.put(ParseInterfaceWrapper.KEY_REPORTED, true);
                                        message.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Toast.makeText(ClassDiscussionActivity.this, getResources().getString(R.string.generic_reported), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }).create().show();

                return false;
            }
        });

        fetchMessages(true);

        final EditText chatEditText = (EditText)findViewById(R.id.chatEditText);

        findViewById(R.id.sendMessageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMessage = chatEditText.getText().toString();

                if (!newMessage.trim().isEmpty()) {
                    ParseObject newMessageObject = new ParseObject(ParseInterfaceWrapper.ENTITY_MESSAGE);

                    newMessageObject.put(ParseInterfaceWrapper.KEY_SCHOOL_NAME, schoolName);
                    newMessageObject.put(ParseInterfaceWrapper.KEY_CLASS_DESCRIPTION, classDescription);
                    newMessageObject.put(ParseInterfaceWrapper.KEY_MESSAGE_ORIGIN, user.getObjectId());
                    newMessageObject.put(ParseInterfaceWrapper.KEY_USERNAME, user.getUsername());
                    newMessageObject.put(ParseInterfaceWrapper.KEY_REPORTED, false);

                    newMessageObject.put(ParseInterfaceWrapper.KEY_MESSAGE_CONTENTS, newMessage);

                    newMessageObject.saveInBackground();

                    adapter.add(new String[] {newMessage, user.getUsername(), newMessageObject.getObjectId()});

                    chatEditText.setText("");
                }
            }
        });
    }

    private void fetchMessages(boolean progressDialog) {
        adapter.clear();

        final ParseUser user = ParseUser.getCurrentUser();

        final String schoolName = user.get(ParseInterfaceWrapper.KEY_SCHOOL_NAME).toString();
        final String classDescription = getIntent().getExtras().getString(ParseInterfaceWrapper.KEY_CLASS_DESCRIPTION);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseInterfaceWrapper.ENTITY_MESSAGE);

        query.whereEqualTo(ParseInterfaceWrapper.KEY_SCHOOL_NAME, schoolName);
        query.whereEqualTo(ParseInterfaceWrapper.KEY_CLASS_DESCRIPTION, classDescription);

        final ProgressDialog progress = new ProgressDialog(ClassDiscussionActivity.this);
        progress.setTitle(getResources().getString(R.string.generic_please_wait));
        progress.setMessage(getResources().getString(R.string.classes_loading));

        if (progressDialog) {
            progress.show();
        }

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                for (ParseObject message : messages) {
                    adapter.add(new String[] {message.getString(ParseInterfaceWrapper.KEY_MESSAGE_CONTENTS),
                            message.getString(ParseInterfaceWrapper.KEY_USERNAME), message.getObjectId()});
                }
                listView.setSelection(adapter.getCount() - 1);

                progress.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_class_discussion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            fetchMessages(false);
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
