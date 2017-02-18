package baryz.esti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import database.DatabaseManager;
import database.Sensor;


public class LeDeviceListAdapter   extends BaseAdapter {

    private ArrayList<Beacon> beacons;
    private LayoutInflater inflater;
    private  DatabaseManager dbManager;
    private  Context context;

    public LeDeviceListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.beacons = new ArrayList<Beacon>();
        this.dbManager = new DatabaseManager(context);
        this.context=context;
    }

    public void replaceWith(Collection<Beacon> newBeacons) {
        this.beacons.clear();
        this.beacons.addAll(newBeacons);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return beacons.size();
    }

    @Override
    public Beacon getItem(int position) {
        if (beacons.size()>0)
            return beacons.get(position);
        else return  null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflateIfRequired(view, position, parent);
        bind(getItem(position), view);
        return view;
    }

    private void bind(Beacon beacon, View view) {


        //String serviceSens=Resources.getSystem().getString(R.string.getSensors);
        Sensor sens= new Sensor(this.context,beacon.getMacAddress());



//      String title = (sens.getTitle().equals(""))? "Brak czujnika w bazie":sens.getTitle();
  //    String distanceBcast = (sens.getDistanceBcast()==0)? " Brak danych ": sens.getDistanceBcast() + " m";


        ViewHolder holder = (ViewHolder) view.getTag();

        holder.titleSensorTextView.setText(view.getResources().getString(R.string.sensor)+": "+sens.getTitle());
        holder.distanceBcastTextView.setText(view.getResources().getString(R.string.distanceBcast)+": "+Integer.toString(sens.getDistanceBcast())+"m");
        holder.currentDistanceTextView.setText(String.format("%s: %.2fm",view.getResources().getString(R.string.currentDistance),Utils.computeAccuracy(beacon)));
        holder.describeSensorTextView.setText(view.getResources().getString(R.string.describe)+" " + sens.getDescribe());
        /*
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.macTextView.setText(String.format("MAC: %s (%.2fm)", beacon.getMacAddress(), Utils.computeAccuracy(beacon)));
        holder.majorTextView.setText("Major: " + beacon.getMajor());
        holder.minorTextView.setText("Minor: " + beacon.getMinor());
        holder.measuredPowerTextView.setText("MPower: " + beacon.getMeasuredPower());
        holder.rssiTextView.setText("RSSI: " + beacon.getRssi());
        holder.sensorTitleTextView.setText("Czujnik: "+ sens.getTitle());
        holder.distanceBcastTextView.setText("Zasieg rozgl: "+ sens.getDistanceBcast());*/


    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.beacon_item, null);
            view.setTag(new ViewHolder(view));
        }
        return view;
    }

    static class ViewHolder {

        final  TextView  titleSensorTextView;
        final  TextView  distanceBcastTextView;
        final  TextView  currentDistanceTextView;
        final  TextView  describeSensorTextView;
        /* DEBUG VIEW DATA BEACONS
        final TextView macTextView;
        final TextView majorTextView;
        final TextView minorTextView;
        final TextView measuredPowerTextView;
        final TextView rssiTextView;
        final TextView sensorTitleTextView;
        final  TextView distanceBcastTextView;
        */
        ViewHolder(View view) {

            titleSensorTextView=(TextView) view.findViewById(R.id.titleSensorTextView);
            distanceBcastTextView=(TextView) view.findViewById(R.id.distanceBcastTextView);
            currentDistanceTextView = (TextView) view.findViewById(R.id.currentDistanceTextView);
            describeSensorTextView=(TextView) view.findViewById(R.id.describeSensorTextView);

            /*macTextView = (TextView) view.findViewWithTag("mac");
            majorTextView = (TextView) view.findViewWithTag("major");
            minorTextView = (TextView) view.findViewWithTag("minor");
            measuredPowerTextView = (TextView) view.findViewWithTag("mpower");
            rssiTextView = (TextView) view.findViewWithTag("rssi");
            sensorTitleTextView = (TextView) view.findViewWithTag("sensorTitle");
            distanceBcastTextView= (TextView) view.findViewWithTag("distanceBcast");
            */

        }
    }
}


