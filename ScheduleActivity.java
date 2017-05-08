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

public class ScheduleActivity extends AppCompatActivity {

    //public ListView scheduleList;
    ArrayList<HashMap<String,Object>> scheduleResultsList = new ArrayList<>();
    String messageText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
                Intent i = new Intent(getBaseContext(), AddSchedule.class);
                startActivity(i);
            }
        });

        String calledFrom = getIntent().getStringExtra("calledFrom");
        messageText = "Getting";
        if (calledFrom.equals("U")){
            messageText = "Updating";
        }

        //Get the schedule information
        GetScheduleTask GetScheduleTask = new GetScheduleTask();
        GetScheduleTask.execute(calledFrom);

    }

    protected class GetScheduleTask extends AsyncTask<String, Integer, String> {
        //final ProgressDialog pleaseWait = ProgressDialog.show(ScheduleActivity.this, "Schedule", messageText +" Schedule", true, true);
        final ProgressDialog pleaseWait = ProgressDialog.show(ScheduleActivity.this, "Schedule", "Getting Schedule", true, true);
        int resultCount = 0;
        @Override
        protected String doInBackground(String... params) {
            scheduleResultsList.clear();
            StringBuilder urlBuilder = new StringBuilder(definedValues.WHEN_HUB_MY_SCHEDULE_URL);

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
                        //Products is an array
                        reader.beginArray();
                        while (reader.hasNext()) {
                            reader.beginObject();
                            HashMap<String, Object> map = new HashMap<>();
                            while (reader.hasNext()) {
                                String tempName = reader.nextName();
                                if (tempName.equals("name")) {
                                    String logText = reader.nextString();
                                    map.put("name", Html.fromHtml(logText));
                                } else {
                                    if (tempName.equals("description")) {
                                        map.put("description", String.valueOf(reader.nextString()));
                                    } else {
                                        if (tempName.equals("id")) {
                                            map.put("id", String.valueOf(reader.nextString()));
                                        } else {
                                            reader.skipValue();
                                        }
                                    }
                                }
                            }
                            reader.endObject();
                            if (!map.isEmpty()) {
                                scheduleResultsList.add(map);
                                resultCount++;
                            }
                        }
                        reader.endArray();
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
                        ListAdapter adapter = new SimpleAdapter(ScheduleActivity.this, scheduleResultsList, R.layout.schedule_results_row, new String[]{"name", "description", "id"}, new int[]{R.id.name, R.id.description, R.id.id,});
                        ListView scheduleList = (ListView) findViewById(R.id.scheduleList);
                        scheduleList.setAdapter(adapter);
                        scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                HashMap<String,Object> stuff =scheduleResultsList.get(position);
                                String scheduleid = (String) stuff.get("id");
                                Intent i = new Intent(getBaseContext(), EventActivity.class);
                                i.putExtra("id", scheduleid);
                                i.putExtra("calledFrom","SA");
                                startActivity(i);
                                //Toast.makeText(getBaseContext(), "You clicked schedule " + scheduleid, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }else {
                //Show error message
                Intent i = new Intent(getBaseContext(), AddSchedule.class);
                startActivity(i);
                //Toast.makeText(getBaseContext(), "No schedules found.", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }
}
