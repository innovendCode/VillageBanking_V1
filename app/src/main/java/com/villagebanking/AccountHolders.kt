package com.villagebanking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AccountHolders: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.membership_list)

        val actionBar = supportActionBar
        actionBar!!.title = "Account Holders"
        actionBar.setDisplayHomeAsUpEnabled(true)
    }
}