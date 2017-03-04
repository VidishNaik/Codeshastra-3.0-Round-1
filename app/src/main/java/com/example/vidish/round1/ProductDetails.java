package com.example.vidish.round1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class ProductDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        setTitle("Product Details");

        ProductAsyncTask productAsyncTask = new ProductAsyncTask();
        productAsyncTask.execute("http://"+MainActivity.ip+"/timepass.php");
        Log.v("ProductDetails","http://"+MainActivity.ip+"/timepass.php");

    }


    private class ProductAsyncTask extends AsyncTask<String, Void, List> {
        ProgressDialog progressDialog = new ProgressDialog(ProductDetails.this);

        @Override
        protected List doInBackground(String... urls) {
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

            return extractProductJSONResponse(jsonResponse);
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
        protected void onPostExecute(final List list) {
            progressDialog.dismiss();

            ProductAdapter adapter = new ProductAdapter(ProductDetails.this, list);

            final ListView listView = (ListView) findViewById(R.id.list);

            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ProductDetails.this,ShowDetails.class);
                    intent.putExtra("name",((ProductObject)parent.getItemAtPosition(position)).getName());
                    startActivity(intent);
                }
            });
        }
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

    private List extractProductJSONResponse(String jsonResponse)
    {
        List<ProductObject> list = new ArrayList<>();

        try {
            JSONObject root=new JSONObject(jsonResponse);
            JSONArray result=root.optJSONArray("result");
            if(result.length() == 0 || result == null)
                return list;
            for(int i=0;i<result.length();i++)
            {
                /*JSONObject object=result.getJSONObject(i);
                String col=object.getString("col");
                if(col!=null && !col.equals("null")) {
                    subjects.add(col);
                }*/
                JSONObject object = result.getJSONObject(i);
                String name = object.getString("name");
                String iname = object.getString("iname");
                String folder = object.getString("folder");
                list.add(new ProductObject(name,iname,folder));
                Log.v("ProductDetails",name+" "+iname);
            }
        } catch (JSONException e) {
            Log.v("JSONParser","exception",e);
        }
        return list;
    }
}
