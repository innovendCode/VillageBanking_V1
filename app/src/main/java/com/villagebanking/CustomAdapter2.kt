package com.villagebanking

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_payments.view.*
import kotlinx.android.synthetic.main.dialog_posts.view.*
import kotlinx.android.synthetic.main.sub_row_layout.view.*
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.round

class CustomAdapter2(mContext2: Context, private val transactionsModel: ArrayList<Model>): RecyclerView.Adapter<CustomAdapter2.ViewHolder>()  {

    val mContext2 = mContext2

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTransactionID : TextView = itemView.tvTransactionID
        val tvTransactionMonth : TextView = itemView.tvTransactionMonth
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transactionsModelPosition : Model = transactionsModel[position]
        holder.tvTransactionID.text = transactionsModelPosition.transactionID.toString()
        holder.tvTransactionMonth.text = transactionsModelPosition.transactionMonth
        holder.tvTransactionInterest.text = transactionsModelPosition.transactionInterest.toString()
        holder.tvTransactionShares.text = transactionsModelPosition.transactionShares.toString()
        holder.tvTransactionShareAmount.text = transactionsModelPosition.transactionShareAmount.toString()
        holder.tvTransactionSharePayment.text = transactionsModelPosition.transactionSharePayment.toString()
        holder.tvTransactionShareDate.text = transactionsModelPosition.transactionShareDate

        holder.tvTransactionLoan.text = transactionsModelPosition.transactionLoanApp.toString()
        holder.tvTransactionLoanPayment.text = transactionsModelPosition.transactionLoanPayment.toString()
        holder.tvTransactionLoanToRepay.text = transactionsModelPosition.transactionLoanToRepay.toString()
        holder.tvTransactionLoanPaymentDate.text = transactionsModelPosition.transactionLoanPaymentDate
        holder.tvTransactionLoanRepayment.text = transactionsModelPosition.transactionLoanRepayment.toString()
        holder.tvTransactionLoanRepaymentDate.text = transactionsModelPosition.transactionLoanRepaymentDate

        holder.tvTransactionCharge.text = transactionsModelPosition.transactionCharge.toString()
        holder.tvTransactionChargePayment.text = transactionsModelPosition.transactionChargePayment.toString()
        holder.tvTransactionChargePaymentDate.text = transactionsModelPosition.transactionChargePaymentDate


        holder.btnSharePayment.setOnClickListener {

            var balance = (transactionsModel[position].transactionShareAmount.toString().toDouble() -
                    transactionsModel[position].transactionSharePayment.toString().toDouble()).toString()

            val paymentDialogLayout = LayoutInflater.from(mContext2).inflate(R.layout.dialog_payments, null)
            paymentDialogLayout.etPayments.setText(balance)
            AlertDialog.Builder(mContext2)
                    .setTitle("Share Payment")
                    .setView(paymentDialogLayout)
                    .setPositiveButton("Cash In", null)
                    .setNegativeButton("Cancel") {_,_->}
                    .setNeutralButton("Reverse Cash In") {_,_->

                        val posts : Boolean = MainActivity.dbHandler.sharePayment(mContext2, transactionsModelPosition.transactionID,
                                paymentDialogLayout.etPayments.text.toString(),
                                AccountDetails().transactionDate)
                        if (posts){
                            transactionsModel[position].transactionSharePayment = 0.0
                            transactionsModel[position].transactionShareDate = AccountDetails().transactionDate
                            notifyDataSetChanged()
                            Toast.makeText(mContext2, "Payment Reversed", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(mContext2, "Something wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
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

                                paymentDialogLayout.etPayments.setText((transactionsModel[position].transactionSharePayment +
                                        paymentDialogLayout.etPayments.text.toString().toDouble()).toString())

                                val posts : Boolean = MainActivity.dbHandler.sharePayment(mContext2, transactionsModelPosition.transactionID,
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



        holder.btnLoanPayout.setOnClickListener {

            var balance = (transactionsModel[position].transactionLoanApp.toString().toDouble() -
                    transactionsModel[position].transactionLoanPayment.toString().toDouble()).toString()

            val paymentDialogLayout = LayoutInflater.from(mContext2).inflate(R.layout.dialog_payments, null)
            paymentDialogLayout.etPayments.setText(balance)
            AlertDialog.Builder(mContext2)
                    .setTitle("Loan Payout")
                    .setView(paymentDialogLayout)
                    .setPositiveButton("Cash Out", null)
                    .setNegativeButton("Cancel") {_,_->}
                    .setNeutralButton("Reverse Cash Out") {_,_->

                        val posts : Boolean = MainActivity.dbHandler.loanPayout(mContext2, transactionsModelPosition.transactionID,
                                paymentDialogLayout.etPayments.text.toString(),
                                AccountDetails().transactionDate)
                        if (posts){
                            transactionsModel[position].transactionLoanPayment = 0.0
                            transactionsModel[position].transactionLoanPaymentDate = AccountDetails().transactionDate
                            notifyDataSetChanged()
                            Toast.makeText(mContext2, "Payment Reversed", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(mContext2, "Something wrong", Toast.LENGTH_SHORT).show()
                        }

                    }
                    .create().apply {
                        setOnShowListener {
                            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                                if(paymentDialogLayout.etPayments.text.toString().toDouble() >
                                        (transactionsModel[position].transactionLoanApp - transactionsModel[position].transactionLoanPayment)){
                                    Toast.makeText(mContext2, "Payout cannot be more than loan application", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }

                                if(paymentDialogLayout.etPayments.text.toString().toDouble() == 0.0){
                                    Toast.makeText(mContext2, "Payment Complete", Toast.LENGTH_SHORT).show()
                                    dismiss()
                                    return@setOnClickListener
                                }

                                paymentDialogLayout.etPayments.setText((transactionsModel[position].transactionLoanPayment +
                                        paymentDialogLayout.etPayments.text.toString().toDouble()).toString())

                                val posts : Boolean = MainActivity.dbHandler.loanPayout(mContext2, transactionsModelPosition.transactionID,
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



        holder.btnChargePayment.setOnClickListener {
            var balance = (transactionsModel[position].transactionCharge.toString().toDouble() -
                    transactionsModel[position].transactionChargePayment.toString().toDouble()).toString()

            val paymentDialogLayout = LayoutInflater.from(mContext2).inflate(R.layout.dialog_payments, null)
            paymentDialogLayout.etPayments.setText(balance)
            AlertDialog.Builder(mContext2)
                    .setTitle("Charge Payment")
                    .setView(paymentDialogLayout)
                    .setPositiveButton("Cash In", null)
                    .setNegativeButton("Cancel") {_,_->}
                    .setNeutralButton("Reverse Cash In") {_,_->

                        val posts : Boolean = MainActivity.dbHandler.chargePayment(mContext2, transactionsModelPosition.transactionID,
                                paymentDialogLayout.etPayments.text.toString(),
                                AccountDetails().transactionDate)
                        if (posts){
                            transactionsModel[position].transactionChargePayment = 0.0
                            transactionsModel[position].transactionChargePaymentDate = AccountDetails().transactionDate
                            notifyDataSetChanged()
                            Toast.makeText(mContext2, "Payment Reversed", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(mContext2, "Something wrong", Toast.LENGTH_SHORT).show()
                        }

                    }
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
        }



        holder.btnLoanRepayment.setOnClickListener {



            var balance = (transactionsModel[position].transactionLoanToRepay.toString().toDouble() -
                    transactionsModel[position].transactionLoanRepayment.toString().toDouble()).toString()

            val paymentDialogLayout = LayoutInflater.from(mContext2).inflate(R.layout.dialog_payments, null)
            paymentDialogLayout.etPayments.setText(balance)
            AlertDialog.Builder(mContext2)
                    .setTitle("Loan Repayment")
                    .setView(paymentDialogLayout)
                    .setPositiveButton("Cash In", null)
                    .setNegativeButton("Cancel") {_,_->}
                    .setNeutralButton("Reverse Cash In") {_,_->

                        val posts : Boolean = MainActivity.dbHandler.loanRepayment(mContext2, transactionsModelPosition.transactionID,
                                paymentDialogLayout.etPayments.text.toString(),
                                AccountDetails().transactionDate)
                        if (posts){
                            transactionsModel[position].transactionLoanRepayment = 0.0
                            transactionsModel[position].transactionLoanRepaymentDate = AccountDetails().transactionDate
                            notifyDataSetChanged()
                            Toast.makeText(mContext2, "Repayment Reversed", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(mContext2, "Something wrong", Toast.LENGTH_SHORT).show()
                        }

                    }
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


        }


    }

    override fun getItemCount(): Int {
        return transactionsModel.size
    }


}