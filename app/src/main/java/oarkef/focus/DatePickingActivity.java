package oarkef.focus;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;


public class DatePickingActivity extends ActionBarActivity {

    private DatePicker date_picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picking);
        date_picker = (DatePicker) findViewById(R.id.datePicker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input, menu);
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

    public void returnResult(View view)
    {

        int day = date_picker.getDayOfMonth();
        int month = date_picker.getMonth();
        int year = date_picker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        Intent out = new Intent();
        out.putExtra("Date", calendar);
        setResult(RESULT_OK, out);
        finish();
    }
}
