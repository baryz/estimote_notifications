package database;


import android.content.Context;
import android.database.Cursor;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import baryz.esti.R;

public class Sensor {

   public Sensor(Context context,String mac) {
        DatabaseManager dbManager=new DatabaseManager(context);

        Cursor curs=dbManager.getSensor(mac);

        if(curs!= null && curs.getCount()>0){
            curs.moveToNext();
            this.id=curs.getInt(0);
            this.idSensor=curs.getInt(1);
            this.title=curs.getString(2);
            this.uuid=curs.getString(3);
            this.major=curs.getInt(4);
            this.minor=curs.getInt(5);
            this.mac=curs.getString(6);
            this.distanceBcast=curs.getInt(7);
            this.describe=curs.getString(8);
            curs.close();

        }else{

            this.id=0;
            this.idSensor=0;
            this.title= context.getString(R.string.noData);
            this.uuid="0";
            this.major=0;
            this.minor=0;
            this.mac="";
            this.distanceBcast=0;
            this.describe="";


        }

        if(curs!=null && !curs.isClosed()) curs.close();
        dbManager.close();

    }


    public Sensor(Context context,int idSensor) {
        DatabaseManager dbManager=new DatabaseManager(context);

        Cursor curs=dbManager.getSensor(idSensor);

        if(curs!= null && curs.getCount()>0){
            curs.moveToNext();
            this.id=curs.getInt(0);
            this.idSensor=curs.getInt(1);
            this.title=curs.getString(2);
            this.uuid=curs.getString(3);
            this.major=curs.getInt(4);
            this.minor=curs.getInt(5);
            this.mac=curs.getString(6);
            this.distanceBcast=curs.getInt(7);
            this.describe=curs.getString(8);
            curs.close();

        }else{

            this.id=0;
            this.idSensor=0;
            this.title= context.getString(R.string.noData);
            this.uuid="0";
            this.major=0;
            this.minor=0;
            this.mac="";
            this.distanceBcast=0;
            this.describe="";

        }
        if(curs!=null && !curs.isClosed()) curs.close();
        dbManager.close();

    }

    public  static Collection<Sensor> getSensors(Context context){
        DatabaseManager dbManager=new DatabaseManager(context);
        Cursor curs=dbManager.getSensors();
        Collection<Sensor> resultList=new ArrayList<Sensor>();
        Sensor tmpSensor;
        int idSens;
        while (curs!=null&&curs.moveToNext()){
                idSens=curs.getInt(1);
                tmpSensor=new Sensor(context,idSens);
                resultList.add(tmpSensor);
            }


        if(curs!=null && !curs.isClosed()) curs.close();
        dbManager.close();
        return  resultList;
    }
    public  String getFollowingCatToLink(Context context,Collection<Category> cat){

        String result= context.getString(R.string.globalHost)+context.getString(R.string.getNotify)+"?";
        Category category;
        Iterator<Category> itCat = cat.iterator();
        if(!itCat.hasNext()) result+=context.getResources().getString(R.string.idCatParamName)+"=0&";
        while (itCat.hasNext()){
            category=itCat.next();
            result+=context.getResources().getString(R.string.idCatParamName)+"="+category.getIdCat()+"&";

        }

        result+= context.getResources().getString(R.string.idSensorParamName)+ "="+ this.getIdSensor();
        return  result;
    }
    public  static  void  updateLocalSensor(Context context,JSONArray sensors){
        DatabaseManager dbManager=new DatabaseManager(context);
        dbManager.updateLocalSensors(sensors);
        dbManager.close();
    }

    public int getId() {
        return id;
    }



    public int getIdSensor() {
        return idSensor;
    }

    private  int id;
    private  int idSensor;

    public int getIsActiv() {
        return isActiv;
    }

    public String getUuid() {
        return uuid;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public String getBatteryChanged() {
        return batteryChanged;
    }

    public String getTitle() {
        return title;
    }

    public String getDescribe() {
        return describe;
    }

    public int getDistanceBcast() {
        return distanceBcast;
    }

    public String getPositionDescribe() {
        return positionDescribe;
    }

    public String getMac() {
        return mac;
    }

    private  int isActiv ;
    private  String uuid;
    private  int major  ;
    private  int minor  ;
    private  String batteryChanged ;
    private  String title ;
    private  String describe;
    private  int distanceBcast;
    private  String positionDescribe;
    private  String mac;


}
