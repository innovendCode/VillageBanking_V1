package com.villagebanking


import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var dbHandler: DBHandler
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHandler = DBHandler(this, null,null, 1)

        val actionBar = supportActionBar
        actionBar!!.title = ""

        val btnLogin: Button = btnLogin
        val tvForgotPassword: TextView = tvForgotPIN

        btnLogin.setOnClickListener {
            //Check if chairperson account exists
            //If not, login without PIN
            getChairpersonAccount()
        }

        num1()
        num2()
        num3()
        num4()
        num5()
        num6()
        num7()
        num8()
        num9()
        num0()
        cancelPIN()
        firstTimeUse()

        tvForgotPassword.setOnClickListener {
            val query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE " +
                    "${DBHandler.ACCOUNT_HOLDERS_ADMIN_COL} = 'Chairperson'"
            val db = dbHandler.writableDatabase
            val cursor = db.rawQuery(query, null)

            if(cursor.moveToFirst()){
               val pinHint = cursor.getString(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_PIN_HINT_COL))
                Toast.makeText(this, "Your PIN Hint is: $pinHint", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "PIN not set. Click Proceed", Toast.LENGTH_LONG).show()
            }
            cursor.close()
            db.close()
        }

    }


    private fun firstTimeUse(){
        val query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        val db = dbHandler.writableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.count == 0){
            val firstTimeAlert = AlertDialog.Builder(this)
                    .setTitle("Welcome to Sonka - Village Banking")
                    .setMessage("First time use does not require a PIN. Click PROCEED to begin")
                    .setNeutralButton("OK") { _:DialogInterface, _: Int -> }
            firstTimeAlert.show()
        }
        cursor.close()
        db.close()
    }

    private fun dbLogin(){
        val etPassword: EditText = etPIN
        val password = etPassword.text
        val db = dbHandler.readableDatabase

        val query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE " +
                "${DBHandler.ACCOUNT_HOLDERS_PIN_COL}= '$password' AND " +
                "${DBHandler.ACCOUNT_HOLDERS_ADMIN_COL} = 'Chairperson'"

        val cursor = db.rawQuery(query, null)

        if (cursor.count == 1){
            etPIN.text.clear()
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Invalid PIN", Toast.LENGTH_SHORT).show()
            etPIN.text.clear()
        }
        cursor.close()
        db.close()
    }




    private fun getChairpersonAccount(){
        val db = dbHandler.readableDatabase
        val query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE} WHERE ${DBHandler.ACCOUNT_HOLDERS_ADMIN_COL} = 'Chairperson'"
        val cursor = db.rawQuery(query, null)

        if (cursor.count > 0) {
            dbLogin()
        }else{
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }



    private fun num1(){
        val tvNum1 : TextView = tvNum1
        val etPIN : EditText = etPIN
        val one = "1"
        tvNum1.setOnClickListener {
            etPIN.setSelection(etPIN.text.length)
            if (etPIN.text.isEmpty()){
                etPIN.setText(one)
            }else{
                val num = etPIN.text.toString() + one
                etPIN.setText(num)
            }
        }
    }

    private fun num2(){
        val tvNum2 : TextView = tvNum2
        val etPIN : EditText = etPIN
        val two = "2"
        tvNum2.setOnClickListener {
            etPIN.setSelection(etPIN.text.length)
            if (etPIN.text.isEmpty()){
                etPIN.setText(two)
            }else{
                val num = etPIN.text.toString() + two
                etPIN.setText(num)
            }
        }
    }

    private fun num3(){
        val tvNum3 : TextView = tvNum3
        val etPIN : EditText = etPIN
        val three = "3"
        tvNum3.setOnClickListener {
            etPIN.setSelection(etPIN.text.length)
            if (etPIN.text.isEmpty()){
                etPIN.setText(three)
            }else{
                val num = etPIN.text.toString() + three
                etPIN.setText(num)
            }
        }
    }

    private fun num4(){
        val tvNum4 : TextView = tvNum4
        val etPIN : EditText = etPIN
        val four = "4"
        tvNum4.setOnClickListener {
            etPIN.setSelection(etPIN.text.length)
            if (etPIN.text.isEmpty()){
                etPIN.setText(four)
            }else{
                val num = etPIN.text.toString() + four
                etPIN.setText(num)
            }
        }
    }

    private fun num5(){
        val tvNum5 : TextView = tvNum5
        val etPIN : EditText = etPIN
        val five = "5"
        tvNum5.setOnClickListener {
            etPIN.setSelection(etPIN.text.length)
            if (etPIN.text.isEmpty()){
                etPIN.setText(five)
            }else{
                val num = etPIN.text.toString() + five
                etPIN.setText(num)
            }
        }
    }

    private fun num6(){
        val tvNum6 : TextView = tvNum6
        val etPIN : EditText = etPIN
        val six = "6"
        tvNum6.setOnClickListener {
            etPIN.setSelection(etPIN.text.length)
            if (etPIN.text.isEmpty()){
                etPIN.setText(six)
            }else{
                val num = etPIN.text.toString() + six
                etPIN.setText(num)
            }
        }
    }

    private fun num7(){
        val tvNum7 : TextView = tvNum7
        val etPIN : EditText = etPIN
        val seven = "7"
        tvNum7.setOnClickListener {
            etPIN.setSelection(etPIN.text.length)
            if (etPIN.text.isEmpty()){
                etPIN.setText(seven)
            }else{
                val num = etPIN.text.toString() + seven
                etPIN.setText(num)
            }
        }
    }

    private fun num8(){
        val tvNum8 : TextView = tvNum8
        val etPIN : EditText = etPIN
        val eight = "8"
        tvNum8.setOnClickListener {
            etPIN.setSelection(etPIN.text.length)
            if (etPIN.text.isEmpty()){
                etPIN.setText(eight)
            }else{
                val num = etPIN.text.toString() + eight
                etPIN.setText(num)
            }
        }
    }

    private fun num9(){
        val vNum9 : TextView = tvNum9
        val etPIN : EditText = etPIN
        val nine = "9"
        vNum9.setOnClickListener {
            etPIN.setSelection(etPIN.text.length)
            if (etPIN.text.isEmpty()){
                etPIN.setText(nine)
            }else{
                val num = etPIN.text.toString() + nine
                etPIN.setText(num)
            }
        }
    }

    private fun num0(){
        val tvNum0 : TextView = tvNum0
        val etPIN : EditText = etPIN
        val zero = "0"
        tvNum0.setOnClickListener {
            etPIN.setSelection(etPIN.text.length)
            if (etPIN.text.isEmpty()){
                etPIN.setText(zero)
            }else{
                val num = etPIN.text.toString() + zero
                etPIN.setText(num)
            }
        }
    }

    private fun cancelPIN(){
        btnClearPIN.setOnClickListener {
            etPIN.text.clear()
        }
    }

    fun delAll(){
        val db = dbHandler.writableDatabase
        db.delete(DBHandler.ACCOUNT_HOLDERS_TABLE, null, null)
        db.close()
    }



}