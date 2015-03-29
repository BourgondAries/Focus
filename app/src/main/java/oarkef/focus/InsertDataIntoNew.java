package oarkef.focus;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;


public class InsertDataIntoNew extends ActionBarActivity {

    private java.util.Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data_into_new);

        Intent intent = new Intent(this, DatePickingActivity.class);
        startActivityForResult(intent, Request.DATE.ordinal());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_insert_data_into_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static enum Request
    {
        DATE,
        TIME
    }

    public void pickDate(View view)
    {
        Intent intent = new Intent(this, DatePickingActivity.class);
        startActivityForResult(intent, Request.DATE.ordinal());
    }

    public void pickTime(View view)
    {
        Intent intent = new Intent(this, TimePickingActivity.class);
        startActivityForResult(intent, Request.TIME.ordinal());
    }

    public void save(View view)
    {
        Intent out = new Intent();
        out.putExtra("Date", calendar);
        out.putExtra("Description", ((EditText) findViewById(R.id.editText)).getText().toString());
        setResult(RESULT_OK, out);
        finish();
    }

    public void cancel(View view)
    {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data)
    {
        if (result_code == RESULT_CANCELED) {
            // We better just return right here,..
            setResult(RESULT_CANCELED, null);
            finish();
        } else if (request_code == Request.DATE.ordinal()) {
            Calendar other_calendar = (java.util.Calendar) data.getSerializableExtra("Date");
            calendar.set(other_calendar.get(Calendar.YEAR), other_calendar.get(Calendar.MONTH), other_calendar.get(Calendar.DAY_OF_MONTH));
            Intent intent = new Intent(this, TimePickingActivity.class);
            startActivityForResult(intent, Request.TIME.ordinal());
        } else if (request_code == Request.TIME.ordinal()) {
            int hour = Integer.valueOf(data.getStringExtra("Hour"));
            int minute = Integer.valueOf(data.getStringExtra("Minute"));
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
        }
    }
}
