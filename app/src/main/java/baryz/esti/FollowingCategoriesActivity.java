package baryz.esti;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import database.Category;
import webservice.CategoryWebServ;
import webservice.NotifyWebServ;
import webservice.SensorWebServ;

/**
 * Created by user on 2015-02-18.
 */
public class FollowingCategoriesActivity extends Activity {

    private  ListView followingCatListView;
    private ArrayAdapter<Category> adapter ;


    protected  void onCreate(Bundle savedInstaceState){

        super.onCreate(savedInstaceState);

        setContentView(R.layout.following_categories);
        followingCatListView=(ListView) findViewById(R.id.followingCatListView);
        if(!DialogManager.haveInternet(this)){
            DialogManager.showNoConnectionDialog(this);
        }

        findViewById(R.id.saveFollowingCatButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 int size = followingCatListView.getAdapter().getCount();
                for(int i=0;i<size;i++) {
                    Category obj = (Category) followingCatListView.getAdapter().getItem(i);
                    obj.getId();
                 }
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        String serviceCat=getString(R.string.globalHost)+getString(R.string.getCategories);

        CategoryWebServ wb= new CategoryWebServ(serviceCat,this);
        wb.execute(null, null, null);

        Collection<Category> catCol = Category.getCategory(this);
        List<Category> catList= new ArrayList<Category>(catCol);


        adapter= new FollowingCatListAdapter(this,catList);
        followingCatListView.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();

        String serviceCat=getString(R.string.globalHost)+getString(R.string.getCategories);

        CategoryWebServ wb= new CategoryWebServ(serviceCat,this);
        wb.execute(null,null,null);


        followingCatListView=(ListView) findViewById(R.id.followingCatListView);

        Collection<Category> catCol = Category.getCategory(this);
        List<Category> catList= new ArrayList<Category>(catCol);
        Iterator<Category> itCatList=catList.iterator();

        adapter= new FollowingCatListAdapter(this,catList);
        followingCatListView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        String serviceCat=getString(R.string.globalHost)+getString(R.string.getCategories);

        CategoryWebServ wb= new CategoryWebServ(serviceCat,this);
        wb.execute(null,null,null);


        followingCatListView=(ListView) findViewById(R.id.followingCatListView);

        Collection<Category> catCol = Category.getCategory(this);
        List<Category> catList= new ArrayList<Category>(catCol);


        adapter= new FollowingCatListAdapter(this,catList);
        followingCatListView.setAdapter(adapter);

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
