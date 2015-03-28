package oarkef.focus.util;

import android.content.Context;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by OleAndreas on 28.03.2015.
 */
public class IO
{

    private final String filename = "gerpherp.txt";
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
}


