package database;

import android.content.Context;
import android.database.Cursor;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by user on 2015-02-14.
 */
public class Notify {

  // private  DatabaseManager dbManager;
    private int id;
    private int idNtf;
    private  int idCat;
    private int idSensor;
    private String title;
    private  String text;
    private String addedDate;
    private  int isRinging;



    public  Notify(Context context,int idNtf){
         DatabaseManager dbManager=new DatabaseManager(context);
         Cursor curs=dbManager.getNotify(idNtf);

        if(curs!=null && curs.getCount()>0){
            curs.moveToNext();
            this.id=curs.getInt(0);
            this.idNtf=curs.getInt(1);
            this.idCat=curs.getInt(2);
            this.idSensor=curs.getInt(3);
            this.title=curs.getString(4);
            this.text=curs.getString(5);
            this.addedDate=curs.getString(6);
            this.isRinging=curs.getInt(7);

            curs.close();
        }

        if(curs!=null && !curs.isClosed()) curs.close();
        dbManager.close();

    }


    public Notify(Context context,int id, int idNtf, int idCat, int idSensor, String title, String text, String addedDate, int isRinging) {

        this.id = id;
        this.idNtf = idNtf;
        this.idCat = idCat;
        this.idSensor = idSensor;
        this.title = title;
        this.text = text;
        this.addedDate = addedDate;
        this.isRinging = isRinging;
    }

    public int getId() {
        return id;
    }

    public int getIdNtf() {
        return idNtf;
    }


    public int getIdCat() {
        return idCat;
    }

    public int getIdSensor() {
        return idSensor;
    }


    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public int getIsRinging() {
        return isRinging;
    }

    public  boolean setIsRinging(Context context){

        DatabaseManager dbManager=new DatabaseManager(context);
        int result =  dbManager.setIsRinging(this.idNtf);
        dbManager.close();
        if(result==1){
            this.isRinging=1;

            return  true;
        }else{

            return  false;
        }


    }



    public  static Collection<Notify> getNotify(Context context){

        Collection<Notify> resultList=new ArrayList<Notify>();
        DatabaseManager dbManager=new DatabaseManager(context);
        Cursor curs= dbManager.getAllNotify();

        if(curs!=null){
            while (curs.moveToNext()){
                Notify tmpNotify=new Notify(context,curs.getInt(1));
                resultList.add(tmpNotify);
            }
        }

        if(curs!=null && !curs.isClosed()) curs.close();
        dbManager.close();


        return  resultList;
    }

    public  static  Collection<Notify> getNotify(Context context,int idSensor){

        Collection<Notify> resultList=new ArrayList<Notify>();
        DatabaseManager dbManager=new DatabaseManager(context);
        Cursor curs= dbManager.getNotifyByIdSensor(idSensor);

        if (curs!=null){
            while (curs.moveToNext()){
                Notify tmpNotify=new Notify(context,curs.getInt(1));
                resultList.add(tmpNotify);
            }
            curs.close();
        }


         dbManager.close();
         return  resultList;
    }

    public  static  Collection<Notify> getFollowingNotify(Context context,int idSensor){

        Collection<Notify> resultList=new ArrayList<Notify>();
        DatabaseManager dbManager=new DatabaseManager(context);
        Cursor curs= dbManager.getNotifyByIdSensor(idSensor);
        Category tmpCategory;
        if (curs!=null){
            while (curs.moveToNext()){
                Notify tmpNotify=new Notify(context,curs.getInt(1));
                tmpCategory=new Category(context,tmpNotify.getIdCat(),tmpNotify.getIdSensor());
                if(tmpCategory.getIsFollow()) {
                    resultList.add(tmpNotify);
                }
            }
            curs.close();
        }

        if(curs!=null && !curs.isClosed()) curs.close();
        dbManager.close();
        return  resultList;
    }

    public  static void  updateNotify(Context context,JSONArray notify){

        DatabaseManager dbManager=new DatabaseManager(context);
        dbManager.updateLocalNotify(notify);
        dbManager.close();


    }




}
