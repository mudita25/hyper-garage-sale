package com.example.hypergaragesale;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewPostActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private SQLiteDatabase db;
    private ContentValues values;
    private EditText titleText;
    private EditText descText;
    private EditText priceText;
    private EditText addressText;
    private ImageButton imageButton;
    private Button addressButton;
    private Button coordButton;
    private ImageView imageCaptured1;
    private ImageView imageCaptured2;
    private ImageView imageCaptured3;
    private static final int IMAGE_REQUEST_CODE = 100;
    private Uri fileUri= null, uri1, uri2, uri3;
    private int count = 0;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    String mLatitudeText;
    String mLongitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        fileUri = null;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        titleText = (EditText) findViewById(R.id.textView_title);
        descText = (EditText) findViewById(R.id.textView_desc);
        priceText = (EditText) findViewById(R.id.textView_price);
        addressText = (EditText) findViewById(R.id.textView_addr);
        imageButton = (ImageButton) findViewById(R.id.button);
        addressButton = (Button) findViewById(R.id.button2);
        coordButton = (Button) findViewById(R.id.button3);

        // Create an instance of GoogleAPIClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String filename = "Image_" + System.currentTimeMillis() + ".jpg";
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, filename);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent intent = new Intent();
                intent.setAction("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });

        final Geocoder fwdGeocoder = new Geocoder(this, Locale.getDefault());

        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String streetAddress = addressText.getText().toString();
                List<Address> address;
                try {
                    address = fwdGeocoder.getFromLocationName(streetAddress, 1);
                    if (address != null && address.size() != 0) {
                        Address location = address.get(0);
                        mLatitudeText = String.valueOf(location.getLatitude());
                        mLongitudeText = String.valueOf(location.getLongitude());
                    }
                    else{
                        AlertDialog builder1 = new AlertDialog.Builder(NewPostActivity.this).create();
                        builder1.setMessage("Address Not Found!");

                        builder1.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder1.show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        coordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    mLatitudeText = String.valueOf(mLastLocation.getLatitude());
                    mLongitudeText = String.valueOf(mLastLocation.getLongitude());
                }
            }
        });

        // Gets the data repository in write mode
        PostsDbHelper mDbHelper = new PostsDbHelper(this);
        db = mDbHelper.getWritableDatabase();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                count++;
                if(count == 1) {
                    imageCaptured1 = (ImageView) findViewById(R.id.imageView1);
                    uri1 = fileUri;

                    if (uri1.toString().length() != 0) {
                        String imagePath = getPathFromUri(uri1);
                        Bitmap bitmap = decodeSampledBitmap(imagePath, 100, 100);
                        imageCaptured1.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 175, 175));
                    }
                    else{
                        imageCaptured1.setImageBitmap(null);
                    }
                }
                else if(count == 2) {
                    imageCaptured2 = (ImageView) findViewById(R.id.imageView2);
                    uri2 = fileUri;

                    if (uri2.toString().length() != 0) {
                        String imagePath = getPathFromUri(uri2);
                        Bitmap bitmap = decodeSampledBitmap(imagePath, 100, 100);
                        imageCaptured2.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 175, 175));
                    }
                    else{
                        imageCaptured2.setImageBitmap(null);
                    }
                }
                else if(count == 3) {
                    imageCaptured3 = (ImageView) findViewById(R.id.imageView3);
                    uri3 = fileUri;
                    if (uri3.toString().length() != 0) {
                        String imagePath = getPathFromUri(uri3);
                        Bitmap bitmap = decodeSampledBitmap(imagePath, 100, 100);
                        imageCaptured3.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 175, 175));
                    }
                    else{
                        imageCaptured3.setImageBitmap(null);
                    }
                    imageButton.setEnabled(false);
                    imageButton.setAlpha(.5f);
                }

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private String getPathFromUri(Uri uri){
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader cursorLoader = new CursorLoader(getApplicationContext(), uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        String imagePath = cursor.getString(column_index);
        cursor.close();
        return imagePath;
    }

    private Bitmap decodeSampledBitmap(String imagePath,   int reqWidth, int reqHeight){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        int inSampleSize1 = 1;
        if (imageHeight > reqHeight || imageWidth > reqWidth) {
            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;

            while ((halfHeight / inSampleSize1) > reqHeight
                    && (halfWidth / inSampleSize1) > reqWidth) {
                inSampleSize1 *= 2;
            }
        }
        options.inSampleSize = inSampleSize1;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath,options);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        //Toast.makeText(getApplicationContext(), "onConnected() is called", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mLatitudeText = String.valueOf(mLastLocation.getLatitude());
                mLongitudeText = String.valueOf(mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Toast.makeText(getApplicationContext(), "onConnectionFailed() is called", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Toast.makeText(getApplicationContext(), "onConnectionSuspended() is called", Toast.LENGTH_SHORT).show();
    }


    private void showSnackBar(View v) {
        if (v == null) {
            Snackbar.make(findViewById(R.id.myCoordinatorLayout), R.string.new_post_snackbar,
                    Snackbar.LENGTH_LONG).show();
        }
        else {
            Snackbar.make(v, R.string.new_post_snackbar,
                    Snackbar.LENGTH_LONG).show();
        }
    }


    private void addPost() {

        // Create a new map of values, where column names are the keys
        values = new ContentValues();

        String itemName = titleText.getText().toString();
        if(itemName != null && itemName.length() != 0) {
            itemName = itemName.toUpperCase();
        }

        values.put(Posts.PostEntry.COLUMN_NAME_TITLE, itemName);

        values.put(Posts.PostEntry.COLUMN_NAME_DESCRIPTION, descText.getText().toString());

        String price = priceText.getText().toString();
        if(price != null && price.length() != 0 && price.charAt(0) != '$'){
            price = "$" + price;
        }
        values.put(Posts.PostEntry.COLUMN_NAME_PRICE, price);

       // Log.i("fileUri", "g"+fileUri.toString());
        if(uri1 != null) {
            values.put(Posts.PostEntry.COLUMN_NAME_IMAGE_URI, uri1.toString());
        }
        else{
            values.put(Posts.PostEntry.COLUMN_NAME_IMAGE_URI, "");
        }
        if(uri2 != null) {
            values.put(Posts.PostEntry.COLUMN_NAME_IMAGE_URI2, uri2.toString());
        }
        else{
            values.put(Posts.PostEntry.COLUMN_NAME_IMAGE_URI2, "");
        }
        if(uri3 != null) {
            values.put(Posts.PostEntry.COLUMN_NAME_IMAGE_URI3, uri3.toString());
        }
        else{
            values.put(Posts.PostEntry.COLUMN_NAME_IMAGE_URI3, "");
        }

        if(mLatitudeText != null && mLongitudeText != null) {
            values.put(Posts.PostEntry.COLUMN_NAME_LAT, mLatitudeText);
            values.put(Posts.PostEntry.COLUMN_NAME_LONG, mLongitudeText);
        }
        else{
            values.put(Posts.PostEntry.COLUMN_NAME_LAT, "");
            values.put(Posts.PostEntry.COLUMN_NAME_LONG, "");
        }
        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                Posts.PostEntry.TABLE_NAME,
                null,
                values);

        startActivity(new Intent(this, BrowsePostsActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_post) {
            showSnackBar(null);
            addPost();
        }
        return super.onOptionsItemSelected(item);
    }
}
