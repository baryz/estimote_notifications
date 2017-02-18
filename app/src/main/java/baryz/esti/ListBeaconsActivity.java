package baryz.esti;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.estimote.sdk.utils.L;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
//import java.util.logging.Handler;

import database.Category;
import database.Notify;
import database.Sensor;
import webservice.CategoryWebServ;
import webservice.NotifyWebServ;
import webservice.SensorWebServ;

/**
 * Created by user on 2015-02-09.
 */
public class ListBeaconsActivity extends Activity {
    private static final String TAG = ListBeaconsActivity.class.getSimpleName();

    public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
    public static final String EXTRAS_BEACON = "extrasBeacon";

    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    public   static  final String EXTRA_MESSAGE_ADDED_DATE="addedDate";
    public   static  final String EXTRA_MESSAGE_TITLE="title";
    public   static final String EXTRA_SENSOR_ID = "sensorId";
    public   static  final String EXTRA_CAT_ID ="categoryId";
    public   static  final String EXTRA_MESSAGE_TEXT="text";



    private BeaconManager beaconManager;
    private LeDeviceListAdapter adapter;
    private NotificationManager notificationManager;
    private  static  Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacons_in_range);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        context=this;

        if(!DialogManager.haveInternet(context)){
            DialogManager.showNoConnectionDialog(context);
        }

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Configure device list.
        adapter = new LeDeviceListAdapter(this);
        ListView list = (ListView) findViewById(R.id.device_list);
        list.setAdapter(adapter);

       //set event OnClick in item of list
       // list.setOnItemClickListener(createOnItemClickListener());

        // Configure verbose debug logging.
       // L.enableDebugLogging(true);

        // Configure BeaconManager.
        beaconManager = new BeaconManager(this);
        long scanPeriodMillis = Long.parseLong(getString(R.string.scanPeriodMillis));
        long waitTimeMillis = Long.parseLong(getString(R.string.waitTimeMilis));
        beaconManager.setForegroundScanPeriod(scanPeriodMillis,waitTimeMillis);
        //beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                // Note that results are not delivered on UI thread.
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        getActionBar().setSubtitle(getString(R.string.foundSensors) + " "+beacons.size());

                        updateLocalData();

                        Sensor tmpSens;
                        Iterator<Beacon> itBeaconIterator=beacons.iterator();
                        while(itBeaconIterator.hasNext()) {
                            Beacon tmpBeacon = itBeaconIterator.next();
                            tmpSens = new Sensor(ListBeaconsActivity.context, tmpBeacon.getMacAddress());
                            Collection<Category> cat= Category.getCategory(getBaseContext(),tmpSens.getIdSensor(),true);
                            updateLocalFollowingNotify(tmpSens.getFollowingCatToLink(getBaseContext(),cat));
                            final Collection<Notify> nots = Notify.getFollowingNotify(ListBeaconsActivity.context, tmpSens.getIdSensor());

                            try{
                                double distance=Utils.computeAccuracy(tmpBeacon);
                                if(tmpSens.getDistanceBcast()>distance){

                                    tmpSens.getIdSensor();
                                    postNotification(nots);
                                }

                            }catch (Exception e){
                                //Log.e(TAG, "InterruptedException: " , e);
                            }


                        }
                            adapter.replaceWith(beacons);

                    }
                });
            }
        });





    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem refreshItem = menu.findItem(R.id.refresh);
        refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        beaconManager.disconnect();

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!DialogManager.haveInternet(context)){
            DialogManager.showNoConnectionDialog(context);
        }
        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, getString(R.string.deviceHasNotBtLowEnergy), Toast.LENGTH_LONG).show();
            return;
        }

        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToService();
        }
    }

    @Override
    protected void onStop() {
       /* try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging", e);
        } */

        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                connectToService();
            } else {
                Toast.makeText(this, getString(R.string.btNotEnabled), Toast.LENGTH_LONG).show();
                getActionBar().setSubtitle(getString(R.string.btNotEnabled));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectToService() {
        getActionBar().setSubtitle(getString(R.string.lookingFor));
        adapter.replaceWith(Collections.<Beacon>emptyList());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Toast.makeText(ListBeaconsActivity.this, getString(R.string.errorToConnectSensors),
                            Toast.LENGTH_LONG).show();
                   // Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

   /* private AdapterView.OnItemClickListener createOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY) != null) {
                    try {
                        Class<?> clazz = Class.forName(getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY));
                        Intent intent = new Intent(ListBeaconsActivity.this, clazz);
                        intent.putExtra(EXTRAS_BEACON, adapter.getItem(position));
                        startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        Log.e(TAG, "Finding class by name failed", e);
                    }
                }
            }
        };
    } */



    private void postNotification(Collection<Notify> collection) throws InterruptedException {

        int i=0;
        Iterator<Notify> notIt = collection.iterator();


        while (notIt.hasNext()) {

            final Notify tmpNotify = notIt.next();
            Category tmpCat=new Category(getBaseContext(),tmpNotify.getIdCat(),tmpNotify.getIdSensor());
            Intent intent = new Intent(context, MessageViewActivity.class);

            //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.putExtra(EXTRA_MESSAGE_ADDED_DATE, tmpNotify.getAddedDate());
            intent.putExtra(EXTRA_SENSOR_ID, tmpNotify.getIdSensor());
            intent.putExtra(EXTRA_MESSAGE_TITLE, tmpNotify.getTitle());
            intent.putExtra(EXTRA_CAT_ID,tmpNotify.getIdCat());
            intent.putExtra(EXTRA_MESSAGE_TEXT, tmpNotify.getText());


            int requestID = (int) System.currentTimeMillis();
            PendingIntent pIntent = PendingIntent.getActivity(context, requestID, intent, 0);


            final Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(tmpNotify.getTitle())
                    .setTicker(tmpNotify.getTitle())
                    .setContentText(tmpNotify.getText())
                    .setAutoCancel(true)
                    .setContentIntent(pIntent)
                    .build();

            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_LIGHTS;


            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(tmpCat!=null && tmpCat.getIsFollow()==true) {
                if (tmpNotify.getIsRinging() == 0) {
                     notificationManager.notify(tmpNotify.getIdNtf(), notification);
                     tmpNotify.setIsRinging(context);

                }
            }




        }
    }

    private  void updateLocalData(){

        String serviceCat=getString(R.string.globalHost)+getString(R.string.getCategories);
        String serviceSensor=getString(R.string.globalHost)+getString(R.string.getSensors);
       // String serviceNotify=getString(R.string.globalHost)+getString(R.string.getNotify);

       // NotifyWebServ wb2= new NotifyWebServ(serviceNotify,getBaseContext());
        //wb2.execute(null,null,null);
        CategoryWebServ wb= new CategoryWebServ(serviceCat,getBaseContext());
        wb.execute(null,null,null);
        SensorWebServ wb1= new SensorWebServ(serviceSensor,getBaseContext());
        wb1.execute(null,null,null);
    }

    private void updateLocalFollowingNotify(String serviceLink) {

        NotifyWebServ ws= new NotifyWebServ(serviceLink,getBaseContext());
        ws.execute(null,null,null);

    }

}
