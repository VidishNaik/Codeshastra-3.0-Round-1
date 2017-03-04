package com.example.vidish.round1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;


public class MainActivity extends AppCompatActivity {

    EditText id,password;
    Button scan,login;
    String scanContent;
    LoginAsyncTask loginAsyncTask;
    String[] tags;
    /*
        tag 1 = aadhar number
        tag 3 = name
        tag 5 = gender
        tag 7 = year of birth
        tag len-2 = pincode
         */
    public final static String ip = "192.168.43.75";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        id = (EditText) findViewById(R.id.edit_text_aadhar_id);
        password = (EditText) findViewById(R.id.edit_text_password);
        login = (Button) findViewById(R.id.aadhar_login);
        scan = (Button) findViewById(R.id.button_aadhar_scan_barcode);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan the barcode on your ID");
                integrator.initiateScan();
            }
        });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    View view = MainActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        if (id.getText().toString().length()!=0 && password.getText().toString().length() != 0) {
                            loginAsyncTask = new LoginAsyncTask();
                            loginAsyncTask.execute("http://"+MainActivity.ip+"/aadharlogin.php?id="+scanContent);
                        }
                        else
                        {
                            if(id.getText().toString().length()==0)
                                Toast.makeText(MainActivity.this, "Please enter your ID", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(MainActivity.this, "Please enter your Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAsyncTask = new LoginAsyncTask();
                loginAsyncTask.execute("http://"+MainActivity.ip+"/aadharlogin.php?id="+scanContent);
                Log.v("MainActivity","http://"+MainActivity.ip+"/aadharlogin.php?id="+scanContent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        String[] xmlContent = scanningResult.getContents().split(">");
        tags = xmlContent[1].split("\"");
        scanContent = tags[1];
        id.setText(tags[1]);
    }


    private class LoginAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected String doInBackground(String... urls) {
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
            return jsonResponse;
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
        protected void onPostExecute(String jsonResponse) {
            progressDialog.dismiss();
            if(jsonResponse.equals(password.getText().toString()))
            {
                Intent intent = new Intent(MainActivity.this,ProductDetails.class)
                        .putExtra("name",tags[3]);
                startActivity(intent);
            }

            else
                Toast.makeText(MainActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
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
}
