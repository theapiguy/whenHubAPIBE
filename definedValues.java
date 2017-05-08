package ootb.com.whenhubbe;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Keith on 4/20/2017.
 */

public class definedValues extends Application {

    public static final String WHEN_HUB_ACCESS_TOKEN = "X9F4bu8nftYBo4z0QpAxKkk7cc9XeQHC6q9fUIRoLCLbP7qa5NKXfaYchzeruCGw";
    public static final String WHEN_HUB_USER_ID = "58f5c818e0a8d33704f7b17e";
    public static final String WHEN_HUB_SCHEDULE_ID = "58ffefb1c5cc3e2f1c6d7086";

    public static final String WHEN_HUB_USER_INFO_URL = "https://api.whenhub.com/api/users/me?access_token="+WHEN_HUB_ACCESS_TOKEN;
    public static final String WHEN_HUB_MY_SCHEDULE_URL = "https://api.whenhub.com/api/users/me/schedules?access_token="+WHEN_HUB_ACCESS_TOKEN;
    public static final String WHEN_HUB_SCHEDULE_EVENT_URL = "https://api.whenhub.com/api/schedules/";
    public static final String WHEN_HUB_ADD_SCHEDULE_URL = "https://api.whenhub.com/api/users/"+ WHEN_HUB_USER_ID +"/schedules?access_token=" + WHEN_HUB_ACCESS_TOKEN;
    public static final String WHEN_HUB_ADD_EVENT_URL = "https://api.whenhub.com/api/schedules/[id]/events?access_token=" + WHEN_HUB_ACCESS_TOKEN;
    public static final String WHEN_HUB_UPDATE_EVENT_URL = "https://api.whenhub.com/api/schedules/[id]/events/[event_id]?access_token=" + WHEN_HUB_ACCESS_TOKEN;

    public static final String USER_INFO = "userInfo";
    public static final String GIVEN_NAME = "given_name";
    public static final String DISPLAY_NAME = "display_name";

    EventObject eventObject = new EventObject();

}

