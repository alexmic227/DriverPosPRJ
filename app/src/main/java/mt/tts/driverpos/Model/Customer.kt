package mt.tts.driverpos.Model

data class Customer(val Address : String, val Postcode : String , val ClientCode : String , val Locality : String , val Litres : Double , val CashInvoice : String ,val Price : Double , val PreviousBalance : Double, val VAT : String, val Email : String, var Qty : Double = 0.0, var Locked : Boolean = false)
