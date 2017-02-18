package baryz.esti;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import database.Category;
import database.Sensor;

/**
 * Created by user on 2015-02-14.
 */
public class MessageViewActivity extends Activity {

    private  TextView addedDateTextView;
    private  TextView titleTextView;
    private  TextView senosrTextView;
    private TextView categoryNameTextView;
    private  TextView contentMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_view);

        addedDateTextView=(TextView) findViewById(R.id.addedDateTextView);
        titleTextView=(TextView) findViewById(R.id.messageTitleTextView);
        senosrTextView= (TextView) findViewById(R.id.sensorTextView);
        categoryNameTextView=(TextView) findViewById(R.id.categoryNameTextView);
        contentMessageTextView = (TextView) findViewById(R.id.contentMessageTextView);

        Intent intent = getIntent();
        String addedDate,title,contentMessage,sensor,category;
        int idSensor,idCat;


        if (intent != null){
            addedDate = intent.getExtras().getString(ListBeaconsActivity.EXTRA_MESSAGE_ADDED_DATE);
            title = intent.getExtras().getString(ListBeaconsActivity.EXTRA_MESSAGE_TITLE);
            idSensor  = intent.getExtras().getInt(ListBeaconsActivity.EXTRA_SENSOR_ID);
            Sensor sen=new Sensor(getBaseContext(),idSensor);
            sensor=sen.getTitle();
            idCat  = intent.getExtras().getInt(ListBeaconsActivity.EXTRA_CAT_ID);
            Category cat= new Category(getBaseContext(),idCat,idSensor);
            category= cat.getNameCat();
            contentMessage = intent.getExtras().getString(ListBeaconsActivity.EXTRA_MESSAGE_TEXT);

        }else {
            addedDate=getString(R.string.noData);
            title=getString(R.string.noData);
            sensor = getString(R.string.noData);
            category = getString(R.string.noData);
            contentMessage= getString(R.string.noData);
        }

        addedDateTextView.setText(addedDate);
        titleTextView.setText(title);
        senosrTextView.setText(sensor);
        categoryNameTextView.setText(category);
        contentMessageTextView.setText(contentMessage);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


}
