package com.villagebanking

import android.os.Bundle
import android.widget.TextView
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
        var accountHolderModel = AccountHolderModel()
        var tvNameDetails: TextView = tvNameDetails



/*
        val query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = '$name'"
        val db= dbHandler.writableDatabase
        val cursor = db.rawQuery(query, null)




        if (cursor.moveToFirst()){
            name = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_CONTACT_COL))
            tvNameDetails.text = name
        }



 */

    }


}