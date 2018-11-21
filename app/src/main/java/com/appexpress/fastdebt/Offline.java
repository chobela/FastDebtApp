package com.appexpress.fastdebt;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appexpress.fastdebt.Database.DBManager;

public class Offline extends AppCompatActivity {

    TextView tvName, tvTown, tvAddress, tvAmount, tvDocid, tvComment;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        tvName = (TextView) findViewById(R.id.name);
        tvTown = (TextView) findViewById(R.id.town);
        tvAddress = (TextView) findViewById(R.id.address);
        tvAmount = (TextView) findViewById(R.id.amount);
        tvDocid = (TextView) findViewById(R.id.docid);
        tvComment = (TextView) findViewById(R.id.comment);

        dbManager = new DBManager(this);
        dbManager.open();

    }

    public void onClick (View view) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Offline.this);
        String uid = sp.getString("uid", "anonymous");

        final String name = tvName.getText().toString();
        final String town = tvTown.getText().toString();
        final String address = tvAddress.getText().toString();
        final String amount = tvAmount.getText().toString();
        final String docid = tvDocid.getText().toString();
        final String comment = tvComment.getText().toString();

        dbManager.insert(uid, name, town, address, amount, docid, comment);

        tvName.setText("");
        tvTown.setText("");
        tvAddress.setText("");
        tvAmount.setText("");
        tvDocid.setText("");
        tvComment.setText("");

        Toast.makeText(getApplicationContext(),"Saved to database", Toast.LENGTH_SHORT).show();

    }

}
