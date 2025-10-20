package com.example.cchen.kaiyunerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by cchen on 2016/5/29.
 */
public class DoLogin extends AsyncTask<String, String, String> {
    private static final String TABLE_NAME = "table1";
    String z = "";
    Boolean isSuccess = false;

    @Override
    protected void onPreExecute() {
    }


    @Override
    protected String doInBackground(String... params) {
        Log.d("cchen", "DoLogin doInBackground " + z);
//        if (params[0].trim().equals("") || params[1].trim().equals(""))
//            z = "Please enter User Id and Password";
//        else {
        try {
            Connection con = new ConnectionClass().CONN();
            if (con == null) {
                z = "Error in connection with SQL server";
            } else {
                String query = "select * from " + TABLE_NAME;//+" where UserId='" + params[0] + "' and password='" + params[0] + "'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next()) {
                    z = "Login successfull";
                    isSuccess = true;
                } else {
                    z = "Invalid Credentials";
                    isSuccess = false;
                }

            }
        } catch (Exception ex) {
            isSuccess = false;
            ex.printStackTrace();
            z = "Exceptions";
        }
//        }
        Log.d("cchen", "z " + z);
        return z;
    }
}
