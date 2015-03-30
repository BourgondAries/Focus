package oarkef.focus.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    private static String id_store = "id_file";
    private static int id = 0;
    private String current_file = main;
    private String other_file = temp;
    private final char split_char = ';' ;

    public static String getMainFileName() {
        return main;
    }

    public static String getIdStoreName() {
        return id_store;
    }

    public static int getId(Context context) throws FileNotFoundException, IOException {
        FileInputStream file = null;
        try {
            file = context.openFileInput(id_store);
        } catch (FileNotFoundException exc) {
            context.openFileOutput(id_store, Context.MODE_PRIVATE).close();
            file = context.openFileInput(id_store);
        }
        byte[] number_buffer = new byte[11]; // An int's largest encoding for 32 bit is 11 (including minus sign)
        int read = file.read(number_buffer);
        file.close();
        if (read == 0) {
            return 0;
        } else {
            return id = Integer.parseInt(new String(number_buffer, "UTF-8"));
        }
    }

    public static int incrementId(Context context) throws FileNotFoundException, IOException {
        FileOutputStream output = context.openFileOutput(id_store, Context.MODE_PRIVATE);
        ++id;
        if (id == Integer.MAX_VALUE)
            id = 0;
        output.write(String.format("%011d", id).getBytes());
        output.close();
        return id;
    }

    public void resetFiles(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(current_file, Context.MODE_PRIVATE);
            FileOutputStream fos2 = context.openFileOutput(other_file, Context.MODE_PRIVATE);

            fos.close();
            fos2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            String[] decoded = input_line.split("" + split_char);
            String time_part = decoded[1];
            read_timestamp = Timestamp.valueOf(time_part);

            if (closest == null || read_timestamp.before(closest)) {
                closest = read_timestamp;
                task = input_line;
            }
        }
        reader.close();
        return task;
    }

    public void deleteSpecificEntry(Context context, int local_id) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(current_file)));
        OutputStreamWriter output_stream = new OutputStreamWriter(context.openFileOutput(other_file, Context.MODE_PRIVATE));

        String input_line;

        while ((input_line = reader.readLine()) != null ) {
            if (!input_line.equals("") && Integer.valueOf(input_line.split("" + split_char)[0]) != local_id) {
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

    public String readFile(Context context, String file) throws IOException {

        String result = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(context.openFileInput(file)));

        String one_line;
        while ((one_line = br.readLine()) != null ) {
            result = result + "\n" + one_line;
        }

        br.close();

        return result;
    }
}


