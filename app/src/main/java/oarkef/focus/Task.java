package oarkef.focus;

import android.content.Context;
import android.util.Log;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import oarkef.focus.util.IO;

/**
 * Created by Bourgond Aries on 3/28/2015.
 */
public class Task
{
    public String description;
    public Calendar finish_time;

    IO io = new IO();

    public boolean loadEarliestEvent(Context context)
    {
        String task =  io.loadNextDeadlineFromFile(context);

        if (task.equalsIgnoreCase("")) {
            return false;
        } else {
            int index = task.indexOf(io.getSplitChar());

            String time_part = task.substring(0, index);
            finish_time.setTimeInMillis(Timestamp.valueOf(time_part).getTime());

            description = task.substring(index + 1);

            return true;
        }

    }

    public String getDescription()
    {
        return description;
    }

    public Calendar getFinishTime()
    {
        return finish_time;
    }

    public String toString()
    {
        String str = finish_time.get(Calendar.YEAR)
                +
                "-" +
                String.format(
                        "%02d-%02d %02d:%02d:%02d",
                        finish_time.get(Calendar.MONTH),
                        finish_time.get(Calendar.DAY_OF_MONTH),
                        finish_time.get(Calendar.HOUR_OF_DAY),
                        finish_time.get(Calendar.MINUTE),
                        finish_time.get(Calendar.SECOND)
                ) + ";" + description + "\n";
        Log.d("toString:" ,str);
        return str;
    }

    public void fromString(String input)
    {
        String[] parts = input.split(";");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            finish_time.setTime((Date) formatter.parse(parts[0]));
        } catch (ParseException exc) {
            System.out.println("parse exc");
        }
        description = parts[1];
    }
}

