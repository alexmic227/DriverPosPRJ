package mt.tts.driverpos.Controller

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.strictmode.InstanceCountViolation
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_delivery_note.scr_02_Main
import kotlinx.android.synthetic.main.activity_settings.*
import mt.tts.driverpos.R

//Page: ADMINISTRATION
//Section: CLEAR EXPORT FILE


class ClearExportFile : BaseActivity() {


        override fun onCreate(savedInstancesState: Bundle?) {
        super.onCreate(savedInstancesState)
        setContentView(R.layout.activity_admin)


        btnClrExportFile.setOnClickListener {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(mainActivityIntent)
        }


        }
}