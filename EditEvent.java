package ootb.com.whenhubbe;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class EditEvent extends AppCompatActivity {

    int responseCode;
    EventObject eventObject = new EventObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventObject.setEventID(getIntent().getStringExtra("id"));
        eventObject.setPeriod("day");
        eventObject.setStartDate(getIntent().getStringExtra("startDate"));
        eventObject.setEndDate(getIntent().getStringExtra("endDate"));
        eventObject.setName(getIntent().getStringExtra("name"));
        //eventObject.setDescription(getIntent().getStringExtra("description"));
        eventObject.setScheduleID(getIntent().getStringExtra("scheduleId"));
        eventObject.setEventCity(getIntent().getStringExtra("city"));
        eventObject.setEventRegion(getIntent().getStringExtra("region"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventObject.setStartTimezone("America/New_York");
                eventObject.setEndTimezone("America/New_York");
                eventObject.setPeriod("day");
                //Get the values
                final EditText eventName = (EditText) findViewById(R.id.name);
                eventObject.setName(eventName.getText().toString());

                EditText eventCity = (EditText) findViewById(R.id.city);
                eventObject.setEventCity(eventCity.getText().toString());

                final Spinner eventRegion = (Spinner) findViewById(R.id.region);
                eventObject.setEventRegion((String) eventRegion.getSelectedItem());
                //Save the event
                doCall();
            }
        });

        //Update the page

        EditText eventName = (EditText) findViewById(R.id.name);
        eventName.setText(eventObject.getName());

        //Load Spinner
        Spinner state_spinner = (Spinner) findViewById(R.id.region);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.state_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state_spinner.setAdapter(adapter);

        //Find the index of selected value
        int state_pos = adapter.getPosition(eventObject.getEventRegion());
        state_spinner.setAdapter(adapter);
        state_spinner.setSelection(state_pos);

        EditText eventCity = (EditText) findViewById(R.id.city);
        eventCity.setText(eventObject.getEventCity());

        //EditText eventDescription = (EditText) findViewById(R.id.description);
        //eventDescription.setText(eventObject.getDescription());

        TextView startDate = (TextView) findViewById(R.id.startDate);
        startDate.setText(eventObject.getStartDate());
        String[] currentStartDate = eventObject.getStartDate().split("/");
        final int currentStartMonth = Integer.parseInt(currentStartDate[0]);
        final int currentStartDay = Integer.parseInt(currentStartDate[1]);
        final int currentStartYear = Integer.parseInt(currentStartDate[2]);

        TextView endDate = (TextView) findViewById(R.id.endDate);
        endDate.setText(eventObject.getEndDate());
        String[] currentEndDate = eventObject.getEndDate().split("/");
        final int currentEndMonth = Integer.parseInt(currentEndDate[0]);
        final int currentEndDay = Integer.parseInt(currentEndDate[1]);
        final int currentEndYear = Integer.parseInt(currentEndDate[2]);

        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog startDateDialog = new DatePickerDialog(EditEvent.this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        //Get the date from the text view
                        TextView startDate = (TextView) findViewById(R.id.startDate);
                        int realMonth = monthOfYear+1;
                        startDate.setText(monthOfYear+"/"+dayOfMonth+"/"+year);
                        //Change end date so we don't have to scroll to the correct month
                        TextView endDate = (TextView) findViewById(R.id.endDate);
                        endDate.setText(monthOfYear+"/"+dayOfMonth+"/"+year);
                        eventObject.setStartDate(monthOfYear+"/"+dayOfMonth+"/"+year);
                    }
                }, currentStartYear, currentStartMonth, currentStartDay);
                startDateDialog.setTitle("Start Date");
                startDateDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog endDateDialog = new DatePickerDialog(EditEvent.this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        //Get the date from the text view
                        int realMonth = monthOfYear+1;
                        TextView endDate = (TextView) findViewById(R.id.endDate);
                        endDate.setText(monthOfYear+"/"+dayOfMonth+"/"+year);
                        eventObject.setEndDate(monthOfYear+"/"+dayOfMonth+"/"+year);
                    }
                }, currentEndYear, currentEndMonth, currentEndDay);
                endDateDialog.setTitle("End Date");
                endDateDialog.show();
            }
        });

    }

    public void doCall(){
        //Format the date
        String[] newStartDate = eventObject.getStartDate().split("/");
        eventObject.setStartDate(newStartDate[2]+"-"+newStartDate[0]+"-"+newStartDate[1]);

        String[] newEndDate = eventObject.getEndDate().split("/");
        eventObject.setEndDate(newEndDate[2]+"-"+newEndDate[0]+"-"+newEndDate[1]);

        EditScheduleTask EditScheduleTask = new EditScheduleTask();
        EditScheduleTask.execute();
    }

    protected class EditScheduleTask extends AsyncTask<String, Integer, String> {
        final ProgressDialog pleaseWait = ProgressDialog.show(EditEvent.this, "Event", "Updating Tournament", true, true);
        int resultCount = 0;
        @Override
        protected String doInBackground(String... params) {
            //Build the JSON request
            JSONObject newEventJSON = new JSONObject();
            JSONObject whenJSON = new JSONObject();
            JSONObject locationJSON = new JSONObject();
//Start When Object
            try {
                whenJSON.put("period", eventObject.getPeriod());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                whenJSON.put("relative", null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                whenJSON.put("startDate", eventObject.getStartDate());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                whenJSON.put("startTimezone", eventObject.getStartTimezone());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                whenJSON.put("endDate", eventObject.getEndDate());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                whenJSON.put("endTimezone", eventObject.getEndTimezone());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                whenJSON.put("recurrenceRule", null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                whenJSON.put("recurrenceException", null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //End When Object
            try {
                newEventJSON.put("when", whenJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                locationJSON.put("city", eventObject.getEventCity());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                locationJSON.put("region", eventObject.getEventRegion());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                newEventJSON.put("location", locationJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                newEventJSON.put("name", eventObject.getName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                newEventJSON.put("description", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                newEventJSON.put("id", eventObject.getEventID());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                newEventJSON.put("scheduleId", eventObject.getScheduleID());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            URL url;
            try {
                String realURL = definedValues.WHEN_HUB_UPDATE_EVENT_URL.replace("[id]",eventObject.getScheduleID());
                realURL = realURL.replace("[event_id]",eventObject.getEventID());

                //url = new URL(definedValues.WHEN_HUB_UPDATE_EVENT_URL.replace("[id]",eventObject.getScheduleID()));
                url = new URL(realURL);
                HttpURLConnection conn;
                try {
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
                    conn.addRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    //conn.connect();
                    //DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.write(newEventJSON.toString());
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
                Intent i = new Intent(getBaseContext(),EventActivity.class);
                i.putExtra("calledFrom","U");
                i.putExtra("id", eventObject.getScheduleID());
                startActivity(i);
                finish();
            }else {
                Toast.makeText(getBaseContext(), "Unable to update tournament - " + responseCode + ".", Toast.LENGTH_LONG).show();
            }
        }

    }

}
