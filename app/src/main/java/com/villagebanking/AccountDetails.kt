package com.villagebanking

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.account_details.*

class AccountDetails : AppCompatActivity() {

    companion object{
        lateinit var dbHandler: DBHandler


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_details)

        dbHandler = DBHandler(this, null, null, 1)

        showAccountDetails()
        postShare()
        applyLoan()

    }


    private fun showAccountDetails(){

        var tvNameDetails: TextView = tvNameDetails
        var intent = intent
        var name = intent.getStringExtra("nameDetailsAD")
        tvNameDetails.text = name

        val tvContactNoDetails: TextView = tvContactNoDetails
        val tvAccountInfoDetails: TextView = tvAccountInfoDetails
        val etPreShare: EditText = etPreShare
        val etLoanApp: EditText = etLoanApp



        val query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = '$name'"
        val db = dbHandler.writableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()){
            tvContactNoDetails.text = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_CONTACT_COL))
            tvAccountInfoDetails.text = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_BANK_INFO_COL))
            etPreShare.setText(cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_SHARE_COL)))
            etLoanApp.setText(cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL)))
            cursor.close()
            db.close()
        }
    }



    private fun postShare(){
        var tvNameDetails: TextView = tvNameDetails
        var intent = intent
        var name = intent.getStringExtra("nameDetailsAD")
        tvNameDetails.text = name

        val btnPostShare : Button = btnPostShare
        val accountHolderModel = AccountHolderModel()
        val etPreShare : EditText = etPreShare
        btnPostShare.setOnClickListener {
            accountHolderModel.accountHoldersShare = etPreShare.text.toString().toInt()
            val contentValues = ContentValues()
            contentValues.put(DBHandler.ACCOUNT_HOLDERS_SHARE_COL, accountHolderModel.accountHoldersShare)
            val db = dbHandler.writableDatabase
            db.update(DBHandler.ACCOUNT_HOLDERS_TABLE, contentValues, "${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = ?", arrayOf(name))
            db.close()
            Toast.makeText(this,"${etPreShare.text.toString()} Shares committed",Toast.LENGTH_SHORT).show()
        }
    }



    private fun applyLoan(){
        var tvNameDetails: TextView = tvNameDetails
        var intent = intent
        var name = intent.getStringExtra("nameDetailsAD")
        tvNameDetails.text = name

        val btnApplyLoan : Button = btnApplyLoan
        val accountHolderModel = AccountHolderModel()
        val etLoanApp : EditText = etLoanApp
        btnApplyLoan.setOnClickListener {
            accountHolderModel.accountHoldersLoanApp = etLoanApp.text.toString().toDouble()
            val contentValues = ContentValues()
            contentValues.put(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL, accountHolderModel.accountHoldersLoanApp)
            val db = dbHandler.writableDatabase
            db.update(DBHandler.ACCOUNT_HOLDERS_TABLE, contentValues, "${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = ?", arrayOf(name))
            db.close()
            Toast.makeText(this,"${etLoanApp.text.toString()} Loan Application Made",Toast.LENGTH_SHORT).show()
        }
    }

}