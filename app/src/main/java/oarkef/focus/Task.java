package oarkef.focus;

import android.content.Context;

import java.sql.Timestamp;
import java.util.Calendar;

import oarkef.focus.util.IO;

/**
 * Created by Bourgond Aries on 3/28/2015.
 */
public class Task
{
    public String description;
    public Calendar finish_time;

    IO io = new IO();

    public void loadEarliestEvent(Context context)
    {
        String deadline =  io.loadDeadlineFromFile(context);
        Timestamp ts = Timestamp.valueOf(deadline);
        finish_time.setTimeInMillis(ts.getTime());

        description = "Do this task pls";
    }

    public String getDescription()
    {
        return description;
    }

    public Calendar getFinishTime()
    {
        return finish_time;
    }

}

