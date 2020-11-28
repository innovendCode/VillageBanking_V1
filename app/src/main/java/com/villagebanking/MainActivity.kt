package com.villagebanking


import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*





class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var dbHandler: DBHandler


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHandler = DBHandler(this, null,null, 1)

        val actionBar = supportActionBar
        actionBar!!.title = "Login"

        val btnLogin: Button = btnLogin
        val tvForgotPassword: TextView = tvForgotPassword


        btnLogin.setOnClickListener {
            val etPassword: EditText = etPassword
            val password = etPassword.text
            val etUsername: EditText = etUsername
            val username = etUsername.text
            val db = dbHandler.readableDatabase

            val query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"

            val cursor = db.rawQuery(query, null)

            if (cursor.count == 0){
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
            } else {
                dbLogin()
            }
        }


        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgotten Password? Too Bad", Toast.LENGTH_SHORT).show()
        }






    }

    private fun dbLogin(){
        val etPassword: EditText = etPassword
        val password = etPassword.text
        val etUsername: EditText = etUsername
        val username = etUsername.text
        val db = dbHandler.readableDatabase

        val query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE " +
                "${DBHandler.ACCOUNT_HOLDERS_NAME_COL} = '$username' AND " +
                "${DBHandler.ACCOUNT_HOLDERS_PASSWORD_COL}= '$password' AND " +
                "${DBHandler.ACCOUNT_HOLDERS_ADMIN_COL} = 'Chairperson'"

        val cursor = db.rawQuery(query, null)

        if (cursor.count > 0){
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show()
        }
    }



}