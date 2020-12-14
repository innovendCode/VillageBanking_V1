package com.villagebanking

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import kotlinx.android.synthetic.main.home.*


class Home: AppCompatActivity() {

    companion object{
        lateinit var dbHandler: DBHandler
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        val actionbar = supportActionBar
        actionbar!!.title = "Home"
        actionbar.setDisplayHomeAsUpEnabled(true)


       dbHandler = DBHandler(this, null, null, 1)

        getNoAccounts()

    }


    override fun onRestart() {
        getNoAccounts()
        super.onRestart()
    }



    private fun getNoAccounts(){
        val query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        val db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, null)
        val tvNoAccountHolders: TextView = tvNoAccountHolders
        tvNoAccountHolders.text = "- Account Holders: ${cursor.count.toString()}"
    }



    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.accountHolders ->{
               val intent = Intent(this,AccountHolders::class.java)
               startActivity(intent)
            }
            R.id.logOff ->{
                val logOff = AlertDialog.Builder(this)
                        .setTitle("Log Off")
                        .setMessage("Are you sure you want to log off?")
                        .setPositiveButton("Yes") {_:DialogInterface, _: Int ->
                            finish()
                        }
                        .setNegativeButton("No") {_:DialogInterface, _:Int ->
                        }
                        logOff.show()
            }
            R.id.settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
            }
        }



        return true
    }
}

    fun logOff() {

    }