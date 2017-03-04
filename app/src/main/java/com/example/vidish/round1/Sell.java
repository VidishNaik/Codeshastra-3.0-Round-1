package com.example.vidish.round1;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

public class Sell extends AppCompatActivity {
    EditText name,number,address,crop,weight;
    Button submit;
    String selectedItem;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RetreiveAsyncTask retreive = new RetreiveAsyncTask();
        retreive.execute("http://"+MainActivity.ip+"/retreivecrops.php");
        setContentView(R.layout.activity_sell);
        name= (EditText) findViewById(R.id.name);
        address= (EditText) findViewById(R.id.address);
        number= (EditText) findViewById(R.id.contactno);
        spinner = (Spinner) findViewById(R.id.spinner);
        weight= (EditText) findViewById(R.id.weight);
        submit= (Button) findViewById(R.id.submit);

    }

    public void onSubmit(View v)
    {
        String namevalue=name.getText().toString();
        String addressvalue=address.getText().toString();
        String contactvalue=number.getText().toString();
        String weightvalue=weight.getText().toString();
        if (!(namevalue.equals("") && addressvalue.equals("") && contactvalue.equals("") && selectedItem.equals("") && weightvalue.equals("")))
        {
            SellAsyncTask sellAsyncTask = new SellAsyncTask();
            String link = "http://"+MainActivity.ip+"/sell.php?name="+namevalue+"&address="+addressvalue+"&contact="+contactvalue+"&crop="+selectedItem+"&weight="+weightvalue;
            String[] linksplit = link.split(" ");
            link = "";
            for(int i=0;i<linksplit.length;i++)
            {
                if(i!=linksplit.length - 1)
                    link = link + linksplit[i] + "%20";
                else
                    link = link +linksplit[i];
            }
            Log.v("Sell",link);
        }
        else
            Toast.makeText(Sell.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();

    }


    private class SellAsyncTask extends AsyncTask<String, Void, List> {
        ProgressDialog progressDialog = new ProgressDialog(Sell.this);

        @Override
        protected List<String> doInBackground(String... urls) {
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
            Log.v("NewTech", jsonResponse);
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
        protected void onPostExecute(List list) {
            progressDialog.dismiss();
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
    }

    private class RetreiveAsyncTask extends AsyncTask<String, Void, List> {
        ProgressDialog progressDialog = new ProgressDialog(Sell.this);

        @Override
        protected List<String> doInBackground(String... urls) {
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
            Log.v("NewTech", jsonResponse);
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
        protected void onPostExecute(List list) {
            progressDialog.dismiss();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Sell.this, R.layout.spinner_layout, list);
            adapter.setDropDownViewResource(R.layout.spinner_layout);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(spinner.getAdapter() != null)
                        selectedItem = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
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

        private List<String> extractProductJSONResponse(String jsonResponse) {
            List<String> list = new ArrayList<>();

            try {
                JSONObject root = new JSONObject(jsonResponse);
                JSONArray result = root.optJSONArray("result");
                if (result.length() == 0 || result == null)
                    return null;
                for(int i = 0;i<result.length();i++) {
                    JSONObject object = result.getJSONObject(i);
                    String link = object.getString("name");
                    list.add(link);
                }

            } catch (JSONException e) {
                Log.v("JSONParser", "exception", e);
            }
            return list;
        }
    }
}
