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
public class Task {
    private int task_id;
    public String description;
    public Calendar finish_time;

    public Task(int id) {
        task_id = id;
    }

    public int getId() {
        return task_id;
    }

    public String toString() {
        if (finish_time == null)
            return task_id + ";null;" + description + "\n";
        String str = task_id
            + ";" + finish_time.get(Calendar.YEAR)
            +
            "-" +
            String.format(
                    "%02d-%02d %02d:%02d:%02d",
                    finish_time.get(Calendar.MONTH) + 1,
                    finish_time.get(Calendar.DAY_OF_MONTH),
                    finish_time.get(Calendar.HOUR_OF_DAY),
                    finish_time.get(Calendar.MINUTE),
                    finish_time.get(Calendar.SECOND)
            ) + ";" + description + "\n";
        return str;
    }

    public boolean fromString(String input) throws ParseException {
        if (input.equals(""))
            return false;
        String[] parts = input.split(";");
        if (parts.length != 3)
            throw new ParseException("Unable to correctly split according to ;", 0);
        description = parts[2];
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        finish_time = Calendar.getInstance();
        finish_time.setTime((Date) formatter.parse(parts[1]));
        task_id = Integer.valueOf(parts[0]);
        return true;
    }
}

