package com.villagebanking

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings.*


class Settings : AppCompatActivity() {

    companion object{
        lateinit var dbHandler: DBHandler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        dbHandler = DBHandler(this, null, null, 1)

        val actionBar = supportActionBar
        actionBar!!.title = "Settings"
        actionBar.setDisplayHomeAsUpEnabled(true)

        settingsWarning()

        enableEditNotes()
        setSettingsTable()

        if(etSettingsNotes.text.isEmpty()){
            etSettingsNotes.isEnabled = true
        }
    }


    override fun onBackPressed() {
        finish()
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save -> {
            saveSettings()
            }
        }
        return true
    }


    private fun setSettingsTable(){
        val etSettingsShareValue: EditText = etSettingsShareValue
        val etSettingsInterestRate: EditText = etSettingsInterestRate
        val etSettingsNotes: EditText = etSettingsNotes

        val query = "SELECT * FROM ${DBHandler.SETTINGS_TABLE}"
        var db = dbHandler.writableDatabase
        var cursor = db.rawQuery(query, null)
            cursor.moveToFirst()
            etSettingsShareValue.setText(cursor.getString(cursor.getColumnIndex(DBHandler.SETTINGS_SHARE_VALUE_COL)))
            etSettingsInterestRate.setText(cursor.getString(cursor.getColumnIndex(DBHandler.SETTINGS_INTEREST_RATE_COL)))
            etSettingsNotes.setText(cursor.getString(cursor.getColumnIndex(DBHandler.SETTINGS_NOTES_COL)))
        cursor.close()
    }


    private fun saveSettings(){

        val etSettingsShareValue: EditText = etSettingsShareValue
        val etSettingsInterestRate: EditText = etSettingsInterestRate
        val etSettingsNotes: EditText = etSettingsNotes

            val contentValues = ContentValues()
            val settingsModel = Model()

        settingsModel.settingsShareValue = etSettingsShareValue.text.toString().toDouble()
        settingsModel.settingsInterestRate = etSettingsInterestRate.text.toString().toInt()
        settingsModel.settingsNotes = etSettingsNotes.text.toString()

            val db = dbHandler.writableDatabase
            contentValues.put(DBHandler.SETTINGS_SHARE_VALUE_COL, settingsModel.settingsShareValue)
            contentValues.put(DBHandler.SETTINGS_INTEREST_RATE_COL, settingsModel.settingsInterestRate)
            contentValues.put(DBHandler.SETTINGS_NOTES_COL, settingsModel.settingsNotes)
            db.update(DBHandler.SETTINGS_TABLE, contentValues, "${DBHandler.SETTINGS_ID_COL} = '1'", arrayOf())
            db.close()
            Toast.makeText(this,"Settings Saves",Toast.LENGTH_SHORT).show()
            etSettingsNotes.isEnabled = false

        val intent = Intent(this, Home::class.java)
        finish()
        startActivity(intent)
        db.close()
    }


    private fun enableEditNotes(){
        val btnEditNotes : Button = btnEditNotes
        btnEditNotes.setOnClickListener {
            etSettingsNotes.isEnabled = true
        }
    }


    fun settingsWarning(){
        val query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE}"
        val db = dbHandler.readableDatabase
        val cursor = db.rawQuery(query, null)
        if(cursor.count > 0){
            AlertDialog.Builder(this)
                    .setTitle("Warning!!!")
                    .setIcon(R.drawable.ic_warning)
                    .setMessage("Changing INTEREST RATES and SHARE VALUE after transactions " +
                            "have already been made may give inaccurate accounts.\n \n" +
                            "Personal Notes and Rules can be edited anytime.")
                    .setNegativeButton("Understood"){_,_->}
                    .show()
        }
        cursor.close()
    }

}