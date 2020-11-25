package com.villagebanking

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.view.LayoutInflater
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_add_account_holder.view.*
import java.lang.Exception
import kotlin.coroutines.coroutineContext

class DBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int):
        SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "village_banking.db"
        private const val DATABASE_VERSION = 1

        val ACCOUNT_HOLDERS_TABLE = "account_holders"

        val ACCOUNT_HOLDERS_ID_COL = "_id"
        val ACCOUNT_HOLDERS_NAME_COL = "name"
        val ACCOUNT_HOLDERS_ADMIN_COL = "admin"
        val ACCOUNT_HOLDERS_CONTACT_COL = "contact_no"
        val ACCOUNT_HOLDERS_ACCOUNT_INFO_COL = "account_info"
        val ACCOUNT_HOLDERS_PRESHARE_COL = "pre_share"
        val ACCOUNT_HOLDERS_LOANAPP_COL = "loan_app"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_ACCOUNT_HOLDERS_TABLE = ("CREATE TABLE $ACCOUNT_HOLDERS_TABLE (" +
                "$ACCOUNT_HOLDERS_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$ACCOUNT_HOLDERS_NAME_COL TEXT, " +
                "$ACCOUNT_HOLDERS_ADMIN_COL TEXT, " +
                "$ACCOUNT_HOLDERS_CONTACT_COL TEXT, " +
                "$ACCOUNT_HOLDERS_ACCOUNT_INFO_COL TEXT, " +
                "$ACCOUNT_HOLDERS_PRESHARE_COL INTEGER, " +
                "$ACCOUNT_HOLDERS_LOANAPP_COL DOUBLE(10,2))")
        db?.execSQL(CREATE_ACCOUNT_HOLDERS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun getAccountHolders(mContext: Context): ArrayList<AccountHolderModel>{
        val query = "SELECT * FROM $ACCOUNT_HOLDERS_TABLE"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val accountHolderModel = ArrayList<AccountHolderModel>()

        if(cursor.count == 0)
            Toast.makeText(mContext, "No Account Holders. Please add account holders to begin", Toast.LENGTH_SHORT).show() else
        {while (cursor.moveToNext()){
            val accountHolders = AccountHolderModel()

            accountHolders.account_holders_name = cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDERS_NAME_COL))
            accountHolders.account_holders_admin = cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDERS_ADMIN_COL))
            accountHolders.account_holders_preshare = cursor.getInt(cursor.getColumnIndex(ACCOUNT_HOLDERS_PRESHARE_COL))
            accountHolders.account_holders_loanapp = cursor.getDouble(cursor.getColumnIndex(ACCOUNT_HOLDERS_LOANAPP_COL))
            accountHolderModel.add(accountHolders)
        }
            Toast.makeText(mContext, "${cursor.count.toString()} Account Holders Found", Toast.LENGTH_SHORT).show()
        }
            cursor.close()
            db.close()
                return accountHolderModel
    }

    fun addAccountHolder (mContext: Context, accountHolderModel: AccountHolderModel){
        val contentValues = ContentValues()
        contentValues.put(ACCOUNT_HOLDERS_NAME_COL, accountHolderModel.account_holders_name)
        contentValues.put(ACCOUNT_HOLDERS_ADMIN_COL, accountHolderModel.account_holders_admin)
        contentValues.put(ACCOUNT_HOLDERS_PRESHARE_COL, accountHolderModel.account_holders_preshare)
        contentValues.put(ACCOUNT_HOLDERS_LOANAPP_COL, accountHolderModel.account_holders_loanapp)

        val db = this.writableDatabase
        try {
            db.insert(ACCOUNT_HOLDERS_TABLE, null, contentValues)
            Toast.makeText(mContext, "Account Holder added",Toast.LENGTH_SHORT).show()
        } catch (e : Exception){
            Toast.makeText(mContext, e.message,Toast.LENGTH_SHORT).show()
        }
            db.close()
    }


}