package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DataBase;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

public class PersistentExpenseManager extends  ExpenseManager{

    private Context context;
    public  PersistentExpenseManager(Context context){
        this.context=context;
        setup();
    }

    @Override
    public void  setup(){
        DataBase dataBase=new DataBase(context);

        AccountDAO persistentAcc = new PersistentAccountDAO(dataBase);
        setAccountsDAO(persistentAcc);

        TransactionDAO persistentTran = new PersistentTransactionDAO(dataBase);
        setTransactionsDAO(persistentTran);
    }
}
