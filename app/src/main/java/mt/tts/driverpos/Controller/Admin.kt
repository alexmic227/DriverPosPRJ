package mt.tts.driverpos.Controller

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_admin.*
import mt.tts.driverpos.R
import java.io.File


class Admin : BaseActivity() {
//this is to test GIT - 4TH and 5th upl successfully //
    private var m_Text = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val settings: SharedPreferences =
            applicationContext.getSharedPreferences("driverpos", MODE_PRIVATE)

        //========================================================================================================
        //Password Popup
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Passcode")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog,
                                                                          which ->
            m_Text = input.text.toString()
            val passcode: String = settings.getString("passcode", "12345").toString()
            if (!m_Text.equals(passcode)) {
                dialog.cancel()
                val mainActivityIntent = Intent(this, MainActivity::class.java)
                startActivity(mainActivityIntent)
            }
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog,
                                                                              which ->
            dialog.cancel()
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
        })

        builder.setOnCancelListener {

            val passcode: String = settings.getString("passcode", "12345").toString()
            if (!m_Text.equals(passcode)) {
                val mainActivityIntent = Intent(this, MainActivity::class.java)
                startActivity(mainActivityIntent)
            }
        }
        builder.show()
        //========================================================================================================


        btnExport.setOnClickListener {
            val export = Intent(this, ExportFile::class.java)
            startActivity(export)
        }

        scr01_Import_Btn.setOnClickListener {
            val importCSVFile = Intent(this, InputActivity::class.java)
            startActivity(importCSVFile)
        }

        btnSettings.setOnClickListener {
            val settings = Intent(this, SettingsSave::class.java)
            startActivity(settings)
        }

        btnFiles.setOnClickListener {
            val filesStorage = Intent(this, FilesActivity::class.java)
            startActivity(filesStorage)
        }

        btnClrExportFile.setOnClickListener {
//            val filesStorage = Intent(this, FilesActivity::class.java)
//            startActivity(filesStorage)

//            val mainActivityIntent = Intent(this, MainActivity::class.java)
//            startActivity(mainActivityIntent)

            //Password Popup
            val builderCEF: AlertDialog.Builder = AlertDialog.Builder(this)
            builderCEF.setTitle("Enter Secret Code")

            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            builderCEF.setView(input)
            builderCEF.setPositiveButton("OK", DialogInterface.OnClickListener { dialog,
                                                                              which ->
                m_Text = input.text.toString()
                val passcode: String = settings.getString("passcode", "227").toString()
                if (!m_Text.equals(passcode)) {
                    dialog.cancel()
                    val mainActivityIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainActivityIntent)
                }
            })
            builderCEF.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog,
                                                                                  which ->
                dialog.cancel()
                val mainActivityIntent = Intent(this, MainActivity::class.java)
                startActivity(mainActivityIntent)
            })

            builderCEF.setOnCancelListener {

                val passcode: String = settings.getString("passcode", "227").toString()
                if (!m_Text.equals(passcode)) {
                    val mainActivityIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainActivityIntent)
                }
            }
            builderCEF.show()
            //If all is well till here we make a copy of the file to internal app storage

            val dir = File(applicationContext.getFilesDir(), "DriverPOSData")
            if (!dir.exists()) {
                dir.mkdir()
            }
            try {
                val exportFile = File(dir, "output.csv")
                if (exportFile.exists())
                {
                    exportFile.delete()
                }
            } catch (e: Exception) {
            }
            Toast.makeText(applicationContext, "DELETED Export file", android.widget.Toast.LENGTH_LONG).show()



        }

        btnMain.setOnClickListener() {
                val mainActivityIntent = Intent(this, MainActivity::class.java)
                startActivity(mainActivityIntent)
            }
    }
}