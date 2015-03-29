package oarkef.focus.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;

/**
 * Created by OleAndreas on 28.03.2015.
 */
public class IO {

    private static String main = "file1";
    private static String temp = "file2";
    private String current_file = main;
    private String other_file = temp;
    private final char split_char = ';' ;

    public static String getMainFileName() {
        return main;
    }

    public static String getTemporaryFilename() {
        return temp;
    }

    public void saveTask(Context context, String task) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput(current_file, Context.MODE_APPEND));
        writer.write(task + "\n");
        writer.close();
    }

    public String loadNextDeadlineFromFile(Context context) throws IOException {

        String task = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(current_file)));
        String input_line;
        Timestamp read_timestamp = null;
        Timestamp closest = null;
        while ((input_line = reader.readLine()) != null ) {
            if (input_line.equals(""))
                continue;
            int index = input_line.indexOf(split_char);
            String time_part = input_line.substring(0, index);
            read_timestamp = Timestamp.valueOf(time_part);

            if (closest == null || read_timestamp.before(closest)) {
                closest = read_timestamp;
                task = input_line;
            }
        }
        reader.close();
        return task;
    }

    public void deleteSpecificEntry(Context context, String entry) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(current_file)));
        OutputStreamWriter output_stream = new OutputStreamWriter(context.openFileOutput(other_file, Context.MODE_PRIVATE));

        String input_line;

        while ((input_line = reader.readLine()) != null ) {
            entry = entry.trim();
            if (!input_line.equals(entry) && !input_line.equals("")) {
                output_stream.write(input_line + "\n");
            }
        }
        output_stream.close();

        reader.close();

        changeCurrentFile();
        copyFromTempToMain(context);
        changeCurrentFile();
    }

    public void deleteOldEntries(Context context) throws IOException {

        Timestamp current_timestamp = new Timestamp(System.currentTimeMillis());
        Timestamp read_timestamp;

        BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(current_file)));
        OutputStreamWriter output_stream = new OutputStreamWriter(context.openFileOutput(other_file, Context.MODE_PRIVATE));

        String oneLine;

        while ((oneLine = reader.readLine()) != null ) {
            int index = oneLine.indexOf(split_char);
            String time_part = oneLine.substring(0, index);
            read_timestamp = Timestamp.valueOf(time_part);
            if (read_timestamp.after(current_timestamp)) {
                output_stream.write(oneLine + "\n");
            }
        }
        output_stream.close();

        reader.close();

        changeCurrentFile();
        copyFromTempToMain(context);
        changeCurrentFile();
    }

    private void changeCurrentFile() {

        if (current_file == main) {
            current_file = temp;
            other_file = main;
        }
        else {
            current_file = main;
            other_file = temp;
        }
    }

    private void copyFromTempToMain(Context context) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(current_file)));
        OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput(other_file, Context.MODE_PRIVATE));

        String input_line;
        while ((input_line = reader.readLine()) != null ) {
            writer.write(input_line + "\n");
        }

        writer.close();
        reader.close();
    }

    public char getSplitChar() {
        return split_char;
    }

}


