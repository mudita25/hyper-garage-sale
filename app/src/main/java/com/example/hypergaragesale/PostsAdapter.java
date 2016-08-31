package com.example.hypergaragesale;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private ArrayList<BrowsePosts> mDataset;
    static Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView mTitle;
        public TextView mPrice;
        public ImageView mImage;
        public ImageView mIcon;
        public TextView mLocation;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mTitle = (TextView) itemView.findViewById(R.id.titleView);
            mPrice = (TextView) itemView.findViewById(R.id.priceView);
            mLocation = (TextView) itemView.findViewById(R.id.locationView);
            mImage = (ImageView) itemView.findViewById(R.id.imageView2);
            mIcon = (ImageView) itemView.findViewById(R.id.imageView3);
        }

        // Implement view click Listener when make each row of RecycleView clickable
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Intent intent = new Intent(context,PostDetailsActivity.class);
            intent.putExtra("POSITION", position + 1);
            context.startActivity(intent);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PostsAdapter(Context myContext, ArrayList<BrowsePosts> myDataset) {
        context = myContext;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_text_view, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh ;
        vh = new ViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // - get elements from dataset at this position
        // - replace the contents of the views with that elements
        holder.mTitle.setText(mDataset.get(position).mTitle);
        holder.mPrice.setText(mDataset.get(position).mPrice);
        String lat = mDataset.get(position).mLat;
        String lon = mDataset.get(position).mLong;

        if(lat != null && lat.length() != 0) {
            Double latitude = Double.valueOf(lat);
            Double longitude = Double.valueOf(lon);
            Geocoder gc = new Geocoder(context, Locale.getDefault());
            List<Address> addresses= null;
            try {
                if (latitude != null && longitude != null) {
                    addresses = gc.getFromLocation(latitude, longitude, 1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String location = null;
            if (addresses != null) {
                location = addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea();
            }
            holder.mLocation.setText(location);
        }
        else{
            holder.mLocation.setText(lat);
        }

        String imagePathUri = mDataset.get(position).mImage;
        if(imagePathUri.length() != 0) {
            Uri imageUri = Uri.parse(imagePathUri);
            String[] proj = { MediaStore.Images.Media.DATA };
            CursorLoader cursorLoader = new CursorLoader(context, imageUri, proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            if(cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                String  imagePath = cursor.getString(column_index);
                Bitmap bitmap = decodeSampledBitmap(imagePath, 75, 75);
                holder.mImage.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 75, 75));
            }
            else{
                holder.mImage.setImageBitmap(null);
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        else{
            holder.mImage.setImageBitmap(null);
        }
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
    // Return the size of dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}