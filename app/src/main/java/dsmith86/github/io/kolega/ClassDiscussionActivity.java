package dsmith86.github.io.kolega;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class ClassDiscussionActivity extends ActionBarActivity {

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_discussion);

        final ParseUser user = ParseUser.getCurrentUser();

        final String schoolName = user.get(ParseInterfaceWrapper.KEY_SCHOOL_NAME).toString();
        final String classDescription = getIntent().getExtras().getString(ParseInterfaceWrapper.KEY_CLASS_DESCRIPTION);

        ListView listView = (ListView)findViewById(android.R.id.list);

        ArrayList<String> chatThread = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, chatThread);

        listView.setAdapter(adapter);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseInterfaceWrapper.ENTITY_MESSAGE);

        query.whereEqualTo(ParseInterfaceWrapper.KEY_SCHOOL_NAME, schoolName);
        query.whereEqualTo(ParseInterfaceWrapper.KEY_CLASS_DESCRIPTION, classDescription);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                for (ParseObject message : messages) {
                    adapter.add(message.getString(ParseInterfaceWrapper.KEY_MESSAGE_CONTENTS));
                }
            }
        });

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

                    newMessageObject.put(ParseInterfaceWrapper.KEY_MESSAGE_CONTENTS, newMessage);

                    newMessageObject.saveInBackground();

                    adapter.add(newMessage);

                    chatEditText.setText("");
                }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
