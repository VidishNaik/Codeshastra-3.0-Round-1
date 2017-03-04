package com.example.vidish.round1;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ShowDetails extends AppCompatActivity {

    TextView name,price,company,description;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);
        setTitle(getIntent().getStringExtra("name"));
        Log.v("ShowDetails","http://"+MainActivity.ip+"/farmer.php?name="+getIntent().getStringExtra("name"));
        name = (TextView) findViewById(R.id.text_view_name);
        price = (TextView) findViewById(R.id.text_view_price);
        company = (TextView) findViewById(R.id.text_view_company);
        description = (TextView) findViewById(R.id.text_view_description);
        imageView = (ImageView) findViewById(R.id.image_view_detail);

        DetailAsyncTask detailAsyncTask = new DetailAsyncTask();
        detailAsyncTask.execute("http://"+MainActivity.ip+"/farmer.php?name="+getIntent().getStringExtra("name"));
        Log.v("ShowDetails","http://"+MainActivity.ip+"/farmer.php?name="+getIntent().getStringExtra("name"));
    }


    private class DetailAsyncTask extends AsyncTask<String, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(ShowDetails.this);

        @Override
        protected Void doInBackground(String... urls) {
            publishProgress();
            if (urls.length < 1 || urls[0] == null)
                return null;
            URL url;
            try {
                url = new URL(urls[0]);
            } catch (MalformedURLException exception) {
                Log.e("ClassSelector", "Error with creating URL", exception);
                return null;
            }
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
            }
            if (jsonResponse == null) {
                return null;
            }
            extractProductJSONResponse(jsonResponse);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void v) {
            progressDialog.dismiss();
           // Toast.makeText(ShowDetails.this, ""+description, Toast.LENGTH_SHORT).show();
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            if (url == null) {
                return jsonResponse;
            }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                //if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                //}
            } catch (IOException e) {
                jsonResponse = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private void extractProductJSONResponse(String jsonResponse) {
            List<String> list = new ArrayList<>();

            try {
                JSONObject root = new JSONObject(jsonResponse);
                JSONArray result = root.optJSONArray("result");
                if (result.length() == 0 || result == null)
                    return;
                /*JSONObject object=result.getJSONObject(i);
                String col=object.getString("col");
                if(col!=null && !col.equals("null")) {
                    subjects.add(col);
                }*/
                JSONObject object = result.getJSONObject(0);
                name.setText(object.getString("name"));
                price.setText(object.getString("price"));
                description.setText(object.getString("description"));

                Log.v("desc",object.getString("description"));
                company.setText(object.getString("company"));

                DownloadImageFromInternet dowhloadImageFromInternet = new DownloadImageFromInternet(imageView);
                dowhloadImageFromInternet.execute("http://"+MainActivity.ip+"/ladies/"+object.getString("iname"));
                Log.v("ShowDetails","http://"+MainActivity.ip+"/ladies/"+object.getString("iname"));

            } catch (JSONException e) {
                Log.v("JSONParser", "exception", e);
            }
        }
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
