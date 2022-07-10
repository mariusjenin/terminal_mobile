package terminal.mobilite.manager;

import android.content.Context;
import terminal.mobilite.TerminalApp;
import terminal.mobilite.activity.R;

import java.text.SimpleDateFormat;

/**
 * Manager de dates
 */
public class DateManager {

    private static DateManager INSTANCE;

    private DateManager() {
    }

    public static DateManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DateManager();
        }
        return INSTANCE;
    }

    public String format(int time){
        SimpleDateFormat formatter;
        Context c = TerminalApp.getContext();
        if(time < 3000){
            return TerminalApp.getContext().getString(R.string.now);
        } else if(time > 3600000){
            formatter = new SimpleDateFormat(c.getString(R.string.time_format));
        } else if(time%3600000==0){
            formatter = new SimpleDateFormat(c.getString(R.string.time_format_hour));
        } else {
            formatter = new SimpleDateFormat(c.getString(R.string.time_format_min));
        }
        return formatter.format(time);
    }
}
