/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager;


import static junit.framework.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.PersistentExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest {
    private  ExpenseManager expenseManager;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        expenseManager = new PersistentExpenseManager(context);
    }

    @Test
    public void testAddAccount() {
        expenseManager.addAccount("1998","BOC","Vinul",100.0);
        List<String> accountNumbers = expenseManager.getAccountNumbersList();
        assertTrue(accountNumbers.contains("1998"));

    }

    @Test
    public void testRemoveAccount(){
        expenseManager.addAccount("1999","BOC","Hasanja",100.0);
        try {
            expenseManager.getAccountsDAO().removeAccount("1999");
            List<String> accountNumbers = expenseManager.getAccountNumbersList();
            assertTrue(!(accountNumbers.contains("1999")));
        } catch (InvalidAccountException e) {
            assertTrue(false);
        }

    }

    @Test
    public void testUpdateBalanceIncome(){
        double balance= 100.00 ;
        expenseManager.addAccount("2000","BOC","Hasanja",balance);
        try {
            expenseManager.updateAccountBalance("2000",15,5,2022, ExpenseType.INCOME,"300");
            assertTrue(expenseManager.getAccountsDAO().getAccount("2000").getBalance()== balance+ 300);
        } catch (InvalidAccountException e) {
            assertTrue(false);
        }

    }

    @Test
    public void testUpdateBalanceExpense(){
        double balance= 100.00 ;
        expenseManager.addAccount("2000","BOC","Hasanja",balance);
        try {
            expenseManager.updateAccountBalance("2000",15,5,2022, ExpenseType.EXPENSE,"25");
            assertTrue(expenseManager.getAccountsDAO().getAccount("2000").getBalance()== balance- 25);
        } catch (InvalidAccountException e) {
            assertTrue(false);
        }

    }

    @Test
    public void testLogTransaction(){
        boolean s= false;
        Date date=new Date();
        TransactionDAO transactionDAO= expenseManager.getTransactionsDAO();
        expenseManager.addAccount("2000","BOC","Hasanja",2000.00);
        transactionDAO.logTransaction(date,"2000",ExpenseType.EXPENSE,599.00);
        List<Transaction> transactionsList = transactionDAO.getPaginatedTransactionLogs(30);
        for(Transaction tr : transactionsList){
            if((tr.getDate()== date && tr.getAccountNo()=="2000" && tr.getExpenseType()==ExpenseType.EXPENSE && tr.getAmount()==599.00)){
                s = true;
                break;
            }
        }
        assertTrue(s);
    }


}