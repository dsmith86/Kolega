package dsmith86.github.io.kolega;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import dsmith86.github.io.kolega.registration.SetupActivity;

public class ProfileFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final TextView realNameTextView, schoolTextView, majorTextView;

        realNameTextView = (TextView)view.findViewById(R.id.realNameTextView);
        schoolTextView = (TextView)view.findViewById(R.id.schoolTextView);
        majorTextView = (TextView)view.findViewById(R.id.majorTextView);

        ParseUser user = ParseUser.getCurrentUser();

        realNameTextView.setText(user.getString(ParseInterfaceWrapper.KEY_REAL_NAME));
        schoolTextView.setText(user.getString(ParseInterfaceWrapper.KEY_SCHOOL_NAME));
        majorTextView.setText(user.getString(ParseInterfaceWrapper.KEY_MAJOR));

        view.findViewById(R.id.editProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SetupActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}