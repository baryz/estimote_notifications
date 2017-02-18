package baryz.esti;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import database.Category;
import database.DatabaseManager;
import database.Notify;
import database.Sensor;
import webservice.CategoryWebServ;
import webservice.NotifyWebServ;
import webservice.SensorWebServ;

public class MainActivity extends Activity {

    //private TextView  debugTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // debugTextView=(TextView)findViewById(R.id.debugTextView);

      findViewById(R.id.message_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListBeaconsActivity.class);
                intent.putExtra(ListBeaconsActivity.EXTRAS_TARGET_ACTIVITY, ListBeaconsActivity.class.getName());
                startActivity(intent);
            }
        });

        findViewById(R.id.followCatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FollowingCategoriesActivity.class);
                startActivity(intent);
            }
        });


        if(!DialogManager.haveInternet(this)){
            DialogManager.showNoConnectionDialog(this);
        }

        updateLocalData();

      /*
         DatabaseManager dbManager=new DatabaseManager(this);
        Cursor k;
        Collection<Category> cat=Category.getCategory(this);
        Iterator<Category> itCat = cat.iterator();
        while(itCat.hasNext()){
            Category tmpCategory=itCat.next();
            //debugTextView.setText(debugTextView.getText()+"\n "+tmpCategory.getIsFollow()+" id_Cat: "+ tmpCategory.getIdCat()+"sensorTitle: "+tmpCategory.getTitleSensor()+" id_sens: "+tmpCategory.getIdSensor()+ " name Cat: " + tmpCategory.getNameCat()  );
            debugTextView.setText(debugTextView.getText()+"\n " +tmpCategory.getIsFollow()+"  " + tmpCategory.getNameCat()+" "+tmpCategory.getTitleSensor());
        }



        k=dbManager.getSensors();
        debugTextView.setText(debugTextView.getText()+"\n Sensory w bazie lokalnej");
        while(k.moveToNext()) {
            int id=k.getInt(0);
            int idSensor=k.getInt(1);
            String title=k.getString(2);
            String uuid=k.getString(3);
            int major=k.getInt(4);
            int minor=k.getInt(5);
            String mac=k.getString(6);
            int distanceBcast=k.getInt(7);

            debugTextView.setText(debugTextView.getText()+"\n id_sensor:"+idSensor+" "+title+" major:"+major+" minor:"+minor+"  mac:"+mac);
        }
        k.close();


        Collection<Notify> notify= Notify.getNotify(this);
        Iterator<Notify> it = notify.iterator();
        while(it.hasNext()){
            Notify tmpNotify=it.next();
            debugTextView.setText(debugTextView.getText()+"\n id_NTF: "+ tmpNotify.getIdNtf()+ " title: " + tmpNotify.getTitle()+" text: "+ tmpNotify.getText() );
        }

        dbManager.close();*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!DialogManager.haveInternet(this)){
            DialogManager.showNoConnectionDialog(this);
        }
        //debugTextView=(TextView)findViewById(R.id.debugTextView);
        //debugTextView.setText(debugTextView.getText()+"\n ON RESUME");
        updateLocalData();



    }

    @Override
    protected void onStart() {
        super.onStart();

       // debugTextView=(TextView)findViewById(R.id.debugTextView);
       //debugTextView.setText(debugTextView.getText()+"\n ON START");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void updateLocalFollowingNotify(String serviceLink) {

        NotifyWebServ ws= new NotifyWebServ(serviceLink,getBaseContext());
        ws.execute(null,null,null);

    }
    private void  updateLocalData(){

        String serviceCat=getString(R.string.globalHost)+getString(R.string.getCategories);
        String serviceSensor=getString(R.string.globalHost)+getString(R.string.getSensors);


        CategoryWebServ wb= new CategoryWebServ(serviceCat,this);
        wb.execute(null,null,null);
        SensorWebServ wb1= new SensorWebServ(serviceSensor,this);
        wb1.execute(null,null,null);

        Collection<Sensor> listSens= Sensor.getSensors(this);
        Iterator<Sensor> itSens = listSens.iterator();
        Sensor tmpSens;
        if(listSens!=null) {
            while (itSens.hasNext()){
                tmpSens=itSens.next();
                Collection<Category> cat = Category.getCategory(getBaseContext(), tmpSens.getIdSensor(), true);
                updateLocalFollowingNotify(tmpSens.getFollowingCatToLink(getBaseContext(), cat));
            }


        }
    }

}
