package database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import baryz.esti.R;


public class DatabaseManager extends SQLiteOpenHelper {

    private  static final int DATABASE_VERSION=39;
    private SQLiteDatabase sqlDb;
    private  Context context;
    private static final  String DATABASE_NAME="EstSettings4.db",
    TABLE_CATEGORIES="categories",
    KEY_ID="id",
    KEY_TITLE_SENSOR="title",
    KEY_NAME_CAT="name",
    KEY_ID_CAT="id_cat",
    KEY_ID_SENSOR="id_sensor",
    KEY_IS_FOLLOW="is_follow";

    private  static  final String TABLE_SENSORS="sensor",
            KEY_UUID="uuid",
            KEY_MAJOR="major",
            KEY_MINOR="minor",
            KEY_MAC="mac",
            KEY_DISTANCE_BCAST="distance_bcast",
            KEY_DESCRIBE_SENSOR="describe";

    private static final  String TABLE_NOTIFY ="Notify",
    KEY_ID_NTF="id_ntf",
    KEY_ID_EST="id_est",
    KEY_TITLE_NTF="title_ntf",
    KEY_TEXT_NTF="text_ntf",
    KEY_ADDED_DATE="added_date",
    KEY_IS_RINGING ="is_ringing";




    public DatabaseManager(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public  void onCreate(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE "+TABLE_CATEGORIES+"("+
                 KEY_ID+" integer primary key autoincrement,"+
                 KEY_TITLE_SENSOR+" text,"+
                 KEY_NAME_CAT+" text," +
                 KEY_ID_CAT+" integer,"+
                 KEY_ID_SENSOR+" integer,"+
                 KEY_IS_FOLLOW+" integer);"
        );
        db.execSQL(
                "CREATE TABLE "+TABLE_SENSORS+"("+
                        KEY_ID+" integer primary key autoincrement,"+
                        KEY_ID_SENSOR+" int,"+
                        KEY_TITLE_SENSOR+" text,"+
                        KEY_UUID+" text," +
                        KEY_MAJOR+" integer,"+
                        KEY_MINOR+" integer,"+
                        KEY_MAC+" text,"+
                        KEY_DISTANCE_BCAST+" integer,"+
                        KEY_DESCRIBE_SENSOR+" text);"

        );

        db.execSQL(
                "CREATE TABLE "+ TABLE_NOTIFY +"("+
                        KEY_ID+" integer primary key autoincrement,"+
                        KEY_ID_NTF+" integer,"+
                        KEY_ID_CAT+" integer,"+
                        KEY_ID_EST+" integer," +
                        KEY_TITLE_NTF+" text,"+
                        KEY_TEXT_NTF+" text,"+
                        KEY_ADDED_DATE+" text,"+
                        KEY_IS_RINGING +" integer);"
        );
    }

    @Override
    public  void onUpgrade(SQLiteDatabase db,int oldVers, int newVers){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFY);
        onCreate(db);

    }

    /*
    *  CAIEGORY TABLE
     */
    public void addCategory(String title,String name,int idCat,int idSensor,boolean isFollow){
        //SQLiteDatabase db=getWritableDatabase();
        sqlDb=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_TITLE_SENSOR,title);
        values.put(KEY_NAME_CAT,name);
        values.put(KEY_ID_CAT,idCat);
        values.put(KEY_ID_SENSOR,idSensor);
        values.put(KEY_IS_FOLLOW,isFollow);
        sqlDb.insertOrThrow(TABLE_CATEGORIES,null,values);
        sqlDb.close();

    }

    public void deleteCategory(int idCat,int idSensor){
        //SQLiteDatabase db=getWritableDatabase();
        sqlDb = getWritableDatabase();
        String[] args = {""+ idCat,""+idSensor};
        sqlDb.delete(TABLE_CATEGORIES,  KEY_ID_CAT+"=? and "+ KEY_ID_SENSOR+"=?" ,args);
        sqlDb.close();

    }

    public  Cursor getCategory(int idCat,int idSensor){

        String[] cols={KEY_ID,KEY_TITLE_SENSOR,KEY_NAME_CAT,KEY_ID_CAT,KEY_ID_SENSOR,KEY_IS_FOLLOW};
        String[] args = {Integer.toString(idCat),Integer.toString(idSensor)};
        //SQLiteDatabase db=getReadableDatabase();
        if(sqlDb!=null&&sqlDb.isOpen()){
            sqlDb.close();
        }
        sqlDb=getReadableDatabase();
        Cursor curs=sqlDb.query(TABLE_CATEGORIES,cols,KEY_ID_CAT+"=? AND "+KEY_ID_SENSOR+"=? ",args,null,null,null);

        if(curs!=null && curs.getCount()>0){
            //curs.moveToNext();

            return curs;
        }

        return curs;


    }
    public Cursor getCategories(){

        String[] cols={KEY_ID,KEY_TITLE_SENSOR,KEY_NAME_CAT,KEY_ID_CAT,KEY_ID_SENSOR,KEY_IS_FOLLOW};
        sqlDb=getReadableDatabase();
        Cursor curs=sqlDb.query(TABLE_CATEGORIES,cols,null,null,null,null,KEY_TITLE_SENSOR+" , "+KEY_NAME_CAT );
        //db.close();
        return curs;

    }

    public Cursor getCategories(int idSensor){

        String[] cols={KEY_ID,KEY_TITLE_SENSOR,KEY_NAME_CAT,KEY_ID_CAT,KEY_ID_SENSOR,KEY_IS_FOLLOW};
        String[] args = {Integer.toString(idSensor)};
        SQLiteDatabase db=getReadableDatabase();

        Cursor curs=db.query(TABLE_CATEGORIES,cols,KEY_ID_SENSOR+"=? ",args,null,null,null);
        return curs;

    }
    public  int setIsFollow(int idSensor,int idCat,boolean followFlag){

        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put(KEY_ID_CAT,idCat);
        values.put(KEY_ID_SENSOR,idSensor);
        int isFollow = (followFlag)? 1:0;
        values.put(KEY_IS_FOLLOW,isFollow);



        int result=db.update(TABLE_CATEGORIES,values,KEY_ID_SENSOR+"="+idSensor+" AND "+KEY_ID_CAT+ "="+idCat,null);
        return  result;
    }

    public boolean updateLocalCategories(JSONArray categories){

        //SQLiteDatabase db=getReadableDatabase();
        sqlDb=getReadableDatabase();
        String title,name;
        int id_cat,id_sensor;
       Cursor curs =null;

        try{
            for (int i = 0; i < categories.length(); i++) {

                JSONObject json_data = categories.getJSONObject(i);
                title=json_data.getString(KEY_TITLE_SENSOR);
                name= json_data.getString(KEY_NAME_CAT);
                id_cat=json_data.getInt(KEY_ID_CAT);
                id_sensor=json_data.getInt(KEY_ID_SENSOR);

                //Log.i("log_tag", " CAT IN DB "+KEY_TITLE_SENSOR+": " + title + ", "+KEY_NAME_CAT+": " + name +
                 //              ", "+KEY_ID_CAT+": " + id_cat +", "+KEY_ID_SENSOR+": " + id_sensor
               // );

                String[]  cols={KEY_ID,KEY_TITLE_SENSOR,KEY_NAME_CAT,KEY_ID_CAT,KEY_ID_SENSOR,KEY_IS_FOLLOW};
                String[]   args={Integer.toString(id_cat),Integer.toString(id_sensor)};
                if (sqlDb.isOpen()){
                    sqlDb.close();

                }
                sqlDb=getReadableDatabase();
                curs=sqlDb.query(TABLE_CATEGORIES, cols, KEY_ID_CAT + "=? AND " + KEY_ID_SENSOR + "=?", args, null, null, null);
                //if exist in localDb, no add
                if(curs!=null && curs.moveToNext()){
                    continue;}
                else {
                    int isFollow = Integer.parseInt( context.getString(R.string.defaultIsFollowingForNewAddedCat));
                    boolean isFollowFlag = (isFollow==1)? true:false;
                    addCategory(title,name,id_cat,id_sensor,isFollowFlag);
                }
            }


            curs = getCategories();
            //checking if existed local_data categories  are  on Database on server
            while (curs!=null && curs.moveToNext()) {
                 id_cat=curs.getInt(3);
                 id_sensor=curs.getInt(4);
                 boolean sFlag=false;
                 for (int i = 0; i < categories.length(); i++) {
                    JSONObject json_data = categories.getJSONObject(i);
                    if(json_data.getInt(KEY_ID_CAT)==id_cat
                            && json_data.getInt(KEY_ID_SENSOR)==id_sensor){
                        sFlag=true;
                        break;
                    }
                 }
                 if(!sFlag) deleteCategory(id_cat,id_sensor);


            }

            if(curs!=null && !curs.isClosed()){
                curs.close();
            }
            if(sqlDb!=null && sqlDb.isOpen()){
                sqlDb.close();
            }

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }finally{
            if(curs!=null && !curs.isClosed()){
                curs.close();
            }
            if(sqlDb!=null && sqlDb.isOpen()){
                sqlDb.close();
            }

        }


         return true;
    }

    /*
    *     TABLE SENSORS
     */
    public Cursor getSensors(){

        String[] cols={KEY_ID,KEY_ID_SENSOR,KEY_TITLE_SENSOR,KEY_UUID,KEY_MAJOR,KEY_MINOR,KEY_MAC,KEY_DISTANCE_BCAST,KEY_DESCRIBE_SENSOR};
        sqlDb=getReadableDatabase();
        Cursor curs=sqlDb.query(TABLE_SENSORS,cols,null,null,null,null,null);

        return curs;

    }

    public void addSensor(int idSensor,String uuid,int major,int minor,String title,int distanceBcast,String mac,String describe){
        //SQLiteDatabase db=getWritableDatabase();
       sqlDb=getWritableDatabase();


        ContentValues values=new ContentValues();
        values.put(KEY_ID_SENSOR,idSensor);
        values.put(KEY_TITLE_SENSOR,title);
        values.put(KEY_MAJOR,major);
        values.put(KEY_MINOR,minor);
        values.put(KEY_DISTANCE_BCAST,distanceBcast);
        values.put(KEY_MAC,mac);
        values.put(KEY_DESCRIBE_SENSOR,describe);
        sqlDb.insertOrThrow(TABLE_SENSORS,null,values);
        sqlDb.close();

    }

    public Cursor getSensor(String mac){

        String[] cols={KEY_ID,KEY_ID_SENSOR,KEY_TITLE_SENSOR,KEY_UUID,KEY_MAJOR,KEY_MINOR,KEY_MAC,KEY_DISTANCE_BCAST,KEY_DESCRIBE_SENSOR};

        //SQLiteDatabase db=getReadableDatabase();

        sqlDb=getReadableDatabase();
       String[] args = {mac};
        Cursor curs=sqlDb.query(TABLE_SENSORS,cols, KEY_MAC+"=?",args,null,null,null);
        if( curs!=null && curs.getCount()>0)
            return curs;
        else{
            return  null;
        }


    }

    public Cursor getSensor(int idSensor){

        String[] cols={KEY_ID,KEY_ID_SENSOR,KEY_TITLE_SENSOR,KEY_UUID,KEY_MAJOR,KEY_MINOR,KEY_MAC,KEY_DISTANCE_BCAST,KEY_DESCRIBE_SENSOR};

        //SQLiteDatabase db=getReadableDatabase();
        sqlDb=getReadableDatabase();
        String[] args = {Integer.toString(idSensor)};
        Cursor curs=sqlDb.query(TABLE_SENSORS,cols, KEY_ID_SENSOR+"=?",args,null,null,null);
        if( curs!=null && curs.getCount()>0)
            return curs;
        else{
            return  null;
        }


    }

    public  boolean checkSensorMac(String mac){

        String[] cols={KEY_ID,KEY_MAJOR,KEY_MINOR,KEY_TITLE_SENSOR,KEY_MAC};
        String[] args = {""+ mac};
       // SQLiteDatabase db=getReadableDatabase();
        sqlDb = getReadableDatabase();
        Cursor curs=sqlDb.query(TABLE_SENSORS,cols, KEY_MAC+"=?",args,null,null,null);

        if(curs!=null && curs.getCount()>0) {
            if( ! curs.isClosed()) curs.close();
            if(!sqlDb.isOpen()) sqlDb.close();
            return true;
        }else {
            if( ! curs.isClosed()) curs.close();
            if(!sqlDb.isOpen()) sqlDb.close();
            return false;
        }

    }

    public void updateLocalSensors(JSONArray sensors){

        //SQLiteDatabase db=getReadableDatabase();
        String title,uuid,mac,describe;
        int idSensor,major,minor,distanceBcast;
        Cursor curs =null;

        //SQLiteDatabase db=getWritableDatabase();


        try{
            for (int i = 0; i < sensors.length(); i++) {
                sqlDb=getWritableDatabase();
                JSONObject json_data = sensors.getJSONObject(i);
                idSensor=json_data.getInt(KEY_ID_SENSOR);
                uuid=json_data.getString(KEY_UUID);
                major=json_data.getInt(KEY_MAJOR);
                minor=json_data.getInt(KEY_MINOR);
                title=json_data.getString(KEY_TITLE_SENSOR);
                distanceBcast= json_data.getInt(KEY_DISTANCE_BCAST);
                mac=json_data.getString(KEY_MAC);
                describe=json_data.getString(KEY_DESCRIBE_SENSOR);

                //Log.i("log_tag", "Dodaje id_sensor:" + idSensor+  "BCAST: "+distanceBcast +" mac:"+mac+ "OPIS:"+describe);
                //String[]  cols={"id","title","name","id_cat","id_sensor","is_follow"};

                ContentValues values=new ContentValues();
                //values.put(KEY_ID_SENSOR,id_sensor);
                values.put(KEY_UUID,uuid);
                values.put(KEY_MAJOR,major);
                values.put(KEY_MINOR,minor);
                values.put(KEY_TITLE_SENSOR,title);
                values.put(KEY_DISTANCE_BCAST,distanceBcast);
                values.put(KEY_MAC,mac);
                values.put(KEY_DESCRIBE_SENSOR,describe);

                int result=sqlDb.update(TABLE_SENSORS,values,KEY_ID_SENSOR+"="+idSensor,null);

                if(checkSensorMac(mac)){
                    continue;
                }else{
                    addSensor(idSensor,uuid,major,minor,title,distanceBcast,mac,describe);
                }

              }
            curs = getSensors();
            //checking if existed local_data sensor  are  on Database on server
            while (curs!=null && curs.moveToNext()) {
                idSensor=curs.getInt(1);
                boolean sFlag=false;
                for (int i = 0; i < sensors.length(); i++) {
                    JSONObject json_data = sensors.getJSONObject(i);
                    if( json_data.getInt(KEY_ID_SENSOR)==idSensor){
                        sFlag=true;
                        break;
                    }
                }
                if(!sFlag) deleteSensor(idSensor);

            }

            if(curs!=null&&!curs.isClosed()){
                curs.close();
            }
            if(!sqlDb.isOpen())sqlDb.close();

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }finally {
            if(curs!=null&&!curs.isClosed()){
                curs.close();
            }
            if(!sqlDb.isOpen())sqlDb.close();
        }

    }

    private void deleteSensor(int idSensor){
        //SQLiteDatabase db=getWritableDatabase();
        sqlDb=getWritableDatabase();
        String[] args = {" "+idSensor};
        sqlDb.delete(TABLE_SENSORS, KEY_ID_SENSOR + "=?", args);
        sqlDb.close();

    }

/*
*  NOTIFY TABLE
 */
    public void addNotify (int idNtf,int idCat,int idSensor,String title,String text,String addedDate,int isRinging){

        //SQLiteDatabase db=getWritableDatabase();

        sqlDb=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_ID_NTF,idNtf);
        values.put(KEY_ID_CAT,idCat);
        values.put(KEY_ID_EST,idSensor);
        values.put(KEY_TITLE_NTF,title);
        values.put(KEY_TEXT_NTF,text);
        values.put(KEY_ADDED_DATE,addedDate);
        values.put(KEY_IS_RINGING,isRinging);

        sqlDb.insertOrThrow(TABLE_NOTIFY,null,values);
        sqlDb.close();


    }

    /**
     * @param idNtf  id of notify, which has it yet ringed
     * @return the value of updated records.
     */
    public  int setIsRinging(int idNtf){

        // SQLiteDatabase db=getWritableDatabase();
        sqlDb=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_ID_NTF,idNtf);
        values.put(KEY_IS_RINGING,1);   // ringing is only set once time, when the user tap on the  notify or cancel it

        int result=sqlDb.update(TABLE_NOTIFY,values,KEY_ID_NTF+"="+idNtf,null);
        sqlDb.close();
        return  result;
    }

    public  Cursor getNotify(int idNtf){

        String[] cols={KEY_ID,KEY_ID_NTF,KEY_ID_CAT,KEY_ID_EST,KEY_TITLE_NTF,KEY_TEXT_NTF,KEY_ADDED_DATE, KEY_IS_RINGING};

        //SQLiteDatabase db=getReadableDatabase();
        sqlDb=getReadableDatabase();
        String[] args = {Integer.toString(idNtf)};
        Cursor curs=sqlDb.query(TABLE_NOTIFY,cols, KEY_ID_NTF+"=?",args,null,null,null);
        if( curs!=null && curs.getCount()>0) {

            return curs;
        }
        else{
            if(curs!=null && !curs.isClosed()) curs.close();

            return  null;
        }



    }

    public Cursor getAllNotify(){

        String[] cols={KEY_ID,KEY_ID_NTF,KEY_ID_CAT,KEY_ID_EST,KEY_TITLE_NTF,KEY_TEXT_NTF,KEY_ADDED_DATE, KEY_IS_RINGING};
       // SQLiteDatabase db=getReadableDatabase();
        sqlDb=getReadableDatabase();
        Cursor curs=sqlDb.query(TABLE_NOTIFY,cols,null,null,null,null,null);

        return curs;

    }

    public Cursor getNotifyByIdSensor(int idSensor){

        String[] cols={KEY_ID,KEY_ID_NTF,KEY_ID_CAT,KEY_ID_EST,KEY_TITLE_NTF,KEY_TEXT_NTF,KEY_ADDED_DATE, KEY_IS_RINGING};
        String[] args = {Integer.toString(idSensor)};
        //SQLiteDatabase db=getReadableDatabase();
        sqlDb=getReadableDatabase();
        Cursor curs=sqlDb.query(TABLE_NOTIFY,cols, KEY_ID_EST+"=?",args,null,null,null);

        if( curs!=null && curs.getCount()>0)
            return curs;
        else{
            if(curs!=null && curs.isClosed()) curs.close();
            return  null;
        }


    }


    public void updateLocalNotify(JSONArray notify){



        int idNtf,idCat,idSensor;
        String title,text,addedDate;

        try{
            for (int i = 0; i < notify.length(); i++) {

                JSONObject json_data = notify.getJSONObject(i);
                idNtf=json_data.getInt(KEY_ID_NTF);
                idCat=json_data.getInt(KEY_ID_CAT);
                idSensor=json_data.getInt(KEY_ID_EST);
                title=json_data.getString(KEY_TITLE_NTF);
                text=json_data.getString(KEY_TEXT_NTF);
                addedDate=json_data.getString(KEY_ADDED_DATE);

               //Log.i("log_tag", "Insert idNtf:" + idNtf+" title: "+title +" text: "+text);
                String[] cols={KEY_ID,KEY_ID_NTF,KEY_ID_CAT,KEY_ID_EST,KEY_TITLE_NTF,KEY_TEXT_NTF,KEY_ADDED_DATE, KEY_IS_RINGING};

                if(checkNotify(idNtf)){
                    continue;
                }else{
                    addNotify(idNtf, idCat, idSensor, title, text, addedDate, 0);
                }

            }

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }



    }

    public  boolean checkNotify(int idNtf){

        String[] cols={KEY_ID,KEY_ID_NTF,KEY_ID_CAT,KEY_ID_EST,KEY_TITLE_NTF,KEY_TEXT_NTF,KEY_ADDED_DATE, KEY_IS_RINGING};
        String[] args = {Integer.toString(idNtf)};
        //SQLiteDatabase db=getReadableDatabase();
        sqlDb=getReadableDatabase();
        Cursor curs=sqlDb.query(TABLE_NOTIFY,cols, KEY_ID_NTF+"=?",args,null,null,null);

        if(curs!=null&&curs.getCount()>0) {
            curs.close();
            if(sqlDb.isOpen())  sqlDb.close();
            return true;
        }else {
            if(curs!=null && !curs.isClosed()) curs.close();
            if(sqlDb.isOpen()) sqlDb.close();
            return false;
        }

    }

    public  void close(){

        if(sqlDb!=null && sqlDb.isOpen()) sqlDb.close();
    }

}
