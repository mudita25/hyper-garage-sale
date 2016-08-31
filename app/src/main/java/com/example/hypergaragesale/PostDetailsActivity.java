package com.example.hypergaragesale;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PostDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SQLiteDatabase db;
    private TextView mTitle;
    private TextView mDesc;
    private TextView mPrice;
    private Double latitude;
    private Double longitude;
    private int imageCount = 0;
    List<Address> addresses = null;
    final String[] uris = new String[3];
    Uri imageUri1, imageUri2, imageUri3;
    ImageView imageView;
    private ShareActionProvider mShareActionProvider;
    private String title, desc, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.post_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        int p = (int) getIntent().getExtras().get("POSITION");

        PostsDbHelper mDbHelper = new PostsDbHelper(this);
        String selectQuery = "SELECT * FROM " + Posts.PostEntry.TABLE_NAME + " WHERE _id = " + (p);
        db = mDbHelper.getReadableDatabase();
        Cursor res = db.rawQuery(selectQuery, null);

        mTitle = (TextView) findViewById(R.id.item);
        mDesc = (TextView) findViewById(R.id.desc);
        mPrice = (TextView) findViewById(R.id.price);

        while (res.moveToNext()) {
            title = res.getString(1);
            desc = res.getString(2);
            price = res.getString(3);
            mTitle.setText(title);
            mDesc.setText(desc);
            mPrice.setText(price);

            uris[0] = res.getString(4);
            uris[1] =  res.getString(5);
            uris[2] = res.getString(6);
            imageUri1 = Uri.parse(uris[0]);
            imageUri2 = Uri.parse(uris[1]);
            imageUri3 = Uri.parse(uris[2]);

            for (int i = 0; i < 3; i++){
                if(getPathFromUri(Uri.parse(uris[i])) != null){
                    imageCount++;
                }
            }

            int l1 =res.getString(7).length();
            int l2 =res.getString(8).length();
            if(res.getString(7) != null && l1 != 0 && res.getString(8) != null && l2 != 0) {
                latitude = Double.valueOf(res.getString(7));
                longitude = Double.valueOf(res.getString(8));
            }
        }

        //close the cursor
        res.close();
        db.close();

        Gallery gallery = (Gallery) findViewById(R.id.gallery1);
        Context context = this;
        if(imageCount > 0) {
            if (gallery != null) {
                gallery.setAdapter(new ImageAdapter(context));
            }
            imageView = (ImageView) findViewById(R.id.imageSl);
        }

        // display the first image
        if(getPathFromUri(imageUri1) != null && imageUri1.toString().length() != 0) {
            String imagePath = getPathFromUri(imageUri1);
            imageView.setImageBitmap(decodeSampledBitmap(imagePath, 700, 700));

        }

        // display the images selected
        if (gallery != null) {
            gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id)
                {
                    String imagePath = getPathFromUri( Uri.parse(uris[position]));
                    if(imagePath != null) {
                        imageView.setImageBitmap(decodeSampledBitmap(imagePath, 700, 700));
                    }
                    else{
                        imageView.setImageBitmap(null);
                    }
                }
            });
        }

        if(latitude == null && longitude == null) {
            return;
        }

        Geocoder gc = new Geocoder(this, Locale.getDefault());
        try {
            if(latitude != null && longitude != null) {
                addresses = gc.getFromLocation(latitude, longitude, 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private String getPathFromUri(Uri uri){
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader cursorLoader = new CursorLoader(getApplicationContext(), uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        String imagePath = null;
        if(cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //cursor.moveToFirst();
            //Log.i("fileUri", "g"+ cursor.getString(column_index));
            imagePath = cursor.getString(column_index);
            cursor.close();
        }

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

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private int itemBackground;
        public ImageAdapter(Context c)
        {
            context = c;

            // sets a grey background; wraps around the images
            TypedArray a =obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();
        }
        // returns the number of images
        public int getCount() {
            return imageCount;
        }
        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }

        // returns the ID of an item
        public long getItemId(int position) {
            return position;
        }

        // returns an ImageView view
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            String imagePath = getPathFromUri(Uri.parse(uris[position]));
            imageView.setImageBitmap(decodeSampledBitmap(imagePath, 100, 100));
            imageView.setLayoutParams(new Gallery.LayoutParams(300, 300));
            imageView.setBackgroundResource(itemBackground);
            return imageView;
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        LatLng loc = new LatLng(latitude, longitude);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13));

        map.addMarker(new MarkerOptions()
                .title(addresses.get(0).getSubThoroughfare() + " " + addresses.get(0).getThoroughfare() + ", " + addresses.get(0).getLocality())
                .snippet(addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryCode())
                .position(loc));

        // map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        UiSettings ui = map.getUiSettings();
        ui.setAllGesturesEnabled(true);
        ui.setCompassEnabled(true);
        ui.setZoomControlsEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post_details_menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        return super.onCreateOptionsMenu(menu);
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    public void sharePost(MenuItem item) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "New Item Available!");
        String message = "Item: " + title + ", Price: " + price ;
        if(desc != null && desc.length() != 0)
            message += ", Desc: " + desc;
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
       startActivity(sendIntent);
    }
}
