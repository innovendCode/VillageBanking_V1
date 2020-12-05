package com.villagebanking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.account_details.*

class AccountDetails : AppCompatActivity() {

    companion object {
        lateinit var dbHandler: DBHandler
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_details)

        dbHandler = DBHandler(this, null, null, 1)
        viewTransactions()
    }


    private fun viewTransactions(){
        val transactionList = dbHandler.getTransactions(this)
        val adapter = CustomAdapter2(this, transactionList)
        val rv2: RecyclerView = recyclerView2
        rv2.setHasFixedSize(true)
        rv2.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager
        rv2.adapter = adapter
    }




}

