package database;


import android.content.Context;
import android.database.Cursor;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;

public class Category {

    private  DatabaseManager dbManager;

    public  Category(Context context){

        this.dbManager=new DatabaseManager(context);

    }

    public Category(Context context,int idCat, int idSensor) {
        this.dbManager=new DatabaseManager(context);
        this.idCat = idCat;
        this.idSensor = idSensor;


        Cursor curs = dbManager.getCategory(idCat,idSensor);

        if(curs!=null &&curs.getCount()>0){
            curs.moveToNext();
            this.id=curs.getInt(0);
            this.titleSensor=curs.getString(1);
            this.nameCat=curs.getString(2);
            this.idCat=curs.getInt(3);
            this.idSensor=curs.getInt(4);
            this.isFollow= ((curs.getInt(5)==1) ? true : false);

            curs.close();

        }
        if(curs!=null && !curs.isClosed()) curs.close();
        dbManager.close();

        //dbManager.close();

    }

    public  static Collection<Category> getCategory(Context context){

        Collection<Category> resultList=new ArrayList<Category>();
        DatabaseManager dbManager=new DatabaseManager(context);
        Cursor curs= dbManager.getCategories();

        if (curs!=null && curs.getCount()>0){
            while (curs.moveToNext()){

                Category tmpCategory = new Category(context);
                tmpCategory.id=curs.getInt(0);
                tmpCategory.titleSensor=curs.getString(1);
                tmpCategory.nameCat=curs.getString(2);
                tmpCategory.idCat=curs.getInt(3);
                tmpCategory.idSensor=curs.getInt(4);
                tmpCategory.isFollow=(curs.getInt(5)==1 ? true : false);

                resultList.add(tmpCategory);

            }
            curs.close();
        }
        if(curs!=null && !curs.isClosed()) curs.close();
        dbManager.close();
        return  resultList;
    }



    public  static Collection<Category> getCategory(Context context,int idSensor,boolean followingFlag){

        Collection<Category> resultList=new ArrayList<Category>();
        DatabaseManager dbManager=new DatabaseManager(context);
        Cursor curs= dbManager.getCategories(idSensor);

        if (curs!=null && curs.getCount()>0){
            while (curs.moveToNext()){

                Category tmpCategory = new Category(context);
                tmpCategory.id=curs.getInt(0);
                tmpCategory.titleSensor=curs.getString(1);
                tmpCategory.nameCat=curs.getString(2);
                tmpCategory.idCat=curs.getInt(3);
                tmpCategory.idSensor=curs.getInt(4);
                tmpCategory.isFollow=(curs.getInt(5)==1 ? true : false);
                if(followingFlag){
                    if (tmpCategory.getIsFollow()){
                        resultList.add(tmpCategory);
                    }
                 }else {
                    resultList.add(tmpCategory);
                }


            }

        }
        if(curs!=null && !curs.isClosed()) curs.close();
        dbManager.close();
        return  resultList;
    }



    public  static void updateCategories(Context context,JSONArray categories){
        DatabaseManager dbManager=new DatabaseManager(context);
       dbManager.updateLocalCategories(categories);
        dbManager.close();
    }



    public int getId() {
        return id;
    }

    public int getIdCat() {
        return idCat;
    }


    public int getIdSensor() {
        return idSensor;
    }


    public String getTitleSensor() {
        return titleSensor;
    }

    public String getNameCat() {
        return nameCat;
    }
    public boolean  getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(boolean isFollow) {
        int result=dbManager.setIsFollow(this.idSensor,this.idCat,isFollow);

        if(result==1) this.isFollow=isFollow;
    }



    private int id;
    private  int idCat;
    private int idSensor;
    private String titleSensor;
    private  String nameCat;
    private  boolean isFollow;

}
