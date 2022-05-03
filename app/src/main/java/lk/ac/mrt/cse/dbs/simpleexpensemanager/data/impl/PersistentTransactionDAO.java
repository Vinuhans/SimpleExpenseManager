package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType.EXPENSE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType.INCOME;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private DataBase database ;
    private final List<Transaction> transactions;

    public PersistentTransactionDAO(DataBase database){
        this.database=database;
        transactions=new LinkedList<>();

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT * FROM Log",null);
        while (cursor.moveToNext()){
            ExpenseType expenseType;
            switch (cursor.getInt(3)){
                case(0):
                    expenseType =INCOME;
                    break;
                case(1):
                    expenseType = EXPENSE;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + cursor.getInt(3));
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date=new Date();
            try{
                date = dateFormat.parse(cursor.getString(1));


            } catch (ParseException e) {
                e.printStackTrace();
            }
            transactions.add(new Transaction(date,cursor.getString(2),expenseType, cursor.getDouble(4)));
        }
        cursor.close();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        transactions.add(transaction);

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Account WHERE accountNo=?",new String[]{accountNo});
        if(cursor.getCount()>0){
            ContentValues contentValues= new ContentValues();
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
            String fo_date = dateFormat.format(date);
            int type;
            if (expenseType==INCOME){
               type = 0;
            }
            else{
                type= 1;
            }
            contentValues.put("accountNo",accountNo);
            contentValues.put("date",fo_date);
            contentValues.put("expenseType",type);
            contentValues.put("amount",amount);
            db.insert("Log",null,contentValues);
            return;
        }
        cursor.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }


}
