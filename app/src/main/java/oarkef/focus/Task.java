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
    public String description;
    public Calendar finish_time;

    public String toString() {
        String str = finish_time.get(Calendar.YEAR)
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
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        finish_time = Calendar.getInstance();
        finish_time.setTime((Date) formatter.parse(parts[0]));
        description = parts[1];
        return true;
    }
}

