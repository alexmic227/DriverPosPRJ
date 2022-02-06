package mt.tts.driverpos.Controller

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_delivery_note.*
import kotlinx.android.synthetic.main.activity_delivery_note.scr_02_Main
import kotlinx.android.synthetic.main.activity_exportfile.*
import kotlinx.android.synthetic.main.activity_files.*
import java.io.File
import mt.tts.driverpos.R
import java.util.*
import kotlin.collections.ArrayList


class FilesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)

        var files_s: ArrayList<String> = ArrayList()

        val path: String = applicationContext.getFilesDir().toString() + "/DriverPOSData/"
        val directory = File(path)
        val files: Array<File> = directory.listFiles()
        for (i in files.indices) {
            files_s.add(files[i].getName())
        }

        val fileslist: List<String> = ArrayList<String>(files_s)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileslist)
        list_view.setAdapter(arrayAdapter)

        scr_02_Main.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }

        var selectedFile = ""

        list_view.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val selectedFromList = list_view.getItemAtPosition(position) as String
            selectedFile = selectedFromList
        })
        exportBtn.setOnClickListener {
            if (selectedFile == "")
            {
                Toast.makeText(applicationContext, "Please select a file to export", android.widget.Toast.LENGTH_LONG).show()
            }
            else {
                val settings: SharedPreferences = applicationContext.getSharedPreferences(
                    "driverpos",
                    MODE_PRIVATE
                )
                val driverID: String = settings.getString("driverid", "X").toString()
                val shareEmail: String = settings.getString("shareemail", "").toString()

                var dt = Date()

                val filepath = path + selectedFile.toString()

                val exportFile = File(filepath)

                if (exportFile.exists()) {
                    val intentShareFile = Intent(Intent.ACTION_SEND)

                    val exportUri = FileProvider.getUriForFile(
                        this,
                        "$packageName.provider",
                        exportFile
                    )

                    intentShareFile.type = "text/plain"
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, exportUri)
                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Export File ...")
                    intentShareFile.putExtra(Intent.EXTRA_EMAIL, Array<String>(1) { shareEmail })
                    intentShareFile.putExtra(Intent.EXTRA_TEXT,
                        "POS File Export from driver $driverID on " + DateFormat.format(
                            "yyyy-MM-dd hh:mm:ss",
                            dt
                        ).toString() + "."
                    )
                    startActivity(intentShareFile)
                }
            }
        }
    }
}