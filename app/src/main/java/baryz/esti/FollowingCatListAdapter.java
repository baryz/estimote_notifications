package baryz.esti;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.estimote.sdk.Beacon;

import java.util.List;

import database.Category;
import database.Notify;

/**
 * Created by user on 2015-02-18.
 */
public class FollowingCatListAdapter extends ArrayAdapter<Category> {

    private List<Category> list;
    private  final Activity context;

    public FollowingCatListAdapter(Activity context,List<Category> list){

        super(context,R.layout.following_categories_item,list);
        this.context=context;
        this.list=list;

    }
    static  class ViewHolder{

        protected TextView followingCatLabelTextView;
        protected CheckBox isFollowingCatCheckBox;


    }
    @Override
    public Category getItem(int position) {
        if (list.size()>0)
            return list.get(position);
        else return  null;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){

        View view=null;
        if(convertView==null){
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.following_categories_item,null);
            final  ViewHolder viewHolder=new ViewHolder();
            viewHolder.followingCatLabelTextView=(TextView) view.findViewById(R.id.followingCatLabelTextView);
            viewHolder.isFollowingCatCheckBox= (CheckBox) view.findViewById(R.id.followingCatCheckBox);
            viewHolder.isFollowingCatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Category cat= (Category) viewHolder.isFollowingCatCheckBox.getTag();
                    cat.setIsFollow(buttonView.isChecked());
                };
            });


            view.setTag(viewHolder);
            viewHolder.isFollowingCatCheckBox.setTag(list.get(position));
        }else{
            view=convertView;
            ((ViewHolder)view.getTag()).isFollowingCatCheckBox.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.isFollowingCatCheckBox.setText(list.get(position).getNameCat());
        holder.isFollowingCatCheckBox.setChecked(list.get(position).getIsFollow());
        holder.followingCatLabelTextView.setText(list.get(position).getTitleSensor());
       // holder.titleSensorTextView.setText(list.get(position).getTitleSensor());

        return  view;
    }

}
