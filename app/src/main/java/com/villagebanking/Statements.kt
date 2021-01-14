package com.villagebanking

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.statements.*

class Statements: AppCompatActivity() {

    companion object {
        lateinit var dbHandler: DBHandler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statements)

        dbHandler = DBHandler(this, null, null, 1)

        viewStatements()

    }






    private fun getStatement(): ArrayList<Model>{
        val query = "SELECT * FROM ${DBHandler.STATEMENT_TABLE}"
        val db = dbHandler.writableDatabase
        val cursor = db.rawQuery(query, null)
        val statementsModel = ArrayList<Model>()
        if(cursor.count == 0)
            Toast.makeText(this, "No Statements Found", Toast.LENGTH_SHORT).show() else
        {while (cursor.moveToNext()){
            val statements = Model()
            statements.statementsID = cursor.getInt(cursor.getColumnIndex(DBHandler.STATEMENT_ID))
            statements.statementsName = cursor.getString(cursor.getColumnIndex(DBHandler.STATEMENT_NAME))
            statements.statementsMonth = cursor.getString(cursor.getColumnIndex(DBHandler.STATEMENT_MONTH))
            statements.statementsDate = cursor.getString(cursor.getColumnIndex(DBHandler.STATEMENT_DATE))

            statementsModel.add(statements)
        }
        }
        cursor.close()
        db.close()
        return statementsModel
    }


    private fun viewStatements(){
        val statementsList = getStatement()
        val adapter = CustomAdapter3(this, statementsList)
        val rv3: RecyclerView = recyclerView3
        rv3.setHasFixedSize(true)
        rv3.adapter = adapter
    }




}