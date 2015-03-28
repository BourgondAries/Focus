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
public class IO
{

    private String filename_0 = "file1";
    private String filename_1 = "file2";
    private String  current_file = filename_0;
    private String other_file = filename_1;
    private final char split_char = ';' ;
    private final int READ_BLOCK_SIZE = 100;

    public void saveDeadline(Context context, String deadline)
    {
        try
        {
            //deleteFile(filename);
            FileOutputStream fos = context.openFileOutput(current_file, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(deadline);
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadNextDeadlineFromFile(Context context)
    {
        String task = "";
        int index;
        Timestamp curr_ts = new Timestamp(System.currentTimeMillis());
        Timestamp read_ts = null;
        Timestamp closest = Timestamp.valueOf("2099-01-01 00:00:00");
        String time_part;

        try
        {
            FileInputStream fis = context.openFileInput(current_file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String oneLine;

            while ((oneLine = br.readLine()) != null ) {

                index = oneLine.indexOf(split_char);
                time_part = oneLine.substring(0, index);
                read_ts = Timestamp.valueOf(time_part);


                if (read_ts.before(curr_ts)) {
                    closest = read_ts;
                    task = oneLine;

                }
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return task;
    }

    public void deleteOldEntries(Context context)
    {
        Timestamp curr_ts = new Timestamp(System.currentTimeMillis());
        Timestamp read_ts;
        int index;
        String time_part;

        try {
            FileInputStream fis = context.openFileInput(current_file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            FileOutputStream fos = context.openFileOutput(other_file, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            String oneLine;

            while ((oneLine = br.readLine()) != null ) {
                index = oneLine.indexOf(split_char);
                time_part = oneLine.substring(0, index);
                read_ts = Timestamp.valueOf(time_part);
                if (read_ts.after(curr_ts)) {
                    osw.write(oneLine + "\n");
                }
            }
            osw.flush();
            osw.close();
            fos.close();

            br.close();
            isr.close();
            fis.close();
            fis.close();

            changeCurrentFile();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void changeCurrentFile() {

        if (current_file == filename_0) {
            current_file = filename_1;
            other_file = filename_0;
        }
        else {
            current_file = filename_0;
            other_file = filename_1;
        }
    }

    public char getSplitChar(){
        return split_char;
    }

}


