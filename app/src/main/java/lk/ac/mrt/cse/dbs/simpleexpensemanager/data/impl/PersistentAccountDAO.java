package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private DataBase database;
    private final Map<String, Account> accounts;

    public PersistentAccountDAO(DataBase database){
        this.database = database;
        this.accounts = new HashMap<>();

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Account",null);
        while (cursor.moveToNext()){
            accounts.put(cursor.getString(0), new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2),cursor.getDouble(3) ));
        }
        cursor.close();
    }

    @Override
    public List<String> getAccountNumbersList() {
        return new ArrayList<>(accounts.keySet());
    }

    @Override
    public List<Account> getAccountsList() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        if (accounts.containsKey(accountNo)) {
            return accounts.get(accountNo);
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        accounts.put(account.getAccountNo(),account);
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("accountNo",account.getAccountNo());
        contentValues.put("bank",account.getBankName());
        contentValues.put("accountHolder",account.getAccountHolderName());
        contentValues.put("balance",account.getBalance());
        long value = db.insert("Account",null,contentValues);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        accounts.remove(accountNo);

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Account where accountNo = ?",new String[]{accountNo});
        if (cursor.getCount()>0){
            db.delete("Account","accountNo=?",new String[]{accountNo});
            return;
        }
        cursor.close();


    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        Account account = accounts.get(accountNo);
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        accounts.put(accountNo, account);


        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from Account where accountNo=?",new String[]{accountNo});
        if(cursor.getCount()>0 && cursor.moveToFirst()) {
            double balance = cursor.getDouble(3);
            switch (expenseType) {
                case EXPENSE:
                    balance -= amount;
                    break;
                case INCOME:
                    balance += amount;
                    break;
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put("balance", balance);
            db.update("Account", contentValues, "accountNo=?", new String[]{accountNo});
            return;
        }

        cursor.close();

    }
}
