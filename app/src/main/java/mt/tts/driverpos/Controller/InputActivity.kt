package mt.tts.driverpos.Controller

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.opencsv.CSVReader
import kotlinx.android.synthetic.main.activity_loadcsv.*
import mt.tts.driverpos.Model.Customer
import mt.tts.driverpos.R
import mt.tts.driverpos.Utilities.*
import java.io.*

class InputActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loadcsv)

        findViewById<Button>(R.id.button_loadCsv)?.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/*"
            startActivityForResult(intent, READ_REQUEST_CODE)
        }

        scr_02_Main.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.let { intent ->
                try {
                    DataStore.instance.customers.clear() //we clear all customers in memory

                    val settings: SharedPreferences = applicationContext.getSharedPreferences("driverpos", MODE_PRIVATE)
                    val driverID: String = settings.getString("driverid", "").toString()

                    val uri = Uri.parse(intent.data.toString());
                    val inputStream: InputStream? = contentResolver.openInputStream(uri)
                    val inputStream2: InputStream? = contentResolver.openInputStream(uri) //copy for saving in internal storage

                    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                    ReadCSV.ReadClientsCSV(bufferedReader, driverID)
                    inputStream?.close()

                    val bufferedReader2 = BufferedReader(InputStreamReader(inputStream2))
                    var sBody = bufferedReader2.readText()
                    inputStream2?.close()

                    //If all is well till here we make a copy of the file to internal app storage
                    val dir = File(applicationContext.getFilesDir(), "DriverPOSData")
                    if (!dir.exists()) {
                        dir.mkdir()
                    }

                    //If Export file exists we delete
                    try {
                        val exportFile = File(dir, "output.csv")
                        if (exportFile.exists())
                        {
                            exportFile.delete()
                        }
                    } catch (e: Exception) {
                    }

                    //We make a copy of the input CSV file in case of application shutdown to resume
                    try {
                        val ffile = File(dir, "clients.csv")
                        val writer = FileWriter(ffile)
                        writer.append(sBody)
                        writer.flush()
                        writer.close()
                    } catch (e: Exception) {
                    }
                } catch (eio: IOException) {
                    Log.d("dposinput", eio.toString())
                }
                catch (e: Exception) {
                    Toast.makeText(applicationContext, "Error " + e.message, Toast.LENGTH_LONG).show()
                }

                val num = DataStore.instance.customers.count()
                csvPrompt.text = "Loaded " + num.toString() + " customers"
                if (num > 0) {
                    Toast.makeText(applicationContext, "Loaded " + num.toString() + " customers", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        const val READ_REQUEST_CODE = 123
    }

}