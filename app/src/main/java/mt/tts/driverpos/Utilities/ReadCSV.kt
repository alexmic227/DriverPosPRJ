package mt.tts.driverpos.Utilities

import android.util.Log
import com.opencsv.CSVReader
import mt.tts.driverpos.Model.Customer
import java.io.BufferedReader
import java.lang.NullPointerException

class ReadCSV {
    companion object {
        fun ReadClientsCSV(bufferedReader: BufferedReader, driverID : String) {
            val reader = CSVReader(bufferedReader)
            var nextLine: Array<String>

            try {

                while (reader.readNext().also { nextLine = it } != null) {
                    // nextLine[] is an array of values from the line
//7 MAR 22
//                    if (nextLine[1].equals("driver", true))  {
//
 //                        if ((driverID != "") && (!(nextLine[2].equals(driverID, true))))
 //                       {
 //                           throw java.lang.Exception("Incorrect File - Driver ID Mismatch found " + nextLine[2] + " expected " + driverID)
 //                       }
 //                   }
 //                   else
                    if (!((nextLine[0].equals("address", true)) || (nextLine[1].equals("Postcode", true)))) {

                        var col4 = nextLine[4]
                        if (col4.isNullOrBlank()) col4 = "0"
                        var col6 = nextLine[6]
                        if (col6.isNullOrBlank()) col6 = "0"
                        var col7 = nextLine[7]
                        if (col7.isNullOrBlank()) col7 = "0"

                        val customr = Customer(nextLine[0],
                                nextLine[1],
                                nextLine[2],
                                nextLine[3],
                                col4.toDouble(),
                                nextLine[5],
                                col6.toDouble(),
                                col7.toDouble(),
                                nextLine[8],
                                nextLine[9])

                        DataStore.instance.customers.add(customr)
                    }
                }
            }
            catch (npe: NullPointerException) {
                //throw npe
            }
            catch (e: Exception) {
                Log.d("dposr", e.toString())
                //throw e
            }
            bufferedReader.close()
        }

        /*
        outputFile.write(customer.ClientCode + "," + orderDate + "," + orderTime + "," + orderNumber + "," + qty_s + "," + totalPrice + "\r\n");
         */

        fun ReadOutputCSV(bufferedReader: BufferedReader) {
            val reader = CSVReader(bufferedReader)
            try {
                var nextLine: Array<String>
                while (reader.readNext().also { nextLine = it } != null) {
                    // nextLine[] is an array of values from the line
                    if (!((nextLine[0].equals("ClientCode", true)) || (nextLine[1].equals("OrderDate", true)))) {
                        val clientCode = nextLine[0];
                        val qty_s = nextLine[4];

                        try {
                            var customer = DataStore.GetCustomer(clientCode)
                            customer.Qty = qty_s.toDouble()
                            customer.Locked = true
                        }
                        catch (e: Exception) {
                            Log.d("dposr", e.toString())
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("dposr", e.toString())
            }
            bufferedReader.close()
        }


    }
}