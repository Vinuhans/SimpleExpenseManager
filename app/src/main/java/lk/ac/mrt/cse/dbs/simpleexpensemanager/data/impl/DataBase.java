package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {

    private static final String DB_NAME = "190177F";

    public DataBase(Context context){
        super(context,DB_NAME,null,1);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("create Table Account(accountNo TEXT primary key,bank TEXT ,accountHolder TEXT,balance DOUBLE)");
        db.execSQL("create Table Log(Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,date DATE,accountNo TEXT,expenseType TINYINT,amount DOUBLE,CONSTRAINT fk_accNo " +
                "FOREIGN KEY(accountNo) REFERENCES Account(accountNo))");
    }

    public void onUpgrade(SQLiteDatabase db,int i,int j){
        db.execSQL("drop Table if exists Log");
        db.execSQL("drop Table if exists Account");
        onCreate(db);
    }

}
