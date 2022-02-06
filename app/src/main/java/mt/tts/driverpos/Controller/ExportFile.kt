package mt.tts.driverpos.Controller

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_delivery_note.*
import kotlinx.android.synthetic.main.activity_exportfile.*
import mt.tts.driverpos.R
import java.io.*
import java.util.*


class
ExportFile : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exportfile)

        val outputFilename: String = applicationContext.getFilesDir().toString() + "/DriverPOSData/" + "output.csv"

        val exportFile1 = FileReader(outputFilename)
        val bufferedReader2 = BufferedReader(exportFile1)
        var sBody = bufferedReader2.readText()
        bufferedReader2.close()

        fileContents.setText(sBody)

        scr05_SavBtn.setOnClickListener {
            var dt = Date()
            var tstamp = DateFormat.format("yyyyMMdd_hhmmss", dt).toString()

            val settings: SharedPreferences = applicationContext.getSharedPreferences("driverpos", MODE_PRIVATE)
            val driverID: String = settings.getString("driverid", "X").toString()
            val shareEmail: String = settings.getString("shareemail", "").toString()

            val resultantOutput: String = applicationContext.getFilesDir().toString() + "/DriverPOSData/" + "DPOS_"+ tstamp +".csv"
            val resultExportFile = File(resultantOutput)

            val intentShareFile = Intent(Intent.ACTION_SEND)
            val exportFile = File(outputFilename)
            exportFile.copyTo(resultExportFile, true) //we make a copy of the file with a different filename (date based)

            if (exportFile.exists()) {
                val exportUri = FileProvider.getUriForFile(
                    this,
                    "$packageName.provider",
                    resultExportFile)

                intentShareFile.type = "text/csv"
                intentShareFile.putExtra(Intent.EXTRA_STREAM, exportUri)
                intentShareFile.putExtra(Intent.EXTRA_EMAIL, Array<String>(1) { shareEmail })
 //               intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Orders CSV (" + tstamp + ")...")
                intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sheet1")
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "POS Export from driver " + driverID +" on " + DateFormat.format("yyyy-MM-dd hh:mm:ss", dt).toString()+".")
                startActivity(intentShareFile)
            }
            else
            {
                Toast.makeText(applicationContext, "Error No Export file", android.widget.Toast.LENGTH_LONG).show()
            }
        }

        scr05_MainBtn.setOnClickListener {
             val mainActivityIntent = Intent(this, MainActivity::class.java)
             startActivity(mainActivityIntent)
        }

    }


}