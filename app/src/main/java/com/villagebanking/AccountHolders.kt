package com.villagebanking

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity

class AccountHolders: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_holders)

        val actionBar = supportActionBar
        actionBar!!.title = "Account Holders"
        actionBar.setDisplayHomeAsUpEnabled(true)




    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}