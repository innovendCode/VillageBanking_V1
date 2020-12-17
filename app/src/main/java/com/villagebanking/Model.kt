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
    var transactionShareDate: String = ""
    var transactionLoan: Double = 0.00
    var transactionLoanDate: String = ""
    var transactionShareAmount : Double = 0.00
    var transactionSharePaid: Double = 0.00

    var settingsShareValue: Double = 0.00
    var settingsInterestRate: Int = 0
    var settingsNotes: String = ""
}






