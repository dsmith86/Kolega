package dsmith86.github.io.kolega;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class ClassesFragment extends ListFragment {
    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classes, container, false);

        listView = (ListView)view.findViewById(android.R.id.list);

        ArrayList<String> classList = new ArrayList<>();

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, classList);

        listView.setAdapter(adapter);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseInterfaceWrapper.ENTITY_CLASS);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> classes, ParseException e) {
                for (ParseObject _class : classes) {
                    adapter.add(_class.getString(ParseInterfaceWrapper.KEY_CLASS_DESCRIPTION));
                }
            }
        });

        view.findViewById(R.id.newClassButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText newClassEditText = new EditText(getActivity());
                newClassEditText.setInputType(InputType.TYPE_CLASS_TEXT);

                new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.button_classes_new))
                        .setView(newClassEditText)
                        .setPositiveButton(getResources().getString(R.string.generic_add), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String newClass = newClassEditText.getText().toString();

                                if (!newClass.isEmpty()) {
                                    ParseObject newClassObject = new ParseObject(ParseInterfaceWrapper.ENTITY_CLASS);

                                    newClassObject.put(ParseInterfaceWrapper.KEY_CLASS_DESCRIPTION, newClass);

                                    newClassObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            adapter.insert(newClass, 0);
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.generic_cancel), null)
                        .create().show();
            }
        });

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String classDescription = adapter.getItem(position);

        Intent intent = new Intent(getActivity(), ClassDiscussionActivity.class);

        intent.putExtra(ParseInterfaceWrapper.KEY_CLASS_DESCRIPTION, classDescription);

        startActivity(intent);

    }
}
