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
        String task =  io.loadNextDeadlineFromFile(context);
        int index = task.indexOf(io.getSplitChar());
        String time_part = task.substring(0, index);
        finish_time.setTimeInMillis(Timestamp.valueOf(time_part).getTime());

        description = task.substring(index + 2);
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

