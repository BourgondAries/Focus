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

    private final String filename = "gerpherp.txt";
    private String tempfile = "temp.txt";
    private final char split_char = ';' ;
    private final int READ_BLOCK_SIZE = 100;

    public void saveDeadline(Context context, String deadline)
    {
        try
        {
            //deleteFile(filename);
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(deadline);
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadDeadlineFromFile(Context context)
    {

        String string = "";

        try
        {
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            int charRead;

            while ((charRead = isr.read(inputBuffer,0,1)) > 0 )
            {
                //Log.d(PROJECT_TAG, String.valueOf(charRead));
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                string += readString;
                inputBuffer = new char[READ_BLOCK_SIZE];
            }

            isr.close();
            //Log.d(PROJECT_TAG, string);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return string;
    }
    public void deleteOldEntries(Context context)
    {
        Log.v("TAG", "Test");
        Timestamp curr_ts = new Timestamp(System.currentTimeMillis());
        Timestamp read_ts;
        int index;
        String time_part;

        try {
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            FileOutputStream fos = context.openFileOutput(tempfile, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            String oneLine;

            while ((oneLine = br.readLine()) != null ) {
                Log.d("TAG", oneLine);
                index = oneLine.indexOf(split_char);
                time_part = oneLine.substring(0, index + 1);
                read_ts = Timestamp.valueOf(time_part);
                if (read_ts.after(curr_ts)) {
                    osw.write(oneLine);
                }
            }
            osw.write("Last line");
            osw.flush();
            osw.close();
            //fos.close();

            br.close();
            // isr.close();
            //fis.close();
            //fis.close();

            // Need to make filename = tempfile:
            //File old_file = context.getFileStreamPath(filename);
            //File new_file = context.getFileStreamPath(tempfile);
            //old_file.renameTo(new_file);


        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}


