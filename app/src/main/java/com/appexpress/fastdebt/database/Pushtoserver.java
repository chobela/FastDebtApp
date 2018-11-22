package com.appexpress.fastdebt.database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appexpress.fastdebt.app.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Pushtoserver extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private DatabaseHelper db;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new DatabaseHelper(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                Cursor cursor = db.getalldata();
                if (cursor.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced data to MySQL
                        saveData(
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.UID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.TOWN)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADDRESS)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.AMOUNT)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COMMENT))
                        );
                    } while (cursor.moveToNext());
                }
            }
        }
    }

    /*
     * method taking two arguments
     * name that is to be saved and id of the name from SQLite
     * if the name is successfully sent
     * we will update the status as synced in SQLite
     * */
    public void saveData(final int id, final String uid, final String name, final String town, final String address, final String amount, final String docid, final String comment) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://fastdebt.pe.hu/form/save.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                //db.delete();

                                //sending the broadcast to refresh the list
                                //context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("name", name);
                params.put("town", town);
                params.put("address", address);
                params.put("amount", amount);
                params.put("docid", docid);
                params.put("comment", comment);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

}
