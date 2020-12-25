package com.villagebanking

import java.time.DateTimeException
import java.util.*

class Model {
    var accountHoldersID: Int = 0
    var accountHoldersName: String = ""
    var accountHoldersAdmin: String = ""
    var accountHolderContact: String = ""
    var accountHolderBankInfo: String = ""
    var accountHolderPin: String = ""
    var accountHolderPinHint: String = ""
    var accountHoldersShare: Double = 0.00
    var accountHoldersLoanApp: Double = 0.0
    var accountHoldersCharges: Double = 0.0

    var transactionID: Int = 0
    var transactionName: String = ""
    var transactionMonth: String = ""
    var transactionShares: Double = 0.00
    var transactionShareAmount : Double = 0.00
    var transactionSharePayment: Double = 0.00
    var transactionShareDate: String = ""
    var transactionLoanApp: Double = 0.00
    var transactionLoanPayment: Double = 0.00
    var transactionLoanPaymentDate: String = ""
    var transactionLoanToRepay : Double = 0.0
    var transactionLoanRepayment: Double = 0.00
    var transactionLoanRepaymentDate: String = ""
    var transactionChargeName: String = ""
    var transactionCharge: Double = 0.00
    var transactionChargePayment: Double = 0.00
    var transactionChargePaymentDate: String = ""
    var transactionShareOut: Double = 0.00
    var transactionInterest: Double = 0.00

    var settingsShareValue: Double = 0.00
    var settingsInterestRate: Int = 0
    var settingsNotes: String = ""
}






