package mt.tts.driverpos.Controller

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.text.format.DateFormat
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.FileProvider
import com.opencsv.bean.CsvCustomBindByName
import kotlinx.android.synthetic.main.activity_delivery_note.*
import kotlinx.android.synthetic.main.activity_delivery_note.scr_02_Main
import kotlinx.android.synthetic.main.activity_settings.*
import mt.tts.driverpos.R
import mt.tts.driverpos.Utilities.DataStore
import java.io.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*


class DeliveryNote : BaseActivity() {
//single spaces
    fun _fw(ss : String) : String
    {
        var s = ss
        do {
            s = " " + s
        } while (s.length <= 10)

        return s;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_note)


        var ccode = intent.getStringExtra("ClientCode")
        var qty_s = intent.getStringExtra("QTY")

        var customer = DataStore.GetCustomer(ccode.toString())
        scr02_DelvDistNme.text = customer.ClientCode
        scr02_DelvNoteAddrTxt.text = customer.Address
        scr02_DelvDistQty.text = qty_s

        val settings: SharedPreferences = applicationContext.getSharedPreferences(
                "driverpos",
                MODE_PRIVATE
        )

//driverID and driverName are vals reading values from SettingSave.kt. These vals will be used for
// for printing and also form part of the filenames
        val driverID: String = settings.getString("driverid", "X").toString()
        val driverName: String = settings.getString("drivernme", "Alexander Micallef").toString()

        var dt = Date()

        var qty = qty_s.toString().toDouble()
        var price = customer.Price.toDouble()
        var netprice = price.times(qty.toDouble())
//        var priceD = BigDecimal(price).setScale(4, RoundingMode.HALF_EVEN)
        var netpriceD = BigDecimal(netprice).setScale(2, RoundingMode.HALF_EVEN)
        var vatRate = 18
        var vatAmount = netprice * vatRate / 100
        var totalPrice = netprice + vatAmount
// 10-NOV-21
        var typeCashInv = customer.CashInvoice
        var custName = customer.Address
        var custCode = customer.ClientCode

        var vatAmountD = BigDecimal(vatAmount).setScale(2, RoundingMode.HALF_EVEN)
        var totalPriceD = BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_EVEN)
        var custPriceD = BigDecimal(customer.Price).setScale(4, RoundingMode.HALF_EVEN)

// 10-NOV-21
//        var orderNumber = driverID + DateFormat.format("yyyyMMdd", dt).toString()
        var orderNumber = driverID + DateFormat.format("ddMMhhmm", dt).toString()
        var orderDate = DateFormat.format("dd-MM-yyyy", dt).toString()
        var orderTime = DateFormat.format("hh:mm:ss a", dt).toString()

        var slip = "<table style='width:100%'>" +

                "<tr><td style='text-align:Centre'>EASYGAS Malta Ltd</td><td style='text-align:right'>"+
                "<tr><td style='text-align:left'>Valletta Road,</td><td style='text-align:right'>"+
                "<tr><td style='text-align:left'>Luqa, </td><td style='text-align:right'>"+
                "<tr><td style='text-align:left'>Malta MRS3000</td><td style='text-align:right'>"+

                "<tr><td style='text-align:left'>Client:</td><td style='text-align:right'>" + customer.Address + "</td></tr>" +
                "<tr><td style='text-align:left'>Code:</td><td style='text-align:right'>" + customer.ClientCode + "</td></tr>" +


                "<tr><td style='text-align:left'>Order Number:</td><td style='text-align:right'>" + orderNumber + "</td></tr>" +
                "<tr><td style='text-align:left'>Date:</td><td style='text-align:right'>" + orderDate + "</td></tr>" +
                "<tr><td style='text-align:left'>Time:</td><td style='text-align:right'>" + orderTime + "</td></tr>" +
                "<tr><td style='text-align:left'>VAT Reg No:</td><td style='text-align:right'>" + customer.VAT + "</td></tr>" +
                "<tr><td colspan='2' style='border-bottom:solid'></td></tr>" +
                "<tr><td style='text-align:left'>Litres:</td><td style='text-align:right'>" + qty_s + "</td></tr>" +
                "<tr><td style='text-align:left'>Unit Price:</td><td style='text-align:right'>" + custPriceD + "</td></tr>" +
                "<tr><td style='text-align:left'>Net Price:</td><td style='text-align:right'>" + netpriceD + "</td></tr>" +
                "<tr><td style='text-align:left'>VAT Rate:</td><td style='text-align:right'>" + vatRate.toString() + "</td></tr>" +
                "<tr><td style='text-align:left'>Total VAT:</td><td style='text-align:right'>" + vatAmountD + "</td></tr>" +
                "<tr><td style='text-align:left'>Total:</td><td style='text-align:right'>" + totalPriceD + "</td></tr>" +
                "<tr><td colspan='2' style='border-bottom:solid'></td></tr>" +
                "<tr><td style='text-align:left'>Previous Balance:</td><td style='text-align:right'>" + customer.PreviousBalance + "</td></tr>" +
                "<tr><td style='text-align:left'>Payment Type:</td><td style='text-align:right'>" + customer.CashInvoice + "</td></tr>" +
                "<tr><td colspan='2' style='border-bottom:solid'></td></tr>" +

                "<tr><td style='text-align:left'>Driver: " + driverName + "</td><td style='text-align:right'>"+

                "</table>"

        var slipORG = "<table style='width:100%'>" +

                "<tr><td style='text-align:Centre'>EASYGAS Malta Ltd</td><td style='text-align:right'>"+
                "<tr><td style='text-align:left'>Valletta Road,</td><td style='text-align:right'>"+
                "<tr><td style='text-align:left'>Luqa, </td><td style='text-align:right'>"+
                "<tr><td style='text-align:left'>Malta MRS3000</td><td style='text-align:right'>"+

                "<tr><td style='text-align:left'>Order Number:</td><td style='text-align:right'>" + orderNumber + "</td></tr>" +
                "<tr><td style='text-align:left'>Date:</td><td style='text-align:right'>" + orderDate + "</td></tr>" +
                "<tr><td style='text-align:left'>Time:</td><td style='text-align:right'>" + orderTime + "</td></tr>" +
                "<tr><td style='text-align:left'>To:</td><td style='text-align:right'>" + customer.ClientCode + " " + customer.Address + "</td></tr>" +
                "<tr><td style='text-align:left'>VAT Reg No:</td><td style='text-align:right'>" + customer.VAT + "</td></tr>" +
                "<tr><td colspan='2' style='border-bottom:solid'></td></tr>" +
                "<tr><td style='text-align:left'>Litres:</td><td style='text-align:right'>" + qty_s + "</td></tr>" +
                "<tr><td style='text-align:left'>Unit Price:</td><td style='text-align:right'>" + custPriceD + "</td></tr>" +
                "<tr><td style='text-align:left'>Net Price:</td><td style='text-align:right'>" + netprice + "</td></tr>" +
                "<tr><td style='text-align:left'>VAT Rate:</td><td style='text-align:right'>" + vatRate.toString() + "</td></tr>" +
                "<tr><td style='text-align:left'>Total VAT:</td><td style='text-align:right'>" + vatAmountD + "</td></tr>" +
                "<tr><td style='text-align:left'>Total:</td><td style='text-align:right'>" + totalPriceD + "</td></tr>" +
                "<tr><td colspan='2' style='border-bottom:solid'></td></tr>" +
                "<tr><td style='text-align:left'>Previous Balance:</td><td style='text-align:right'>" + customer.PreviousBalance + "</td></tr>" +
                "<tr><td style='text-align:left'>Payment Type:</td><td style='text-align:right'>" + customer.CashInvoice + "</td></tr>" +
                "<tr><td colspan='2' style='border-bottom:solid'></td></tr>" +

                "<tr><td style='text-align:left'>Driver: George Fenech</td><td style='text-align:right'>"+

                "</table>"
        var slip1 = "<table style='width:100%'>" +
                "<tr><td style='text-align:left'>Order Number:</td><td style='text-align:right'>" + orderNumber + "</td></tr>" +
                "<tr><td style='text-align:left'>Date:</td><td style='text-align:right'>" + orderDate + "</td></tr>" +
                "<tr><td style='text-align:left'>Time:</td><td style='text-align:right'>" + orderTime + "</td></tr>" +
                "<tr><td style='text-align:left'>To:</td><td style='text-align:right'>" + customer.ClientCode + " " + customer.Address + "</td></tr>" +
                "<tr><td style='text-align:left'>VAT Reg No:</td><td style='text-align:right'>" + customer.VAT + "</td></tr>" +
                "<tr><td colspan='2' style='border-bottom:solid'></td></tr>" +
                "<tr><td style='text-align:left'>Litres:</td><td style='text-align:right'>" + qty_s + "</td></tr>" +
                "<tr><td style='text-align:left'>Unit Price:</td><td style='text-align:right'>" + custPriceD + "</td></tr>" +
                "<tr><td style='text-align:left'>Net Price:</td><td style='text-align:right'>" + netprice + "</td></tr>" +
                "<tr><td style='text-align:left'>VAT Rate:</td><td style='text-align:right'>" + vatRate.toString() + "</td></tr>" +
                "<tr><td style='text-align:left'>Total VAT:</td><td style='text-align:right'>" + vatAmountD + "</td></tr>" +
                "<tr><td style='text-align:left'>Total:</td><td style='text-align:right'>" + totalPriceD + "</td></tr>" +
                "<tr><td colspan='2' style='border-bottom:solid'></td></tr>" +
                "<tr><td style='text-align:left'>Previous Balance:</td><td style='text-align:right'>" + customer.PreviousBalance + "</td></tr>" +
                "<tr><td style='text-align:left'>Payment Type:</td><td style='text-align:right'>" + customer.CashInvoice + "</td></tr>" +
                "<tr><td colspan='2' style='border-bottom:solid'></td></tr>" +
                "</table>"


        //Delivery note is in HTML
        var header = "<head> </head>"
        var footer = ""

        var signaturebmp = intent.getStringExtra("BMP")
        var signatureimg = "<img width=\"100\" src=\"data:image/png;base64, " + signaturebmp + "\" />"

        val html = "<!DOCTYPE html>\r\n<html lang=\"en\">" + header + "<body style='font-size:10px;'> <h2>DriverPos</h2> <h3>Delivery Note</h3>" + slip + "<p>Signature:</p> " + signatureimg + " <p>Thank you for your custom.</p>" + footer + " </body></html>"

        val webSettings: WebSettings = printSlip.getSettings()
        webSettings.javaScriptEnabled = true
        printSlip.setWebViewClient(WebViewClient())
        printSlip.setWebChromeClient(WebChromeClient())
        printSlip.loadData(html, "text/html", "UTF8")

        val dir = File(applicationContext.getFilesDir(), "DriverPOSData")
        if (!dir.exists()) {
            dir.mkdir()
        }

        DeliveryNoteHTML = html

        //=====================================
        //ZPL STUFF

        var zplimg = intent.getStringExtra("ZPLIMG")

        //ZPL Template with Signature
        var zpl = "^XA\n" +
                "\n" +
                "^CF0,30\n" +
                "^FO50,10^FDEASYGAS Malta Ltd^FS\n" +
                "^FO50,40^FDValletta Road^FS\n" +
                "^FO50,80^FDLuqa, Malta MRS3000^FS\n" +
                "^FO50,120^FDVAT No: 1915-3809^FS\n" +
                "^CF0,30\n" +
                "^CF0,30\n" +
                "^FO50,200^FDClient: %CLIENTTOX%^FS\n" +
                "^FO50,240^FDVAT No: %VATXXXXXX%^FS\n" +
                "^FO50,320^FD                  Delivery Note^FS\n" +

                "^CFA,30\n" +
                "^FO50,400^FDNumber:       %ORDERNOXX%^FS\n" +
                "^FO50,440^FDDate:         %ORDERDATE%^FS\n" +
                "^FO50,480^FDTime:         %ORDERTIME%^FS\n" +
                "^FO50,520^GB700,3,3^FS\n" +
                "^FO50,560^FDLitres:       %QTYXXXXXX%^FS\n" +
                "^FO50,600^FDUnit Price:   %PRICEXXXX%^FS\n" +
                "^FO50,640^FDNet Price:    %NETPRICEX%^FS\n" +
                "^FO50,680^FDTotal VAT 18%:%VATAMOUNT%^FS\n" +
                "^FO50,720^FDTotal:        %TOTALPRIC%^FS\n" +
                "^FO50,800^GB700,3,3^FS\n" +
                "^FO50,840^FDPrev. Balance: %PBALANCE%^FS\n" +
                "^FO50,900^GB700,3,3^FS\n" +
                "^FO50,940^FDSignature^FS\n" +
                "^FO50,980"+zplimg+"^FS\n" +
                "^FO50,1200^GB700,3,3^FS\n" +
                "^FO50,1240^FDDriver:       %DRIVERNM% ^FS\n" +
                "\n" +
                "^XZ"
//                "^FO50,960^FDPayment Type:  %PTYPEXXX%^FS\n" +
//           "^FO50,680^FDVAT Rate:     %VATRATEXX%^FS\n" +
//                "^FO50,1240^FDDriver: George Fenech ^FS\n" +
//
        val pm = packageManager
        try {
            pm.getPackageInfo("com.zebra.printconnect", PackageManager.GET_ACTIVITIES)
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(getApplicationContext(), "Please Install Zebra PrintConnect ", Toast.LENGTH_LONG).show();
        }

        val variableData: HashMap<String, String> = HashMap()
        variableData.put("%ORDERNOXX%", _fw(orderNumber))
        variableData.put("%ORDERDATE%", _fw(orderDate))
        variableData.put("%ORDERTIME%", _fw(orderTime))
        variableData.put("%CLIENTTOX%", _fw(customer.Address))
        variableData.put("%VATXXXXXX%", _fw(customer.VAT))
        variableData.put("%QTYXXXXXX%", _fw(qty_s.toString()))
        variableData.put("%PRICEXXXX%", _fw(custPriceD.toString()))
        variableData.put("%NETPRICEX%", _fw(netprice.toString()))
        variableData.put("%VATRATEXX%", _fw(vatRate.toString()))
        variableData.put("%VATAMOUNT%", _fw(vatAmountD.toString()))
        variableData.put("%TOTALPRIC%", _fw(totalPriceD.toString()))
        variableData.put("%PBALANCE%", _fw(customer.PreviousBalance.toString()))
        variableData.put("%PTYPEXXX%", _fw(customer.CashInvoice))
        variableData.put("%DRIVERNM%", _fw(driverName))

        //driverName

        //==========

        //We make a copy of the delivery Note (html file)
        try {
            val ffile = File(
                    dir,
            //        "O_" + custCode +  orderNumber + "_" + DateFormat.format("hhmmss", dt).toString() + ".html"
                //09 FEB22 - Client Name and Order number - Requested  by Ruth
                "O_" + custName + "  " + orderNumber + ".html"
            )
            val writer = FileWriter(ffile)
            writer.append(html)
            writer.flush()
            writer.close()

            DeliveryNoteShareFile = ffile
        } catch (e: Exception) {
        }

        scr_02_Main.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }

        scr_02_Print_Btn.setOnClickListener {
//on pressing print button the following process takes place.
// Check if record is already locked, if so, pop message, if not, copy record to export file and mark it locked.
// The first step is to print the Chit using function createZPLPrint(zpl, variableData)
// the next step is to save the file on Storage by invoking function ShareFile()


            ShareEmail = settings.getString("shareemail", "").toString()
            TStamp = orderNumber
            CustomerEmail = customer.Email
            DriverID = driverID
            DT = dt
            DriverNM = driverName
            //09FEB22
            CustCode = customer.ClientCode
            CustName = customer.Address
            //var success = createWebPrintJob(printSlip, orderNumber)
            var success = createZPLPrint(zpl, variableData)
            if (success)
            {
                shareFile()

                //We save the order to output CSV file
                if (!customer.Locked) {
                    try {
                        val outputFilename: String = applicationContext.getFilesDir().toString() + "/DriverPOSData/" + "output.csv"
                        val outputFile = FileWriter(outputFilename, true)
//10-NOV-21  - typeCashInv
//                        outputFile.write(customer.ClientCode + "," + orderDate + "," + orderTime + "," + orderNumber + "," + qty_s + "," + totalPrice + "," + typeCashInv + "\r\n");
                        outputFile.write(customer.Address +"," +customer.ClientCode + ","  + qty_s + ","  + typeCashInv + ","+ price + "," + orderDate + "," + orderNumber + ","+ "\r\n");
                        outputFile.flush();
                        outputFile.close();
                    } catch (e: Exception) {
                    }
                }

                customer.Locked = true //entry is now locked
            }
        }
    }

    private fun buildIPCSafeReceiver(actualReceiver: ResultReceiver): ResultReceiver? {
 //https://stackoverflow.com/questions/64222509/delphi-10-3-android-intent-to-zebra-printer-with-hashmap
 //this function is needed by the Zebra printer to print variable data
        val parcel = Parcel.obtain()
        actualReceiver.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val receiverForSending = ResultReceiver.CREATOR.createFromParcel(parcel)
        parcel.recycle()
        return receiverForSending
    }

    fun createZPLPrint(zpl: String, variableData: HashMap<String, String>) : Boolean {
        try {
            val charset = Charsets.UTF_8
            val zplByteArray = zpl.toByteArray(charset)

            val zebraintent = Intent()
            zebraintent.setComponent(ComponentName("com.zebra.printconnect", "com.zebra.printconnect.print.TemplatePrintWithContentService"))
            zebraintent.putExtra("com.zebra.printconnect.PrintService.TEMPLATE_DATA", zplByteArray)
            zebraintent.putExtra("com.zebra.printconnect.PrintService.VARIABLE_DATA", variableData);
            zebraintent.putExtra("com.zebra.printconnect.PrintService.RESULT_RECEIVER", buildIPCSafeReceiver(object : ResultReceiver(null) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                    if (resultCode == 0) { // Result code 0 indicates success
                        // Handle successful print
                    } else {
                        // Handle unsuccessful print
                        // Error message (null on successful print)
                        val errorMessage = resultData.getString("com.zebra.printconnect.PrintService.ERROR_MESSAGE")
                        val x = 5;
                    }
                }
            }))

            startService(zebraintent)

        } catch (e: Exception) {
            Toast.makeText(getApplicationContext(), "Error sending print job to Zebra Printer ", Toast.LENGTH_LONG).show();
        }
        return true
    }

    fun createWebPrintJob(webView: WebView, orderNum: String) : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val printManager = this.getSystemService(Context.PRINT_SERVICE) as PrintManager

            val printAdapter: PrintDocumentAdapter = CustomPDAdapterWrapper(webView.createPrintDocumentAdapter("DeliveryNote" + orderNum), ::shareFile)

            printManager.print(
                    "DeliveryNote" + orderNum,
                    printAdapter,
                    PrintAttributes.Builder().build()
            )

            return true
        } else {
            Toast.makeText(getApplicationContext(), "Failed to Print", Toast.LENGTH_LONG).show();
            return false
        }
    }

    private lateinit var DeliveryNoteShareFile: File
    private lateinit var DeliveryNoteHTML: String
    private lateinit var ShareEmail: String
    private lateinit var CustomerEmail: String
    private lateinit var TStamp: String
    private lateinit var DriverID: String
    private lateinit var DT: Date
    private lateinit var DriverNM: String
    private lateinit var CustCode: String
    private lateinit var CustName: String
    private val WRITE_REQUEST_CODE = 101

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WRITE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK ->
                    if (data != null && data.data != null) {
                        writeInFile(data.data!!, DeliveryNoteHTML)
                    }
                RESULT_CANCELED -> {
                }
            }
        }
    }

    private fun writeInFile(uri: Uri, text: String) {
        val outputStream: OutputStream?
        try {
            outputStream = contentResolver.openOutputStream(uri)
            val bw = BufferedWriter(OutputStreamWriter(outputStream))
            bw.write(text)
            bw.flush()
            bw.close()
        } catch (e: IOException) {
        }
    }

    fun saveFile()
    {
        //This function is called after Sending Email is Done
        //We Save the Delivery Note to storage

        val intentShareFile = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intentShareFile.addCategory(Intent.CATEGORY_OPENABLE)
        intentShareFile.type = "text/html"
       // intentShareFile.putExtra(Intent.EXTRA_TITLE, "DeliveryNote" + CustCode + " " + DriverNM + " " +  TStamp + ".html")
        intentShareFile.putExtra(Intent.EXTRA_TITLE, "DeliveryNote" + CustName + " " + DriverNM + " " +  TStamp + ".html")
        startActivityForResult(intentShareFile, WRITE_REQUEST_CODE);
    }

    fun shareFile()
    {
        //This function is called after Printing is Finished

        //=============================================================================
        saveFile() //if saving file on device storage is not required comment this line
        //=============================================================================

        //We Share the Delivery Note via email
        val intentShareFile = Intent(Intent.ACTION_SEND)
        val exportUri = FileProvider.getUriForFile(
                this,
                "$packageName.provider",
                DeliveryNoteShareFile)

        intentShareFile.type = "text/html"
        intentShareFile.putExtra(Intent.EXTRA_STREAM, exportUri)
        intentShareFile.putExtra(Intent.EXTRA_EMAIL, arrayOf(ShareEmail, CustomerEmail))
 //       intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Delivery Note ( " + " " + CustCode +"_"+ DriverNM + "_"+ TStamp + ")...")
 // 09 FEB22
        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Delivery Note ( " + " " + CustName +"_"+ TStamp + ")...")
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Delivery Note Export from driver " + DriverNM + " on " + DateFormat.format("dd-MM-yyyy hh:mm:ss", DT).toString() + " for order " + TStamp + ".")
        startActivity(intentShareFile)
    }
//        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Delivery Note Export from driver " + DriverID + " on " + DateFormat.format("yyyy-MM-dd hh:mm:ss", DT).toString() + " for order " + TStamp + ".")

}

class CustomPDAdapterWrapper(private val delegate: PrintDocumentAdapter, private val sharefin: () -> (Unit)) : PrintDocumentAdapter() {
    override fun onFinish() {
        delegate.onFinish()
        sharefin(); //share the file on finish printing
    }

    override fun onLayout(oldAttributes: PrintAttributes?, newAttributes: PrintAttributes?, cancellationSignal: CancellationSignal?, callback: LayoutResultCallback?, extras: Bundle?) {
        delegate.onLayout(oldAttributes, newAttributes, cancellationSignal, callback, extras)
    }

    override fun onStart() {
        delegate.onStart()
    }

    override fun onWrite(pages: Array<out PageRange>?, destination: ParcelFileDescriptor?, cancellationSignal: CancellationSignal?, callback: WriteResultCallback?) {
        delegate.onWrite(pages, destination, cancellationSignal, callback)
    }

}