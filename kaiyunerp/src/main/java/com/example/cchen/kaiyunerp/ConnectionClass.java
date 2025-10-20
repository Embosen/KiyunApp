package com.example.cchen.kaiyunerp;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by cchen on 2016/5/29.
 */

public class ConnectionClass {
    String ip = "192.168.0.10";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "TestDb";
    String un = "kaiyun";
    String password = "kaiyun123";

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
//            conn = DriverManager.getConnection(ConnURL);
            conn = DriverManager.getConnection(
                    "jdbc:jtds:sqlserver://192.168.0.10:1433/"+db, un,
                    password);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("cchen", e.getMessage());
        }
        return conn;
    }

}
