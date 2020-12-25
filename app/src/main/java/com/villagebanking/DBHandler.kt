package com.villagebanking

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import kotlinx.android.synthetic.main.account_details.*
import kotlinx.android.synthetic.main.dialog_payments.view.*
import kotlin.Exception
import kotlin.collections.ArrayList



class DBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int):
        SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "village_banking.db"
        private const val DATABASE_VERSION = 1

        const val ACCOUNT_HOLDERS_TABLE = "account_holders"
        const val ACCOUNT_HOLDERS_ID_COL = "_id"
        const val ACCOUNT_HOLDERS_NAME_COL = "name"
        const val ACCOUNT_HOLDERS_ADMIN_COL = "admin"
        const val ACCOUNT_HOLDERS_PIN_COL = "pin"
        const val ACCOUNT_HOLDERS_PIN_HINT_COL = "pin_hint"
        const val ACCOUNT_HOLDERS_CONTACT_COL = "contact_no"
        const val ACCOUNT_HOLDERS_BANK_INFO_COL = "account_info"
        const val ACCOUNT_HOLDERS_SHARE_COL = "pre_share"
        const val ACCOUNT_HOLDERS_LOAN_APP_COL = "loan_app"
        const val ACCOUNT_HOLDERS_CHARGES_COL = "charges"

        const val TRANSACTION_TABLE = "transactions"
        const val TRANSACTION_ID_COL = "_id"
        const val TRANSACTION_NAME_COL = "tr_name"
        const val TRANSACTION_MONTH_COL = "month"
        const val TRANSACTION_SHARE_COL = "share"
        const val TRANSACTION_SHARE_AMOUNT_COL = "share_amount"
        const val TRANSACTION_SHARE_PAYMENT_COL = "share_payment"
        const val TRANSACTION_SHARE_DATE_COL = "share_date"
        const val TRANSACTION_LOAN_APP_COL = "loan"
        const val TRANSACTION_LOAN_PAYMENT_COL = "loan_payment"
        const val TRANSACTION_LOAN_PAYMENT_DATE_COL = "loan_payment_date"
        const val TRANSACTION_LOAN_TO_REPAY_COL = "loan_to_repay_date"
        const val TRANSACTION_LOAN_REPAYMENT_COL = "loan_repayment"
        const val TRANSACTION_LOAN_REPAYMENT_DATE_COL = "loan_repayment_date"
        const val TRANSACTION_CHARGE_NAME_COL = "charge_name"
        const val TRANSACTION_CHARGE_COL = "charge"
        const val TRANSACTION_CHARGE_PAYMENT_COL = "charge_payment"
        const val TRANSACTION_CHARGE_DATE_COL = "charge_date"
        const val TRANSACTION_SHARE_OUT_COL = "current_share_out"
        const val TRANSACTION_INTEREST_COL = "interest"

        const val SETTINGS_TABLE = "settings"
        const val SETTINGS_ID_COL = "_id"
        const val SETTINGS_SHARE_VALUE_COL = "share_value"
        const val SETTINGS_INTEREST_RATE_COL = "interest_rate"
        const val SETTINGS_NOTES_COL = "notes"



    }



    override fun onCreate(db: SQLiteDatabase?) {
        val createAccountHoldersTable = ("CREATE TABLE $ACCOUNT_HOLDERS_TABLE (" +
                "$ACCOUNT_HOLDERS_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$ACCOUNT_HOLDERS_NAME_COL TEXT, " +
                "$ACCOUNT_HOLDERS_ADMIN_COL TEXT, " +
                "$ACCOUNT_HOLDERS_PIN_COL TEXT, " +
                "$ACCOUNT_HOLDERS_PIN_HINT_COL TEXT, " +
                "$ACCOUNT_HOLDERS_CONTACT_COL TEXT, " +
                "$ACCOUNT_HOLDERS_BANK_INFO_COL TEXT, " +
                "$ACCOUNT_HOLDERS_SHARE_COL DOUBLE, " +
                "$ACCOUNT_HOLDERS_LOAN_APP_COL DOUBLE, " +
                "$ACCOUNT_HOLDERS_CHARGES_COL DOUBLE)")


        val createTransactionTable = ("CREATE TABLE $TRANSACTION_TABLE (" +
                "$TRANSACTION_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$TRANSACTION_NAME_COL TEXT, " +
                "$TRANSACTION_MONTH_COL DATE, " +
                "$TRANSACTION_SHARE_COL DOUBLE, " +
                "$TRANSACTION_SHARE_AMOUNT_COL DOUBLE, " +
                "$TRANSACTION_SHARE_PAYMENT_COL DOUBLE, " +
                "$TRANSACTION_SHARE_DATE_COL DATE, " +
                "$TRANSACTION_LOAN_APP_COL DOUBLE, " +
                "$TRANSACTION_LOAN_PAYMENT_COL DOUBLE, " +
                "$TRANSACTION_LOAN_PAYMENT_DATE_COL DATE, " +
                "$TRANSACTION_LOAN_TO_REPAY_COL DOUBLE, " +
                "$TRANSACTION_LOAN_REPAYMENT_COL DOUBLE, " +
                "$TRANSACTION_LOAN_REPAYMENT_DATE_COL DATE, " +
                "$TRANSACTION_CHARGE_NAME_COL TEXT, " +
                "$TRANSACTION_CHARGE_COL DOUBLE, " +
                "$TRANSACTION_CHARGE_PAYMENT_COL DOUBLE, " +
                "$TRANSACTION_CHARGE_DATE_COL DATE, " +
                "$TRANSACTION_INTEREST_COL DOUBLE, " +
                "$TRANSACTION_SHARE_OUT_COL DOUBLE)")

        val createSettingsTable = ("CREATE TABLE $SETTINGS_TABLE (" +
                "$SETTINGS_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$SETTINGS_SHARE_VALUE_COL DOUBLE, " +
                "$SETTINGS_INTEREST_RATE_COL PERCENTAGE, " +
                "$SETTINGS_NOTES_COL TEXT)")

        db?.execSQL(createAccountHoldersTable)
        db?.execSQL(createTransactionTable)
        db?.execSQL(createSettingsTable)
    }



    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $ACCOUNT_HOLDERS_TABLE;")
        db?.execSQL("DROP TABLE IF EXISTS $TRANSACTION_TABLE;")
        db?.execSQL("DROP TABLE IF EXISTS $SETTINGS_TABLE;")
        onCreate(db)
    }



    fun getAccountHolders(mContext: Context): ArrayList<Model>{
        val query = "SELECT * FROM $ACCOUNT_HOLDERS_TABLE"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val accountHolderModel = ArrayList<Model>()
        if(cursor.count == 0)
            Toast.makeText(mContext, "Create Accounts Here", Toast.LENGTH_SHORT).show() else
        {while (cursor.moveToNext()){
            val accountHolders = Model()
            accountHolders.accountHoldersID = cursor.getInt(cursor.getColumnIndex(ACCOUNT_HOLDERS_ID_COL))
            accountHolders.accountHoldersName = cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDERS_NAME_COL))
            accountHolders.accountHoldersAdmin = cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDERS_ADMIN_COL))
            accountHolders.accountHoldersShare = cursor.getDouble(cursor.getColumnIndex(ACCOUNT_HOLDERS_SHARE_COL))
            accountHolders.accountHoldersLoanApp = cursor.getDouble(cursor.getColumnIndex(ACCOUNT_HOLDERS_LOAN_APP_COL))
            accountHolders.accountHolderBankInfo = cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDERS_BANK_INFO_COL))
            accountHolders.accountHolderContact = cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDERS_CONTACT_COL))
            accountHolders.accountHoldersCharges = cursor.getDouble(cursor.getColumnIndex(ACCOUNT_HOLDERS_CHARGES_COL))
            accountHolderModel.add(accountHolders)
        }
        }
            cursor.close()
            db.close()
                return accountHolderModel
    }



    fun getAccountAdmins(mContext: Context): ArrayList<Model>{
        val query = "SELECT * FROM $ACCOUNT_HOLDERS_TABLE WHERE $ACCOUNT_HOLDERS_ADMIN_COL != ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, arrayOf("Account Holder"))
        val accountHolderModel = ArrayList<Model>()
        if(cursor.count == 0)
            Toast.makeText(mContext, "No Admins Found", Toast.LENGTH_SHORT).show() else
        {while (cursor.moveToNext()){
            val accountAdmins = Model()
            accountAdmins.accountHoldersID = cursor.getInt(cursor.getColumnIndex(ACCOUNT_HOLDERS_ID_COL))
            accountAdmins.accountHoldersName = cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDERS_NAME_COL))
            accountAdmins.accountHoldersAdmin = cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDERS_ADMIN_COL))
            accountAdmins.accountHoldersShare = cursor.getDouble(cursor.getColumnIndex(ACCOUNT_HOLDERS_SHARE_COL))
            accountAdmins.accountHoldersLoanApp = cursor.getDouble(cursor.getColumnIndex(ACCOUNT_HOLDERS_LOAN_APP_COL))
            accountAdmins.accountHolderBankInfo = cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDERS_BANK_INFO_COL))
            accountAdmins.accountHolderContact = cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDERS_CONTACT_COL))
            accountAdmins.accountHoldersCharges = cursor.getDouble(cursor.getColumnIndex(ACCOUNT_HOLDERS_CHARGES_COL))
            accountHolderModel.add(accountAdmins)
        }
        }
        cursor.close()
        db.close()
        return accountHolderModel
    }



    fun addAccountHolder (mContext: Context, accountHolderModel: Model){
        val name = accountHolderModel.accountHoldersName

        //Check for duplicate Name
        var query = "SELECT * FROM $ACCOUNT_HOLDERS_TABLE WHERE $ACCOUNT_HOLDERS_NAME_COL = '$name'"
        var db = this.readableDatabase
        var cursor = db.rawQuery(query, null)
        if (cursor.count == 1){
            val duplicateName = AlertDialog.Builder(mContext)
                    .setTitle("Duplicate Name")
                    .setMessage("$name already exists. Ensure names are unique")
                    .setNegativeButton("Ok") { _: DialogInterface, _: Int ->
                    }
            duplicateName.show()
            return
        }


        //Check for duplicate Chairperson account
        val admin = accountHolderModel.accountHoldersAdmin

        if (admin !== "Account Holder") {

            query = "SELECT * FROM $ACCOUNT_HOLDERS_TABLE WHERE $ACCOUNT_HOLDERS_ADMIN_COL = '$admin'"
            db = this.readableDatabase
            cursor = db.rawQuery(query, null)

            if (cursor.count > 0) {
                val duplicateAdmin = AlertDialog.Builder(mContext)
                        .setTitle("Duplicate Admin")
                        .setMessage("You can only have one $admin")
                        .setNegativeButton("Ok") { _: DialogInterface, _: Int ->
                        }
                duplicateAdmin.show()
                return
            }
            cursor.close()
            db.close()
        }

        val contentValues = ContentValues()

        contentValues.put(ACCOUNT_HOLDERS_NAME_COL, accountHolderModel.accountHoldersName)
        contentValues.put(ACCOUNT_HOLDERS_ADMIN_COL, accountHolderModel.accountHoldersAdmin)
        contentValues.put(ACCOUNT_HOLDERS_CONTACT_COL, accountHolderModel.accountHolderContact)
        contentValues.put(ACCOUNT_HOLDERS_BANK_INFO_COL, accountHolderModel.accountHolderBankInfo)
        contentValues.put(ACCOUNT_HOLDERS_PIN_COL, accountHolderModel.accountHolderPin)
        contentValues.put(ACCOUNT_HOLDERS_PIN_HINT_COL, accountHolderModel.accountHolderPinHint)
        contentValues.put(ACCOUNT_HOLDERS_SHARE_COL, accountHolderModel.accountHoldersShare)
        contentValues.put(ACCOUNT_HOLDERS_LOAN_APP_COL, accountHolderModel.accountHoldersLoanApp)
        contentValues.put(ACCOUNT_HOLDERS_CHARGES_COL, accountHolderModel.accountHoldersCharges)

       db = this.writableDatabase
        try {
            db.insert(ACCOUNT_HOLDERS_TABLE, null, contentValues)
            Toast.makeText(mContext, "Account Creation Successful",Toast.LENGTH_SHORT).show()
        } catch (e : Exception){
            Toast.makeText(mContext, e.message,Toast.LENGTH_SHORT).show()
        }
            db.close()
    }



    fun editAccountHolder(mContext: Context, AccountID: Int, Name: String, Admin: String, ContactNo: String, AccountInfo: String, PIN: String, Hint: String) : Boolean {
        var result: Boolean
        val contentValues = ContentValues()
        contentValues.put(ACCOUNT_HOLDERS_NAME_COL, Name)
        contentValues.put(ACCOUNT_HOLDERS_ADMIN_COL, Admin)
        contentValues.put(ACCOUNT_HOLDERS_CONTACT_COL, ContactNo)
        contentValues.put(ACCOUNT_HOLDERS_BANK_INFO_COL, AccountInfo)
        contentValues.put(ACCOUNT_HOLDERS_PIN_COL, PIN)
        contentValues.put(ACCOUNT_HOLDERS_PIN_HINT_COL, Hint)
        val db = this.writableDatabase
        try {
            db.update(ACCOUNT_HOLDERS_TABLE, contentValues, "$ACCOUNT_HOLDERS_ID_COL = ?", arrayOf(AccountID.toString()))
            result = true
            Toast.makeText(mContext, "Update successful",Toast.LENGTH_SHORT).show()
        } catch (e : Exception){
            Toast.makeText(mContext, e.message,Toast.LENGTH_SHORT).show()
            result = false
        }
        db.close()
        return result
    }




    fun delAccountHolder(AccountID : Int): Boolean {
        val db = writableDatabase
        var result : Boolean = false
        try{
            db.delete(ACCOUNT_HOLDERS_TABLE, "$ACCOUNT_HOLDERS_ID_COL = ?", arrayOf(AccountID.toString()))
            result = true
        }catch (e : Exception){
            Log.e(ContentValues.TAG, "Something wrong. Cannot Delete")
        }
        db.close()
        return result
    }



    fun delAccountHolderTransactions(Name: String): Boolean {
        val db = writableDatabase
        var result : Boolean = false
        try{
            db.delete(TRANSACTION_TABLE, "$TRANSACTION_NAME_COL = ?", arrayOf(Name))
            result = true
        }catch (e : Exception){
            Log.e(ContentValues.TAG, "Something wrong. Cannot Delete")
        }
        db.close()
        return result
    }



    fun delAllAccountHolders(mContext: Context){
        val db = this.writableDatabase
        db.delete(ACCOUNT_HOLDERS_TABLE, null, null)
        db.delete(TRANSACTION_TABLE, null,null)
        db.close()
    }



    fun postings(mContext: Context, AccountID: Int, shares: String, loanApplication: String, charges: String) : Boolean{
        var result: Boolean
        val contentValues = ContentValues()
        contentValues.put(ACCOUNT_HOLDERS_SHARE_COL, shares)
        contentValues.put(ACCOUNT_HOLDERS_LOAN_APP_COL, loanApplication)
        contentValues.put(ACCOUNT_HOLDERS_CHARGES_COL, charges)
        val db = writableDatabase
        try {
            db.update(ACCOUNT_HOLDERS_TABLE, contentValues, "$ACCOUNT_HOLDERS_ID_COL = ?", arrayOf(AccountID.toString()))
            result = true
            Toast.makeText(mContext, "Application Successful", Toast.LENGTH_SHORT).show()
        }catch (e : Exception){
            Toast.makeText(mContext, e.message, Toast.LENGTH_SHORT).show()
            result = false
        }
        db.close()
        return result
    }



    fun createMonth(mContext: Context, transactionModel: Model){
            val contentValues = ContentValues()
            contentValues.put(TRANSACTION_NAME_COL, transactionModel.transactionName)
            contentValues.put(TRANSACTION_MONTH_COL, transactionModel.transactionMonth)
            contentValues.put(TRANSACTION_SHARE_COL, transactionModel.transactionShares)
            contentValues.put(TRANSACTION_SHARE_AMOUNT_COL, transactionModel.transactionShareAmount)
            contentValues.put(TRANSACTION_SHARE_PAYMENT_COL, transactionModel.transactionSharePayment)
            contentValues.put(TRANSACTION_SHARE_DATE_COL, transactionModel.transactionShareDate)
            contentValues.put(TRANSACTION_LOAN_APP_COL, transactionModel.transactionLoanApp)
            contentValues.put(TRANSACTION_LOAN_PAYMENT_COL, transactionModel.transactionLoanPayment)
            contentValues.put(TRANSACTION_LOAN_PAYMENT_DATE_COL, transactionModel.transactionLoanPaymentDate)
            contentValues.put(TRANSACTION_LOAN_TO_REPAY_COL, transactionModel.transactionLoanToRepay)
            contentValues.put(TRANSACTION_LOAN_REPAYMENT_COL, transactionModel.transactionLoanRepayment)
            contentValues.put(TRANSACTION_LOAN_REPAYMENT_DATE_COL, transactionModel.transactionLoanRepaymentDate)
            contentValues.put(TRANSACTION_CHARGE_COL, transactionModel.transactionCharge)
            contentValues.put(TRANSACTION_CHARGE_PAYMENT_COL, transactionModel.transactionChargePayment)
            contentValues.put(TRANSACTION_CHARGE_DATE_COL, transactionModel.transactionChargePaymentDate)
            contentValues.put(TRANSACTION_SHARE_OUT_COL, transactionModel.transactionShareOut)
            contentValues.put(TRANSACTION_INTEREST_COL, transactionModel.transactionInterest)
            val db = writableDatabase
            try {
                db.insert(TRANSACTION_TABLE, null, contentValues)

            }catch (e : Exception){
                Toast.makeText(mContext, e.message,Toast.LENGTH_SHORT).show()
            }
        db.close()
    }



    fun updateMonth(mContext: Context, transactionModel: Model, Name: String){
        val contentValues = ContentValues()
        //contentValues.put(TRANSACTION_NAME_COL, transactionModel.transactionName)
        //contentValues.put(TRANSACTION_MONTH_COL, transactionModel.transactionMonth)
        contentValues.put(TRANSACTION_SHARE_COL, transactionModel.transactionShares)
        contentValues.put(TRANSACTION_SHARE_AMOUNT_COL, transactionModel.transactionShareAmount)
        //contentValues.put(TRANSACTION_SHARE_PAYMENT_COL, transactionModel.transactionSharePayment)
        contentValues.put(TRANSACTION_LOAN_APP_COL, transactionModel.transactionLoanApp)
        //contentValues.put(TRANSACTION_LOAN_PAYMENT_COL, transactionModel.transactionLoanPayment)
       // contentValues.put(TRANSACTION_LOAN_REPAYMENT_COL, transactionModel.transactionLoanRepayment)
        contentValues.put(TRANSACTION_CHARGE_COL, transactionModel.transactionCharge)
        //contentValues.put(TRANSACTION_CHARGE_PAYMENT_COL, transactionModel.transactionChargePayment)
        val db = writableDatabase
        try {
            db.update(TRANSACTION_TABLE, contentValues, "$TRANSACTION_NAME_COL = '$Name' AND $TRANSACTION_MONTH_COL = '${AccountDetails().transactionMonth}'", arrayOf())
        }catch (e : Exception){
            Toast.makeText(mContext, e.message,Toast.LENGTH_SHORT).show()
        }
        db.close()
    }




    fun sharePayment(mContext: Context, TransactionID: Int, Payment: String, Date: String) : Boolean{
        val result: Boolean

        val contentValues = ContentValues()

        contentValues.put(TRANSACTION_SHARE_PAYMENT_COL, Payment)
        contentValues.put(TRANSACTION_SHARE_DATE_COL, Date)
        val db = writableDatabase
        result = try {
            db.update(TRANSACTION_TABLE, contentValues, "$TRANSACTION_ID_COL = ?", arrayOf(TransactionID.toString()))
            true
        }catch (e : Exception){
            Toast.makeText(mContext, e.message, Toast.LENGTH_SHORT).show()
            false
        }
        db.close()
        return result
    }




    fun loanPayout(mContext: Context, TransactionID: Int, Payment: String, Date: String) : Boolean{
        val result: Boolean

        val contentValues = ContentValues()

        contentValues.put(TRANSACTION_LOAN_PAYMENT_COL, Payment)
        contentValues.put(TRANSACTION_LOAN_PAYMENT_DATE_COL, Date)
        val db = writableDatabase
        result = try {
            db.update(TRANSACTION_TABLE, contentValues, "$TRANSACTION_ID_COL = ?", arrayOf(TransactionID.toString()))
            true
        }catch (e : Exception){
            Toast.makeText(mContext, e.message, Toast.LENGTH_SHORT).show()
            false
        }
        db.close()
        return result
    }



    fun loanRepayment(mContext: Context, TransactionID: Int, Payment: String, Date: String) : Boolean{
        val result: Boolean

        val contentValues = ContentValues()

        contentValues.put(TRANSACTION_LOAN_REPAYMENT_COL, Payment)
        contentValues.put(TRANSACTION_LOAN_REPAYMENT_DATE_COL, Date)
        val db = writableDatabase
        result = try {
            db.update(TRANSACTION_TABLE, contentValues, "$TRANSACTION_ID_COL = ?", arrayOf(TransactionID.toString()))
            true
        }catch (e : Exception){
            Toast.makeText(mContext, e.message, Toast.LENGTH_SHORT).show()
            false
        }
        db.close()
        return result
    }




    fun chargePayment(mContext: Context, TransactionID: Int, Payment: String, Date: String) : Boolean{
        val result: Boolean

        val contentValues = ContentValues()

        contentValues.put(TRANSACTION_CHARGE_PAYMENT_COL, Payment)
        contentValues.put(TRANSACTION_CHARGE_DATE_COL, Date)
        val db = writableDatabase
        result = try {
            db.update(TRANSACTION_TABLE, contentValues, "$TRANSACTION_ID_COL = ?", arrayOf(TransactionID.toString()))
            true
        }catch (e : Exception){
            Toast.makeText(mContext, e.message, Toast.LENGTH_SHORT).show()
            false
        }
        db.close()
        return result
    }



    fun investment(mContext: Context, Name: String) : Boolean{
        val result: Boolean
        val transactions = Model()
        val contentValues = ContentValues()
        contentValues.put(TRANSACTION_SHARE_OUT_COL,  transactions.transactionShareOut)
        val db = writableDatabase
        result = try {
            db.update(TRANSACTION_TABLE, contentValues, "$TRANSACTION_NAME_COL = ?", arrayOf(Name))
            true
        }catch (e : Exception){
            Toast.makeText(mContext, e.message, Toast.LENGTH_SHORT).show()
            false
        }
        db.close()
        return result
    }






}