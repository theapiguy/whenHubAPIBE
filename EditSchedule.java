package ootb.com.whenhubbe;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class EditSchedule extends AppCompatActivity {

    String scheduleID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        scheduleID = getIntent().getStringExtra("scheduleID");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save Changes
                //Get the values
                final EditText scheduleName = (EditText) findViewById(R.id.name);
                String  name = scheduleName.getText().toString();

                final EditText scheduleDescription = (EditText) findViewById(R.id.description);
                String description = scheduleDescription.getText().toString();
            }
        });
    }

}
