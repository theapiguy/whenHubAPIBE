package ootb.com.whenhubbe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.JsonReader;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class AddSchedule extends AppCompatActivity {

    int responseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();
                //Get the values
                final EditText scheduleName = (EditText) findViewById(R.id.name);
                String  name = scheduleName.getText().toString();

                final EditText scheduleDescription = (EditText) findViewById(R.id.description);
                String description = scheduleDescription.getText().toString();

                AddScheduleTask AddScheduleTask = new AddScheduleTask();
                AddScheduleTask.execute(name, description);
            }
        });
        //Display name and description field

    }

    protected class AddScheduleTask extends AsyncTask<String, Integer, String> {
        final ProgressDialog pleaseWait = ProgressDialog.show(AddSchedule.this, "Schedule", "Adding Schedule", true, true);
        int resultCount = 0;
        @Override
        protected String doInBackground(String... params) {
            StringBuilder urlBuilder = new StringBuilder(definedValues.WHEN_HUB_ADD_SCHEDULE_URL);

            //Build the JSON request
            JSONObject newScheduleJSON = new JSONObject();
            try {
                newScheduleJSON.put("name", params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                newScheduleJSON.put("description", params[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                newScheduleJSON.put("scope", "public");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            URL url;
            try {
                url = new URL(definedValues.WHEN_HUB_ADD_SCHEDULE_URL);
                HttpURLConnection conn;
                try {
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.addRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    //conn.connect();
                    //DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.write(newScheduleJSON.toString());
                    wr.flush();
                    responseCode = conn.getResponseCode();
                    Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String inLine = in.toString();
                    StringBuffer response = new StringBuffer();
                    //while ((inLine = in.readLine()) != null){
                        //response.append(inLine);
                    //}
                    in.close();
                    //String addOutput = conn.getOutputStream().toString();
                    //Log.i("output", response.toString());
                    //wr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

        }

        protected void onPostExecute(String result) {
            pleaseWait.dismiss();
            if (responseCode == 200){
                Intent i = new Intent(getBaseContext(),ScheduleActivity.class);
                i.putExtra("calledFrom","U");
                startActivity(i);
                finish();
            }else {
                Toast.makeText(getBaseContext(), "Unable to create schedule.", Toast.LENGTH_LONG).show();
            }
        }

    }
}
