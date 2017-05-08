package ootb.com.whenhubbe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public static final String USER_INFO = "userInfo";
    public static final String GIVEN_NAME = "given_name";

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        //Get Schedule Info
        Intent i = new Intent(this, ScheduleActivity.class);
        i.putExtra("calledFrom", "M");
        finish();
        startActivity(i);
    }

    public SharedPreferences getPreferences(Context context) {
        return getSharedPreferences(USER_INFO,
                context.MODE_PRIVATE);
    }

    public String getGivenName(Context context) {
        final SharedPreferences prefs = getPreferences(context);
        String givenName = prefs.getString(GIVEN_NAME, "");
        if (givenName.isEmpty()) {
            return "";
        }
        return givenName;
    }

}
