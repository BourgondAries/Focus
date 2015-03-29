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

    private String main = "file1";
    private String temp = "file2";
    private String  current_file = main;
    private String other_file = temp;
    private final char split_char = ';' ;

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

    public void saveTask(Context context, String task)
    {
        try
        {
            FileOutputStream fos = context.openFileOutput(current_file, Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(task + "\n");
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadNextDeadlineFromFile(Context context)
    {
        String task = "";
        int index;
        //Timestamp curr_ts = new Timestamp(System.currentTimeMillis());
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
                System.out.println("Current line read: " + oneLine);
                if (oneLine.equals(""))
                    break;
                index = oneLine.indexOf(split_char);
                time_part = oneLine.substring(0, index);
                read_ts = Timestamp.valueOf(time_part);

                if (read_ts.before(closest)) {
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
    public void deleteSpecificEntry(Context context, String entry) {
        try {
            FileInputStream fis = context.openFileInput(current_file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            FileOutputStream fos = context.openFileOutput(other_file, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            String oneLine;

            while ((oneLine = br.readLine()) != null ) {
                System.out.print("deleteSpecific:" + entry + "|" + oneLine + ":");
                entry = entry.trim();
                if (!oneLine.equals(entry)) {
                    System.out.println("Actually copying");
                    osw.write(oneLine + "\n");
                }
            }
            osw.close();
            fos.close();

            br.close();
            isr.close();
            fis.close();

            changeCurrentFile();
            copyFromTempToMain(context);
            changeCurrentFile();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            osw.close();
            fos.close();

            br.close();
            isr.close();
            fis.close();

            changeCurrentFile();
            //Copy file
            copyFromTempToMain(context);
            changeCurrentFile();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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

    private void copyFromTempToMain(Context context) {
        try {
            FileInputStream fis = context.openFileInput(current_file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            FileOutputStream fos = context.openFileOutput(other_file, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            String oneLine;
            while ((oneLine = br.readLine()) != null ) {
                osw.write(oneLine + "\n");
            }

            osw.close();
            fos.close();

            br.close();
            isr.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public char getSplitChar(){
        return split_char;
    }

    public String printFile(Context context, String file) {

        String result = "";

        try {
            FileInputStream fis = context.openFileInput(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String oneLine;
            while ((oneLine = br.readLine()) != null ) {
                result = result + "\n" + oneLine;
            }

            br.close();
            isr.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}


