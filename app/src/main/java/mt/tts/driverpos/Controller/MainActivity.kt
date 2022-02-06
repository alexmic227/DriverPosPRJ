package mt.tts.driverpos.Controller

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import mt.tts.driverpos.R
import mt.tts.driverpos.Utilities.DataStore
import mt.tts.driverpos.Utilities.ReadCSV
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


class MainActivity : BaseActivity() {

    var DSCpos = 0 //Position in Customer Array
    val DSC = DataStore.instance.customers

    fun displayActive()
    {
        if (DSC.count() > 0)
        {
            var cust = DSC[DSCpos]
            scr01_DistName.text = cust.ClientCode + " - Qty: " + cust.Litres + "\n" + cust.Address

            if (cust.Qty > 0)
                scr01_Qty_text.setText(cust.Qty.toString())
            else
                scr01_Qty_text.setText("") //show blank instead of 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            if (DSC.count() == 0) //if no data then we have to load
            {
                val settings: SharedPreferences = applicationContext.getSharedPreferences("driverpos", MODE_PRIVATE)
                val driverID: String = settings.getString("driverid", "").toString()

                //if this Fails we load from Internal Storage
                val clientsFilename: String = applicationContext.getFilesDir().toString() + "/DriverPOSData/" + "clients.csv"
                val clientsFile = File(clientsFilename)
                if (clientsFile.exists()) { //load from internal storage
                    val bufferedReader = BufferedReader(InputStreamReader(clientsFile.inputStream()))
                    ReadCSV.ReadClientsCSV(bufferedReader, driverID)

                    //Read Output CSV in case of application start to Lock records
                    val outputFilename: String = applicationContext.getFilesDir().toString() + "/DriverPOSData/" + "output.csv"
                    val outputFile = File(outputFilename)
                    if (outputFile.exists()) { //load from internal storage
                        val bufferedReaderO = BufferedReader(InputStreamReader(outputFile.inputStream()))
                        ReadCSV.ReadOutputCSV(bufferedReaderO)
                    }

                } else { //ask user for file via Import Activity
                    val importCSVFile = Intent(this, InputActivity::class.java)
                    startActivity(importCSVFile)
                }
            }
        }
        catch (e: java.lang.Exception)
        {
            Toast.makeText(applicationContext, "Error loading Clients file. Please Import", android.widget.Toast.LENGTH_LONG).show()
        }

        scr01_btnAdmin.setOnClickListener {
            val admin = Intent(this, Admin::class.java)
            startActivity(admin)
        }

        scr01_Print_Btn.setOnClickListener {

            if (DSC[DSCpos].Locked)
            {
                Toast.makeText(applicationContext, "Entry is Locked!", Toast.LENGTH_LONG).show()
            }
            else {
                val gasQty = scr01_Qty_text.text.toString()

                try {
                    if (TextUtils.isEmpty(gasQty)) {
                        Toast.makeText(applicationContext, "Missing Gas Quantity!", Toast.LENGTH_LONG).show()
                    } else {
                        var gq = gasQty.toDouble() //we only accept numbers
                        if (gq <= 0) {
                            Toast.makeText(applicationContext, "Gas Quantity cannot be 0!", android.widget.Toast.LENGTH_LONG).show()
                        } else {
                            DSC[DSCpos].Qty = gq //we set the quantity

                            val signActivity = Intent(this, SignatureActivity::class.java)
                            signActivity.putExtra("ClientCode", DSC[DSCpos].ClientCode)
                            signActivity.putExtra("QTY", gasQty)
                            signActivity.putExtra("ADDR", DSC[DSCpos].Address)

                            startActivity(signActivity)
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "Gas Quantity must be Number!", android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }

        displayActive()

        scr01_next_Btn.setOnClickListener {
            if (DSCpos < DSC.count()-1)
            {
                DSCpos++
                displayActive()
            }
        }

        scr01_prev_Btn.setOnClickListener {
            if (DSCpos > 0)
            {
                DSCpos--
                displayActive();
            }
        }

        scr01_find_Btn.setOnClickListener {
            val serstr = scr01_Search_text.text.toString()

            if (TextUtils.isEmpty(serstr))
            {
                Toast.makeText(applicationContext, "Please enter Search text first!", Toast.LENGTH_LONG).show()
            }
            else {
                var found = false;
                var xpos = 0;
                for (c in DSC) {
                    if ( (c.ClientCode.indexOf(serstr, 0, true) >= 0) ||
                            (c.Address.indexOf(serstr, 0, true) >= 0) ||
                            (c.Postcode.indexOf(serstr, 0, true) >= 0) )
                    {
                        found = true;
                        DSCpos = xpos
                        displayActive()
                        break
                    }

                    xpos++
                }

                if (!found)
                {
                    Toast.makeText(applicationContext, "Client not found. Please try again", Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}


