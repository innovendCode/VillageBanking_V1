package com.villagebanking

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.villagebanking.MainActivity.Companion.dbHandler
import kotlinx.android.synthetic.main.dialog_payments.view.*
import kotlinx.android.synthetic.main.dialog_posts.view.*
import kotlinx.android.synthetic.main.sub_row_layout.view.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList

class CustomAdapter2(mContext2: Context, private val transactionsModel: ArrayList<Model>): RecyclerView.Adapter<CustomAdapter2.ViewHolder>()  {

    val mContext2 = mContext2


    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTransactionID : TextView = itemView.tvTransactionID
        val tvTransactionMonth : TextView = itemView.tvTransactionMonth
        val tvTransactionName : TextView = itemView.tvTransactionName
        val tvTransactionInterest : TextView = itemView.tvTransactionInterest
        val tvTransactionShares : TextView = itemView.tvTransactionShares
        val tvTransactionShareAmount : TextView = itemView.tvTransactionShareAmount
        val tvTransactionSharePayment : TextView = itemView.tvTransactionSharePayment
        val tvTransactionShareDate : TextView = itemView.tvTransactionShareDate

        val tvTransactionLoan : TextView = itemView.tvTransactionLoan
        val tvTransactionLoanPayment : TextView = itemView.tvTransactionLoanPayment
        val tvTransactionLoanPaymentDate : TextView = itemView.tvTransactionLoanPaymentDate
        val tvTransactionLoanToRepay : TextView = itemView.tvTransactionLoanToRepay
        val tvTransactionLoanRepayment : TextView = itemView.tvTransactionLoanRepayment
        val tvTransactionLoanRepaymentDate : TextView = itemView.tvTransactionLoanRepaymentDate

        val tvTransactionCharge : TextView = itemView.tvTransactionCharge
        val tvTransactionChargePayment : TextView = itemView.tvTransactionChargePayment
        val tvTransactionChargePaymentDate : TextView = itemView.tvTransactionChargePaymentDate

        val btnSharePayment : ImageButton = itemView.btnSharePayment
        val btnLoanPayout : ImageButton = itemView.btnLoanPayout
        val btnChargePayment : ImageButton = itemView.btnChargePayment
        val btnLoanRepayment : ImageButton = itemView.btnLoanRepayment
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sub_row_layout, parent, false)
        return ViewHolder(view)
    }



    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transactionsModelPosition : Model = transactionsModel[position]
        holder.tvTransactionID.text = transactionsModelPosition.transactionID.toString()
        holder.tvTransactionMonth.text = transactionsModelPosition.transactionMonth
        holder.tvTransactionInterest.text = transactionsModelPosition.transactionInterest.toString()
        holder.tvTransactionName.text = transactionsModelPosition.transactionName
        holder.tvTransactionShares.text = transactionsModelPosition.transactionShares.toString()
        holder.tvTransactionShareAmount.text = BigDecimal(transactionsModelPosition.transactionShareAmount).setScale(2, RoundingMode.HALF_EVEN).toString()
        holder.tvTransactionSharePayment.text = BigDecimal(transactionsModelPosition.transactionSharePayment).setScale(2, RoundingMode.HALF_EVEN).toString()
        holder.tvTransactionShareDate.text = transactionsModelPosition.transactionShareDate

        holder.tvTransactionLoan.text = BigDecimal(transactionsModelPosition.transactionLoanApp).setScale(2, RoundingMode.HALF_EVEN).toString()
        holder.tvTransactionLoanPayment.text = BigDecimal(transactionsModelPosition.transactionLoanPayment).setScale(2, RoundingMode.HALF_EVEN).toString()
        holder.tvTransactionLoanToRepay.text = BigDecimal(transactionsModelPosition.transactionLoanToRepay).setScale(2, RoundingMode.HALF_EVEN).toString()
        holder.tvTransactionLoanPaymentDate.text = transactionsModelPosition.transactionLoanPaymentDate
        holder.tvTransactionLoanRepayment.text = BigDecimal(transactionsModelPosition.transactionLoanRepayment).setScale(2, RoundingMode.HALF_EVEN).toString()
        holder.tvTransactionLoanRepaymentDate.text = transactionsModelPosition.transactionLoanRepaymentDate

        holder.tvTransactionCharge.text = BigDecimal(transactionsModelPosition.transactionCharge).setScale(2, RoundingMode.HALF_EVEN).toString()
        holder.tvTransactionChargePayment.text = BigDecimal(transactionsModelPosition.transactionChargePayment).setScale(2, RoundingMode.HALF_EVEN).toString()
        holder.tvTransactionChargePaymentDate.text = transactionsModelPosition.transactionChargePaymentDate

        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = java.text.SimpleDateFormat("MMMM yyyy")
        val stf = java.text.SimpleDateFormat("kk:mm")
        //println(sdf.format(c.time)) // NOW
        val transactionMonth = (sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        //println(sdf.format(c.time)) // One month ago
        val transactionLastMonth = (sdf.format(c.time))
        val currentTime = (stf.format(c.time))

        @SuppressLint("SimpleDateFormat")
        val sdf2 = SimpleDateFormat("dd MMM yyyy")
        val transactionDate: String = sdf2.format(Date())

        var totalShareSubmitted = 0.0
        var totalSharePayment = 0.0
        var totalLoanSubmitted = 0.0
        var totalLoanPayout = 0.0
        var totalLoanRepayment = 0.0
        var totalChargeSubmitted = 0.0
        var totalChargePayment = 0.0
        var availableCash  = 0.0

        var query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE}"
        var db = dbHandler.readableDatabase
        var cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            totalSharePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_SHARE_PAYMENT_COL))
            totalLoanRepayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_REPAYMENT_COL))
            totalChargePayment += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_CHARGE_PAYMENT_COL))
            totalLoanPayout += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
        }


        var previousLoanPayouts = 0.0

        query = "SELECT * FROM ${DBHandler.TRANSACTION_TABLE} WHERE ${DBHandler.TRANSACTION_MONTH_COL} != ?"
        db = Home.dbHandler.readableDatabase
        cursor = db.rawQuery(query, arrayOf(transactionMonth))
        while (cursor.moveToNext()) {
            previousLoanPayouts += cursor.getDouble(cursor.getColumnIndex(DBHandler.TRANSACTION_LOAN_PAYMENT_COL))
        }


        query = "SELECT * FROM ${DBHandler.ACCOUNT_HOLDERS_TABLE}"
        db = Home.dbHandler.readableDatabase
        cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            totalShareSubmitted += cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_SHARE_COL))
            totalLoanSubmitted += cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_LOAN_APP_COL))
            totalChargeSubmitted += cursor.getDouble(cursor.getColumnIndex(DBHandler.ACCOUNT_HOLDERS_CHARGES_COL))
        }


        val virtualBalance = totalSharePayment + totalLoanRepayment + totalChargePayment - totalLoanSubmitted - previousLoanPayouts
        availableCash = totalSharePayment + totalLoanRepayment + totalChargePayment - totalLoanPayout



        holder.itemView.setOnClickListener {

            query = "SELECT * FROM ${DBHandler.STATEMENT_TABLE} WHERE ${DBHandler.STATEMENT_MONTH} = ? AND ${DBHandler.STATEMENT_NAME} = ?"
            db = dbHandler.readableDatabase
            cursor = db.rawQuery(query, arrayOf(transactionsModelPosition.transactionMonth, transactionsModelPosition.transactionName))

            val buffer = StringBuffer()
            if (cursor.count == 0)
                Toast.makeText(mContext2, "No Records Found", Toast.LENGTH_SHORT).show() else {
                while (cursor.moveToNext()) {
                    buffer.append(" Date: ${cursor.getString(cursor.getColumnIndex(DBHandler.STATEMENT_DATE))}\n")
                    buffer.append(" Time: ${cursor.getString(cursor.getColumnIndex(DBHandler.STATEMENT_TIME))}\n")
                    buffer.append(" ${cursor.getString(cursor.getColumnIndex(DBHandler.STATEMENT_ACTION))}\n")
                    buffer.append("  -Shares: ${cursor.getDouble(cursor.getColumnIndex(DBHandler.STATEMENT_SHARE))}\n")
                    buffer.append("  -Share Amount: ${cursor.getDouble(cursor.getColumnIndex(DBHandler.STATEMENT_SHARE_AMOUNT))}\n")
                    buffer.append("  -Loan: ${cursor.getDouble(cursor.getColumnIndex(DBHandler.STATEMENT_LOAN))}\n")
                    buffer.append("  -Charge: ${cursor.getDouble(cursor.getColumnIndex(DBHandler.STATEMENT_CHARGE))}\n\n")
                }
                AlertDialog.Builder(mContext2)
                        .setTitle("${transactionsModelPosition.transactionMonth}\nStatement")
                        .setMessage(buffer.toString())
                        .setNegativeButton("OK") { _, _ -> }
                        .show()
            }
        }



        holder.itemView.setOnLongClickListener{

            val c: Calendar = GregorianCalendar()
            c.time = Date()
            val sdf = java.text.SimpleDateFormat("MMMM yyyy")
            //println(sdf.format(c.time)) // NOW
            val transactionMonth = (sdf.format(c.time))

            if (transactionsModel[position].transactionMonth == transactionMonth){

                AlertDialog.Builder(mContext2)
                        .setTitle("Warning")
                        .setIcon(R.drawable.ic_warning)
                        .setMessage("Delete $transactionMonth transactions?")
                        .setNegativeButton("No") {_,_->}
                        .setPositiveButton("Yes") {_,_->


                            if (virtualBalance < transactionsModelPosition.transactionSharePayment - transactionsModelPosition.transactionLoanPayment){
                                AlertDialog.Builder(mContext2)
                                        .setTitle("Insufficient funds: ${transactionsModelPosition.transactionSharePayment - transactionsModelPosition.transactionLoanPayment}")
                                        .setMessage("Insufficient cash to refund${transactionsModelPosition.transactionName}.\n\n" +
                                                "To resolve this deficit, return loan applications amounting to" +
                                                " ${transactionsModelPosition.transactionSharePayment - transactionsModelPosition.transactionLoanPayment}.")
                                        .setNegativeButton("Got it") {_,_->}
                                        .show()
                                return@setPositiveButton
                            }




                            //Zero Share Payment
                            val zeroShare : Boolean = dbHandler.sharePayment(mContext2, transactionsModelPosition.transactionID,
                                    0.0.toString(),
                                    AccountDetails().transactionDate)
                            if (zeroShare){
                                transactionsModel[position].transactionSharePayment = 0.0
                                transactionsModel[position].transactionShareDate = AccountDetails().transactionDate
                                notifyDataSetChanged()
                            }

                            //Zero Loan Payout
                            val zeroLoanPayment : Boolean = dbHandler.loanPayout(mContext2, transactionsModelPosition.transactionID,
                                    0.0.toString(),
                                    AccountDetails().transactionDate)
                            if (zeroLoanPayment){
                                transactionsModel[position].transactionLoanPayment = 0.0
                                transactionsModel[position].transactionLoanPaymentDate = AccountDetails().transactionDate
                                notifyDataSetChanged()
                            }

                            //Zero Charge Payment
                            val zeroChargePayment : Boolean = dbHandler.chargePayment(mContext2, transactionsModelPosition.transactionID,
                                    0.0.toString(),
                                    AccountDetails().transactionDate)
                            if (zeroChargePayment){
                                transactionsModel[position].transactionChargePayment = 0.0
                                transactionsModel[position].transactionChargePaymentDate = AccountDetails().transactionDate
                                notifyDataSetChanged()
                            }

                          //Zero Loan Repayment
                            val zeroLR : Boolean = dbHandler.loanRepayment(mContext2, transactionsModelPosition.transactionID,
                                    0.0.toString(),
                                    AccountDetails().transactionDate)
                            if (zeroLR){
                                transactionsModel[position].transactionLoanRepayment = 0.0
                                transactionsModel[position].transactionLoanRepaymentDate = AccountDetails().transactionDate
                                notifyDataSetChanged()
                            }


                    dbHandler.deleteMonth(transactionsModelPosition.transactionID)
                    Toast.makeText(mContext2, "${transactionsModel[position].transactionMonth} deleted", Toast.LENGTH_SHORT).show()
                            transactionsModel.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeRemoved(position, transactionsModel.size)
                        }
                        .show()
            }else{
                Toast.makeText(mContext2, "Can only delete current month", Toast.LENGTH_SHORT).show()
            }

            true
        }



        holder.btnSharePayment.setOnClickListener {

            var balance = (transactionsModel[position].transactionShareAmount.toString().toDouble() -
                    transactionsModel[position].transactionSharePayment.toString().toDouble()).toString()

            val paymentDialogLayout = LayoutInflater.from(mContext2).inflate(R.layout.dialog_payments, null)
            paymentDialogLayout.etPayments.setText(balance)
            AlertDialog.Builder(mContext2)
                    .setTitle("Share Payment")
                    .setView(paymentDialogLayout)
                    .setPositiveButton("Pay", null)
                    .setNegativeButton("Cancel") {_,_->}
                    .create().apply {
                        setOnShowListener {
                            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                                if(paymentDialogLayout.etPayments.text.toString().toDouble() >
                                        (transactionsModel[position].transactionShareAmount - transactionsModel[position].transactionSharePayment)){
                                    Toast.makeText(mContext2, "Cash In cannot be more that share", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }

                                if(paymentDialogLayout.etPayments.text.toString().toDouble() == 0.0){
                                    Toast.makeText(mContext2, "Payment Complete", Toast.LENGTH_SHORT).show()
                                    dismiss()
                                    return@setOnClickListener
                                }

                                val contentValues = ContentValues()
                                val payment : Double = paymentDialogLayout.etPayments.text.toString().toDouble()
                                contentValues.put(DBHandler.STATEMENT_MONTH, transactionMonth)
                                contentValues.put(DBHandler.STATEMENT_DATE, transactionDate)
                                contentValues.put(DBHandler.STATEMENT_TIME, currentTime)
                                contentValues.put(DBHandler.STATEMENT_NAME, transactionsModelPosition.transactionName)
                                contentValues.put(DBHandler.STATEMENT_ACTION, "Share Payment")
                                contentValues.put(DBHandler.STATEMENT_SHARE_AMOUNT, BigDecimal(payment).setScale(2, RoundingMode.HALF_EVEN).toDouble())
                                db = dbHandler.writableDatabase
                                db.insert(DBHandler.STATEMENT_TABLE, null, contentValues)

                                paymentDialogLayout.etPayments.setText((transactionsModel[position].transactionSharePayment +
                                        paymentDialogLayout.etPayments.text.toString().toDouble()).toString())

                                val posts : Boolean = dbHandler.sharePayment(mContext2, transactionsModelPosition.transactionID,
                                        paymentDialogLayout.etPayments.text.toString(),
                                        AccountDetails().transactionDate)
                                if (posts){
                                    transactionsModel[position].transactionSharePayment = paymentDialogLayout.etPayments.text.toString().toDouble()
                                    transactionsModel[position].transactionShareDate = AccountDetails().transactionDate
                                    if (transactionsModel[position].transactionShareAmount == transactionsModel[position].transactionSharePayment){
                                        Toast.makeText(mContext2, "Payment Complete", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(mContext2, "Part Payment", Toast.LENGTH_SHORT).show()
                                    }
                                    notifyDataSetChanged()
                                    dismiss()
                                }else{
                                    Toast.makeText(mContext2, "Something wrong", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .show()
        }



        holder.btnSharePayment.setOnLongClickListener {

            if (transactionsModelPosition.transactionSharePayment > virtualBalance){
                Toast.makeText(mContext2, "Insufficient cash for a refund. Check Loan Applications", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }

            val contentValues = ContentValues()
            contentValues.put(DBHandler.STATEMENT_MONTH, transactionMonth)
            contentValues.put(DBHandler.STATEMENT_DATE, transactionDate)
            contentValues.put(DBHandler.STATEMENT_TIME, currentTime)
            contentValues.put(DBHandler.STATEMENT_NAME, transactionsModelPosition.transactionName)
            contentValues.put(DBHandler.STATEMENT_ACTION, "Share Payment Reversed")
            contentValues.put(DBHandler.STATEMENT_SHARE, transactionsModelPosition.transactionShares*-1)
            contentValues.put(DBHandler.STATEMENT_SHARE_AMOUNT, BigDecimal(transactionsModelPosition.transactionSharePayment *-1).setScale(2, RoundingMode.HALF_EVEN).toDouble())
            db = dbHandler.writableDatabase
            db.insert(DBHandler.STATEMENT_TABLE, null, contentValues)

            val posts : Boolean = dbHandler.sharePayment(mContext2, transactionsModelPosition.transactionID,
                    0.0.toString(),
                    AccountDetails().transactionDate)
            if (posts){
                transactionsModel[position].transactionSharePayment = 0.0
                transactionsModel[position].transactionShareDate = AccountDetails().transactionDate
                notifyDataSetChanged()
                Toast.makeText(mContext2, "Payment Reversed", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(mContext2, "Something wrong", Toast.LENGTH_SHORT).show()
        }
            true
        }



        holder.btnLoanPayout.setOnClickListener {

            val balance = (transactionsModel[position].transactionLoanApp.toString().toDouble() -
                    transactionsModel[position].transactionLoanPayment.toString().toDouble()).toString()

            val paymentDialogLayout = LayoutInflater.from(mContext2).inflate(R.layout.dialog_payments, null)
            paymentDialogLayout.etPayments.setText(balance)
            AlertDialog.Builder(mContext2)
                    .setTitle("Loan Payout")
                    .setView(paymentDialogLayout)
                    .setPositiveButton("Pay", null)
                    .setNegativeButton("Cancel") {_,_->}
                    .create().apply {
                        setOnShowListener {
                            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                                if(paymentDialogLayout.etPayments.text.toString().toDouble() >
                                        (transactionsModel[position].transactionLoanApp - transactionsModel[position].transactionLoanPayment)){
                                    Toast.makeText(mContext2, "Payout cannot be more than loan application", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }

                                if (paymentDialogLayout.etPayments.text.toString().toDouble() > availableCash){
                                    Toast.makeText(mContext2, "Insufficient funds for loan application $availableCash", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }

                                if(paymentDialogLayout.etPayments.text.toString().toDouble() == 0.0){
                                    Toast.makeText(mContext2, "Payment Complete", Toast.LENGTH_SHORT).show()
                                    dismiss()
                                    return@setOnClickListener
                                }

                                val contentValues = ContentValues()
                                val payment : Double = paymentDialogLayout.etPayments.text.toString().toDouble()
                                contentValues.put(DBHandler.STATEMENT_MONTH, transactionMonth)
                                contentValues.put(DBHandler.STATEMENT_DATE, transactionDate)
                                contentValues.put(DBHandler.STATEMENT_TIME, currentTime)
                                contentValues.put(DBHandler.STATEMENT_NAME, transactionsModelPosition.transactionName)
                                contentValues.put(DBHandler.STATEMENT_ACTION, "Loan Payment")
                                contentValues.put(DBHandler.STATEMENT_LOAN, BigDecimal(payment).setScale(2, RoundingMode.HALF_EVEN).toDouble())
                                db = dbHandler.writableDatabase
                                db.insert(DBHandler.STATEMENT_TABLE, null, contentValues)

                                paymentDialogLayout.etPayments.setText((transactionsModel[position].transactionLoanPayment +
                                        paymentDialogLayout.etPayments.text.toString().toDouble()).toString())

                                val posts : Boolean = dbHandler.loanPayout(mContext2, transactionsModelPosition.transactionID,
                                        paymentDialogLayout.etPayments.text.toString(),
                                        AccountDetails().transactionDate)
                                if (posts){
                                    transactionsModel[position].transactionLoanPayment = paymentDialogLayout.etPayments.text.toString().toDouble()
                                    transactionsModel[position].transactionLoanPaymentDate = AccountDetails().transactionDate

                                    if (transactionsModel[position].transactionLoanApp == transactionsModel[position].transactionLoanPayment){
                                        Toast.makeText(mContext2, "Payment Complete", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(mContext2, "Part Payment", Toast.LENGTH_SHORT).show()
                                    }
                                    notifyDataSetChanged()
                                    dismiss()
                                }else{
                                    Toast.makeText(mContext2, "Something wrong", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .show()
        }



        holder.btnLoanPayout.setOnLongClickListener {

            val contentValues = ContentValues()
            contentValues.put(DBHandler.STATEMENT_MONTH, transactionMonth)
            contentValues.put(DBHandler.STATEMENT_DATE, transactionDate)
            contentValues.put(DBHandler.STATEMENT_TIME, currentTime)
            contentValues.put(DBHandler.STATEMENT_NAME, transactionsModelPosition.transactionName)
            contentValues.put(DBHandler.STATEMENT_ACTION, "Loan Payment Reversed")
            contentValues.put(DBHandler.STATEMENT_LOAN, BigDecimal(transactionsModelPosition.transactionLoanPayment*-1).setScale(2, RoundingMode.HALF_EVEN).toDouble())
            db = dbHandler.writableDatabase
            db.insert(DBHandler.STATEMENT_TABLE, null, contentValues)

            val posts : Boolean = MainActivity.dbHandler.loanPayout(mContext2, transactionsModelPosition.transactionID,
                    0.0.toString(),
                    AccountDetails().transactionDate)
            if (posts){
                transactionsModel[position].transactionLoanPayment = 0.0
                transactionsModel[position].transactionLoanPaymentDate = AccountDetails().transactionDate
                notifyDataSetChanged()
                Toast.makeText(mContext2, "Payment Reversed", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(mContext2, "Something wrong", Toast.LENGTH_SHORT).show()
            }
            true
        }



        holder.btnChargePayment.setOnClickListener {
            val balance = (transactionsModel[position].transactionCharge.toString().toDouble() -
                    transactionsModel[position].transactionChargePayment.toString().toDouble()).toString()

            val paymentDialogLayout = LayoutInflater.from(mContext2).inflate(R.layout.dialog_payments, null)
            paymentDialogLayout.etPayments.setText(balance)
            AlertDialog.Builder(mContext2)
                    .setTitle("Charge Payment")
                    .setView(paymentDialogLayout)
                    .setPositiveButton("Pay", null)
                    .setNegativeButton("Cancel") {_,_->}
                    .create().apply {
                        setOnShowListener {
                            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                                if(paymentDialogLayout.etPayments.text.toString().toDouble() >
                                        (transactionsModel[position].transactionCharge - transactionsModel[position].transactionChargePayment)){
                                    Toast.makeText(mContext2, "Cash In cannot be higher than charge", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }

                                if(paymentDialogLayout.etPayments.text.toString().toDouble() == 0.0){
                                    Toast.makeText(mContext2, "Payment Complete", Toast.LENGTH_SHORT).show()
                                    dismiss()
                                    return@setOnClickListener
                                }

                                val contentValues = ContentValues()
                                val payment : Double = paymentDialogLayout.etPayments.text.toString().toDouble()
                                contentValues.put(DBHandler.STATEMENT_MONTH, transactionMonth)
                                contentValues.put(DBHandler.STATEMENT_DATE, transactionDate)
                                contentValues.put(DBHandler.STATEMENT_TIME, currentTime)
                                contentValues.put(DBHandler.STATEMENT_NAME, transactionsModelPosition.transactionName)
                                contentValues.put(DBHandler.STATEMENT_ACTION, "Loan Payment")
                                contentValues.put(DBHandler.STATEMENT_CHARGE, BigDecimal(payment).setScale(2, RoundingMode.HALF_EVEN).toDouble())
                                db = dbHandler.writableDatabase
                                db.insert(DBHandler.STATEMENT_TABLE, null, contentValues)

                                paymentDialogLayout.etPayments.setText((transactionsModel[position].transactionChargePayment +
                                        paymentDialogLayout.etPayments.text.toString().toDouble()).toString())

                                val posts : Boolean = MainActivity.dbHandler.chargePayment(mContext2, transactionsModelPosition.transactionID,
                                        paymentDialogLayout.etPayments.text.toString(),
                                        AccountDetails().transactionDate)
                                if (posts){
                                    transactionsModel[position].transactionChargePayment = paymentDialogLayout.etPayments.text.toString().toDouble()
                                    transactionsModel[position].transactionChargePaymentDate = AccountDetails().transactionDate

                                    if (transactionsModel[position].transactionCharge == transactionsModel[position].transactionChargePayment){
                                        Toast.makeText(mContext2, "Charge Payment Complete", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(mContext2, "Part Payment", Toast.LENGTH_SHORT).show()
                                    }
                                    notifyDataSetChanged()
                                    dismiss()
                                }else{
                                    Toast.makeText(mContext2, "Something wrong", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .show()
            val contentValues = ContentValues()
            contentValues.put(DBHandler.STATEMENT_MONTH, transactionMonth)
            contentValues.put(DBHandler.STATEMENT_DATE, transactionDate)
            contentValues.put(DBHandler.STATEMENT_TIME, currentTime)
            contentValues.put(DBHandler.STATEMENT_NAME, transactionsModelPosition.transactionName)
            contentValues.put(DBHandler.STATEMENT_ACTION, "Charge Payment")
            contentValues.put(DBHandler.STATEMENT_CHARGE, BigDecimal(transactionsModelPosition.transactionChargePayment).setScale(2, RoundingMode.HALF_EVEN).toDouble())
            db = dbHandler.writableDatabase
            db.insert(DBHandler.STATEMENT_TABLE, null, contentValues)
        }



        holder.btnChargePayment.setOnLongClickListener {

            if (transactionsModelPosition.transactionChargePayment > availableCash){
                Toast.makeText(mContext2, "Insufficient cash for a refund", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }

            val contentValues = ContentValues()
            contentValues.put(DBHandler.STATEMENT_MONTH, transactionMonth)
            contentValues.put(DBHandler.STATEMENT_DATE, transactionDate)
            contentValues.put(DBHandler.STATEMENT_TIME, currentTime)
            contentValues.put(DBHandler.STATEMENT_NAME, transactionsModelPosition.transactionName)
            contentValues.put(DBHandler.STATEMENT_ACTION, "Charge Payment Reversed")
            contentValues.put(DBHandler.STATEMENT_CHARGE, BigDecimal(transactionsModelPosition.transactionChargePayment*-1).setScale(2, RoundingMode.HALF_EVEN).toDouble())
            db = dbHandler.writableDatabase
            db.insert(DBHandler.STATEMENT_TABLE, null, contentValues)

            val posts : Boolean = dbHandler.chargePayment(mContext2, transactionsModelPosition.transactionID,
                    0.0.toString(),
                    AccountDetails().transactionDate)
            if (posts){
                transactionsModel[position].transactionChargePayment = 0.0
                transactionsModel[position].transactionChargePaymentDate = AccountDetails().transactionDate
                notifyDataSetChanged()
                Toast.makeText(mContext2, "Payment Reversed", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(mContext2, "Something wrong", Toast.LENGTH_SHORT).show()
            }
            true
        }



        holder.btnLoanRepayment.setOnClickListener {

            val balance = (transactionsModel[position].transactionLoanToRepay.toString().toDouble() -
                    transactionsModel[position].transactionLoanRepayment.toString().toDouble()).toString()

            val paymentDialogLayout = LayoutInflater.from(mContext2).inflate(R.layout.dialog_payments, null)
            paymentDialogLayout.etPayments.setText(balance)
            AlertDialog.Builder(mContext2)
                    .setTitle("Loan Repayment")
                    .setView(paymentDialogLayout)
                    .setPositiveButton("Pay", null)
                    .setNegativeButton("Cancel") {_,_->}
                    .create().apply {
                        setOnShowListener {
                            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                                if(paymentDialogLayout.etPayments.text.toString().toDouble() >
                                        (transactionsModel[position].transactionLoanToRepay - transactionsModel[position].transactionLoanRepayment)){
                                    Toast.makeText(mContext2, "Cash In cannot be higher than loan repayment", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }

                                if(paymentDialogLayout.etPayments.text.toString().toDouble() == 0.0){
                                    Toast.makeText(mContext2, "Repayment Complete", Toast.LENGTH_SHORT).show()
                                    dismiss()
                                    return@setOnClickListener
                                }


                                val contentValues = ContentValues()
                                val payment : Double = paymentDialogLayout.etPayments.text.toString().toDouble()
                                contentValues.put(DBHandler.STATEMENT_MONTH, transactionMonth)
                                contentValues.put(DBHandler.STATEMENT_DATE, transactionDate)
                                contentValues.put(DBHandler.STATEMENT_TIME, currentTime)
                                contentValues.put(DBHandler.STATEMENT_NAME, transactionsModelPosition.transactionName)
                                contentValues.put(DBHandler.STATEMENT_ACTION, "Loan Payment")
                                contentValues.put(DBHandler.STATEMENT_LOAN, BigDecimal(payment).setScale(2, RoundingMode.HALF_EVEN).toDouble())
                                db = dbHandler.writableDatabase
                                db.insert(DBHandler.STATEMENT_TABLE, null, contentValues)


                                paymentDialogLayout.etPayments.setText((transactionsModel[position].transactionLoanRepayment +
                                        paymentDialogLayout.etPayments.text.toString().toDouble()).toString())


                                val posts : Boolean = MainActivity.dbHandler.loanRepayment(mContext2, transactionsModelPosition.transactionID,
                                        paymentDialogLayout.etPayments.text.toString(),
                                        AccountDetails().transactionDate)
                                if (posts){
                                    transactionsModel[position].transactionLoanRepayment = paymentDialogLayout.etPayments.text.toString().toDouble()
                                    transactionsModel[position].transactionLoanRepaymentDate = AccountDetails().transactionDate

                                    if (transactionsModel[position].transactionLoanToRepay == transactionsModel[position].transactionLoanRepayment){
                                        Toast.makeText(mContext2, "Loan Repayment Complete", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(mContext2, "Part Payment", Toast.LENGTH_SHORT).show()
                                    }
                                    notifyDataSetChanged()
                                    dismiss()
                                }else{
                                    Toast.makeText(mContext2, "Something wrong", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .show()
            val contentValues = ContentValues()
            contentValues.put(DBHandler.STATEMENT_MONTH, transactionMonth)
            contentValues.put(DBHandler.STATEMENT_DATE, transactionDate)
            contentValues.put(DBHandler.STATEMENT_TIME, currentTime)
            contentValues.put(DBHandler.STATEMENT_NAME, transactionsModelPosition.transactionName)
            contentValues.put(DBHandler.STATEMENT_ACTION, "Loan Repayment")
            contentValues.put(DBHandler.STATEMENT_LOAN, BigDecimal(transactionsModelPosition.transactionLoanRepayment).setScale(2, RoundingMode.HALF_EVEN).toDouble())
            db = dbHandler.writableDatabase
            db.insert(DBHandler.STATEMENT_TABLE, null, contentValues)
        }



        holder.btnLoanRepayment.setOnLongClickListener {

            if (transactionsModelPosition.transactionLoanRepayment > availableCash){
                Toast.makeText(mContext2, "Insufficient cash for a refund", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }

            val contentValues = ContentValues()
            contentValues.put(DBHandler.STATEMENT_MONTH, transactionMonth)
            contentValues.put(DBHandler.STATEMENT_DATE, transactionDate)
            contentValues.put(DBHandler.STATEMENT_TIME, currentTime)
            contentValues.put(DBHandler.STATEMENT_NAME, transactionsModelPosition.transactionName)
            contentValues.put(DBHandler.STATEMENT_ACTION, "Loan Repayment Reversed")
            contentValues.put(DBHandler.STATEMENT_LOAN, BigDecimal(transactionsModelPosition.transactionLoanRepayment*-1).setScale(2, RoundingMode.HALF_EVEN).toDouble())
            db = dbHandler.writableDatabase
            db.insert(DBHandler.STATEMENT_TABLE, null, contentValues)

            val posts : Boolean = dbHandler.loanRepayment(mContext2, transactionsModelPosition.transactionID,
                    0.0.toString(),
                    AccountDetails().transactionDate)
            if (posts){
                transactionsModel[position].transactionLoanRepayment = 0.0
                transactionsModel[position].transactionLoanRepaymentDate = AccountDetails().transactionDate
                notifyDataSetChanged()
                Toast.makeText(mContext2, "Repayment Reversed", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(mContext2, "Something wrong", Toast.LENGTH_SHORT).show()
            }
            true
        }


    }

    override fun getItemCount(): Int {
        return transactionsModel.size
    }


}