package ootb.com.whenhubbe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.JsonReader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class EventActivity extends AppCompatActivity {

    ArrayList<HashMap<String,Object>> eventResultsList = new ArrayList<>();
    String messageText;
    String scheduleID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        scheduleID = getIntent().getStringExtra("id");
        String calledFrom = getIntent().getStringExtra("calledFrom");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                Intent i = new Intent(getBaseContext(), AddEvent.class);
                i.putExtra("scheduleID", scheduleID);
                startActivity(i);
            }
        });

        messageText = "Getting";
        if (calledFrom.equals("U")){
            messageText = "Updating";
        }

        //Get the schedule information
        GetEventTask GetEventTask = new GetEventTask();
        GetEventTask.execute(scheduleID);
    }

    protected class GetEventTask extends AsyncTask<String, Integer, String> {
        final ProgressDialog pleaseWait = ProgressDialog.show(EventActivity.this, "Schedule Games", messageText +" Games", true, true);
        int resultCount = 0;
        @Override
        protected String doInBackground(String... params) {
            eventResultsList.clear();
            StringBuilder urlBuilder = new StringBuilder(definedValues.WHEN_HUB_SCHEDULE_EVENT_URL+params[0]+"/?filter[include][events]=media&filter[include]=media");

            URL url;
            try {
                url = new URL(urlBuilder.toString());
                HttpURLConnection conn;
                try {
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();
                    InputStream in = conn.getInputStream();
                    try {
                        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String tempName = reader.nextName();
                            if(tempName.equals("events")){
                            //Events is an array
                            reader.beginArray();
                            while (reader.hasNext()) {
                                reader.beginObject();
                                HashMap<String, Object> map = new HashMap<>();
                                while (reader.hasNext()) {
                                    tempName = reader.nextName();
                                    if (tempName.equals("when")) {
                                        //When is an object
                                        reader.beginObject();
                                        while (reader.hasNext()) {
                                            tempName = reader.nextName();
                                            if (tempName.equals("startDate")) {
                                                //Format the date
                                                String inStartDate = Html.fromHtml(reader.nextString()).toString();
                                                String[] startDateArray = inStartDate.split("-");
                                                map.put("startDate", startDateArray[1]+"/"+startDateArray[2]+"/"+startDateArray[0]);
                                            }else{
                                                if (tempName.equals("endDate")){
                                                    String inEndDate = Html.fromHtml(reader.nextString()).toString();
                                                    String[] endDateArray = inEndDate.split("-");
                                                    map.put("endDate", endDateArray[1]+"/"+endDateArray[2]+"/"+endDateArray[0]);
                                                }else{
                                                    if (tempName.equals("period")){
                                                        String kdr = reader.nextString();
                                                        map.put("periodOfTime", Html.fromHtml(kdr));
                                                        //map.put("period", Html.fromHtml(reader.nextString()));
                                                    }else {
                                                        reader.skipValue();
                                                    }
                                                }
                                            }
                                        }
                                        reader.endObject();
                                     } else {
                                        if (tempName.equals("name")) {
                                            map.put("name", String.valueOf(reader.nextString()));
                                        } else {
                                            if (tempName.equals("id")) {
                                                map.put("id", String.valueOf(reader.nextString()));
                                            } else {
                                                if (tempName.equals("region")){
                                                    map.put("region", String.valueOf(reader.nextString()));
                                                }else {
                                                    if (tempName.equals("scheduleId")){
                                                        map.put("scheduleId", String.valueOf(reader.nextString()));
                                                    }else {
                                                        if (tempName.equals("location")){
                                                            //location is an object
                                                            reader.beginObject();
                                                            while (reader.hasNext()){
                                                                tempName = reader.nextName();
                                                                if (tempName.equals("city")){
                                                                    String cityName = reader.nextString();
                                                                    //Toast.makeText(getBaseContext(), "City "+cityName, Toast.LENGTH_LONG).show();
                                                                    map.put("city", cityName);
                                                                }else {
                                                                    if(tempName.equals("region")){
                                                                        map.put("region", reader.nextString());

                                                                        //map.put("region", "region");
                                                                    }else {
                                                                        reader.skipValue();
                                                                    }
                                                                }
                                                            }
                                                            reader.endObject();
                                                        }else {
                                                            reader.skipValue();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                reader.endObject();
                                if (!map.isEmpty()) {
                                    eventResultsList.add(map);
                                    resultCount++;
                                }
                            }
                            reader.endArray();
                            }else{
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
            if(resultCount>0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListAdapter adapter = new SimpleAdapter(EventActivity.this, eventResultsList, R.layout.event_results_row, new String[]{"startDate", "endDate", "city", "region", "name", "id", "periodOfTime", "scheduleId"}, new int[]{R.id.startDate, R.id.endDate, R.id.city, R.id.region, R.id.name, R.id.id, R.id.period, R.id.scheduleId});
                        ListView eventList = (ListView) findViewById(R.id.eventList);
                        eventList.setAdapter(adapter);
                        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                HashMap<String,Object> stuff = eventResultsList.get(position);
                                String eventid = (String) stuff.get("id");
                                String startDate = (String) stuff.get("startDate");
                                String endDate = (String) stuff.get("endDate");
                                String name = (String) stuff.get("name");
                                String scheduleID = (String) stuff.get("scheduleId");
                                String city = (String) stuff.get("city");
                                String region = (String) stuff.get("region");

                                Intent i = new Intent(getBaseContext(), EditEvent.class);
                                i.putExtra("id", eventid);
                                i.putExtra("startDate", startDate);
                                i.putExtra("endDate", endDate);
                                i.putExtra("name", name);
                                i.putExtra("scheduleId", scheduleID);
                                i.putExtra("city", city);
                                i.putExtra("region", region);
                                startActivity(i);
                                finish();
                                //Toast.makeText(getBaseContext(), "You clicked event " + eventid, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }else {
                //Show error message
                Intent i = new Intent(getBaseContext(), AddEvent.class);
                i.putExtra("scheduleID", scheduleID);
                startActivity(i);
                //Toast.makeText(getBaseContext(), "No games found.", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }

}
