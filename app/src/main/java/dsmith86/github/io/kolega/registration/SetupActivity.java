package dsmith86.github.io.kolega.registration;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaActionSound;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import dsmith86.github.io.kolega.DispatchActivity;
import dsmith86.github.io.kolega.ParseInterfaceWrapper;
import dsmith86.github.io.kolega.R;
import dsmith86.github.io.kolega.SchoolSelectActivity;


public class SetupActivity extends ActionBarActivity {

    private static final int INTENT_TAKE_PICTURE = 0;
    private static final int INTENT_IMPORT_PICTURE = 1;

    private Uri imageFileUri;

    ParseUser user;

    ImageView profileImageView;
    EditText realNameEditText, majorEditText;
    TextView schoolTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        user = ParseUser.getCurrentUser();

        profileImageView = (ImageView)findViewById(R.id.profileImageView);

        realNameEditText = (EditText)findViewById(R.id.realNameEditText);
        majorEditText = (EditText)findViewById(R.id.majorEditText);
        schoolTextView = (TextView)findViewById(R.id.schoolTextView);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SetupActivity.this)
                        .setTitle(getResources().getString(R.string.setup_profile_image))
                        .setPositiveButton(getResources().getString(R.string.setup_profile_image_camera), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String imageFileName = UUID.randomUUID().toString() + ".jpg";
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(MediaStore.Images.Media.TITLE, imageFileName);
                                imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                i.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                                try {
                                    startActivityForResult(i, INTENT_TAKE_PICTURE);
                                } catch (ActivityNotFoundException e) {
                                    Log.e("error", "Could not start a Camera Intent");
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.generic_cancel), null)
                        .create().show();
            }
        });

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        final ProgressDialog progress = new ProgressDialog(SetupActivity.this);
        progress.setTitle(getResources().getString(R.string.generic_please_wait));
        progress.setMessage(getResources().getString(R.string.setup_upload_image_progress));
        progress.show();

        switch (requestCode) {
            case INTENT_TAKE_PICTURE:
            case INTENT_IMPORT_PICTURE:
                if (imageFileUri != null) {
                    profileImageView.setImageURI(imageFileUri);

                    try {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        InputStream inputStream;

                        inputStream = getContentResolver().openInputStream(imageFileUri);
                        byte[] buf = new byte[1024];

                        int n;

                        while (-1 != (n = inputStream.read(buf))) {
                            byteArrayOutputStream.write(buf, 0, n);
                        }

                        byte[] bytes = byteArrayOutputStream.toByteArray();

                        String imageFilename = String.format("%s%s", ParseInterfaceWrapper.KEY_PROFILE_IMAGE, ".png");
                        ParseFile image = new ParseFile(imageFilename, bytes);

                        user.put(ParseInterfaceWrapper.KEY_PROFILE_IMAGE, image);

                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                progress.dismiss();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        user = ParseUser.getCurrentUser();

        String schoolName = user.get(ParseInterfaceWrapper.KEY_SCHOOL_NAME).toString();
        String realName = user.get(ParseInterfaceWrapper.KEY_REAL_NAME).toString();
        String major = user.get(ParseInterfaceWrapper.KEY_MAJOR).toString();

        ParseFile profileImage = (ParseFile)user.get(ParseInterfaceWrapper.KEY_PROFILE_IMAGE);

        profileImage.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profileImageView.setImageBitmap(bitmap);
            }
        });

        if (!schoolName.isEmpty()) {
            schoolTextView.setText(schoolName);
        }

        if (!realName.isEmpty()) {
            realNameEditText.setText(realName);
        }

        if (!major.isEmpty()) {
            majorEditText.setText(major);
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
