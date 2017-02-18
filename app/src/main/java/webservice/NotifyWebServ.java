package webservice;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import database.DatabaseManager;
import database.Notify;

public class NotifyWebServ extends AsyncTask<Void,Void,Void> {

    public HttpGet httpget=null;
    public InputStream Input = null;
    public  String Result = "";

    private Context context;

    public NotifyWebServ(String serviceName, Context context){
        this.context =context;
        httpget = new HttpGet(serviceName);

    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            Input = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Input, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            Input.close();

            this.Result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }
        //parse json data
        try {

            JSONArray jArray = new JSONArray(Result);
            Notify.updateNotify(context,jArray);


        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return null;
    }
}
