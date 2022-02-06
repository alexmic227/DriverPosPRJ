package mt.tts.driverpos.Controller

import android.R.attr.bitmap
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.view.View
import android.widget.Toast
import com.github.gcacace.signaturepad.views.SignaturePad
import kotlinx.android.synthetic.main.activity_delivery_note.*
import kotlinx.android.synthetic.main.activity_delivery_note.scr02_DelvDistNme
import kotlinx.android.synthetic.main.activity_delivery_note.scr02_DelvDistQty
import kotlinx.android.synthetic.main.activity_delivery_note.scr02_DelvNoteAddrTxt
import kotlinx.android.synthetic.main.activity_delivery_note.scr_02_Main
import kotlinx.android.synthetic.main.activity_delivery_note.scr_02_Print_Btn
import kotlinx.android.synthetic.main.activity_sign.*
import mt.tts.driverpos.Model.Customer
import mt.tts.driverpos.R
import mt.tts.driverpos.Utilities.BitmapUtils
import mt.tts.driverpos.Utilities.DataStore
import java.io.ByteArrayOutputStream
import java.util.*


class SignatureActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        var ccode = intent.getStringExtra("ClientCode")
        var qty_s = intent.getStringExtra("QTY")

        var customer = DataStore.GetCustomer(ccode.toString())

        scr02_DelvDistNme.text = customer.ClientCode
        scr02_DelvNoteAddrTxt.text = customer.Address
        scr02_DelvDistQty.text = qty_s

        var signed = false

        var mSignaturePad = findViewById<View>(R.id.signature_pad) as SignaturePad
        mSignaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                //Event triggered when the pad is touched
                signed = true
            }

            override fun onSigned() {
                //Event triggered when the pad is signed
                signed = true
            }

            override fun onClear() {
                //Event triggered when the pad is cleared
                signed = false
            }
        })

        scr_02_Main.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }

        scr_02_Print_Btn.setOnClickListener {
            if (!signed)
            {
                Toast.makeText(applicationContext,"Please sign to continue!", android.widget.Toast.LENGTH_LONG).show()
            }
            else
            {
                //Get the signature as BMP and converting to Base64 string
                var bitmap = mSignaturePad.getTransparentSignatureBitmap(true)

                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                val encodedbmp: String = encodeToString(byteArray, DEFAULT)

                var zplBitmap = BitmapUtils.getResizedBitmap(mSignaturePad.signatureBitmap, 400, 200)
                //We convert Image to ZPL
                //var grayBmp = BitmapUtils.toGrayScale(bitmap)
                var zplimg = BitmapUtils.getZplCode(zplBitmap, false)

                val deliveryNote = Intent(this, DeliveryNote::class.java)
                deliveryNote.putExtra("ClientCode", ccode)
                deliveryNote.putExtra("QTY", qty_s)
                deliveryNote.putExtra("BMP", encodedbmp)
                deliveryNote.putExtra("ZPLIMG", zplimg)
                deliveryNote.putExtra("ADDR", customer.Address)
                startActivity(deliveryNote)
            }
        }


    }
}