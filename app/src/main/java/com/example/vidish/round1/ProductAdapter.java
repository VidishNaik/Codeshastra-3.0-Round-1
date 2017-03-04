package com.example.vidish.round1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Vidish on 28-01-2017.
 */
public class ProductAdapter extends ArrayAdapter<ProductObject> {
    public ProductAdapter(Context context, List objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_product_details, parent, false);
        }
        final ProductObject currentProductObject = getItem(position);
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image_view_product);
        TextView textView = (TextView) listItemView.findViewById(R.id.text_view_product);

        textView.setText(currentProductObject.getName());
        String folder = currentProductObject.getFolder();
        DownloadImageFromInternet dowhloadImageFromInternet = new DownloadImageFromInternet(imageView);
        dowhloadImageFromInternet.execute("http://"+MainActivity.ip+"/ladies/"+currentProductObject.getImage());
        Log.v("ProducAdaper","http://"+MainActivity.ip+"/ladies/"+currentProductObject.getImage());

        return listItemView;
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            //Toast.makeText(getContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }


}
