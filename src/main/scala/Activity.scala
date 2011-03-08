package com.buycheapviagraonlinenow.android.textconverter

import _root_.android.app.Activity
import _root_.android.app.ProgressDialog
import _root_.android.content.DialogInterface
import _root_.android.app.AlertDialog
import _root_.android.os.Bundle
import _root_.android.os.Handler
import _root_.android.os.Message
import _root_.android.content.Intent
import _root_.android.content.Context
import _root_.java.net._
import _root_.java.io._
import _root_.com.twitter.json._

class TestActivity extends Activity {
  var self = this
  var handler = new Handler {
    override def handleMessage(message:Message) {
      var services = message.getData().get("services").asInstanceOf[Array[CharSequence]]
      var text = message.getData().get("text")
      var dialog = new AlertDialog.Builder(self)
      var id = 0

      dialog.setTitle("Pick a service")
      dialog.setItems(services, new DialogInterface.OnClickListener {
        override def onClick(dialog:DialogInterface, item:Int) {
          var intent = new Intent("com.buycheapviagraonlinenow.android.textconverter.CONVERT");
          intent.setClassName("com.buycheapviagraonlinenow.android.textconverter", "com.buycheapviagraonlinenow.android.textconverter.Convert");
          intent.putExtra(Constant.SERVICE_NAME, services(item).asInstanceOf[String])
          intent.putExtra(Constant.CONVERTED_TEXT, text.asInstanceOf[String])
          startActivityForResult(intent, 0);
        }
      })
      dialog.show
    }
  }

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    var intent = getIntent();
    var text = intent.getStringExtra(Constant.REPLACE_KEY)
    var progressDialog = new ProgressDialog(this)
    var self = this

    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
    progressDialog.setMessage("Loading services ...")
    progressDialog.show

    new Thread(new Runnable {
      override def run {
        try {
          var services = getServices
          var message = new Message
          var bundle = new Bundle

          bundle.putCharSequenceArray("services", services);
          bundle.putString("text", text);
          message.setData(bundle);
          handler.sendMessage(message);
          progressDialog.dismiss
        } catch {
          case e => nop
        }
      }
    }).start
  }

  override def onActivityResult(req:Int, res:Int, intent:Intent) {
    setResult(Activity.RESULT_OK, intent)

    finish
    System.exit(Activity.RESULT_OK)
  }

  private def nop {}

  private def getServices:Array[CharSequence] = {
    var data = if(isDataStored) loadDataFromFile else loadDataFromURL

    if(!isDataStored) {
      saveDataToFile(data)
    }

    data
  }

  private def isDataStored:Boolean = {
    fileList.exists(_ == Constant.SERVICES_FILENAME)
  }

  private def loadDataFromURL:Array[CharSequence] = {
    var url = new URL("http://wedata.net/databases/Text%20Conversion%20Services/items.json")
    var in = new BufferedReader(new InputStreamReader(url.openStream))
    var responseText = ""
    var inputLine = ""

    /* http://www.ne.jp/asahi/hishidama/home/tech/scala/expression.html#h_let */
    while({inputLine = in.readLine; inputLine != null}) {
      responseText += inputLine
    }

    in.close

    var data = Json.parse(responseText).asInstanceOf[List[Map[String, Any]]]
    var result = new Array[CharSequence](data.length)
    var i = 0

    data.foreach(service => {
      result(i) = service.get("name").get.asInstanceOf[CharSequence]
      i += 1
    })

    result
  }

  private def loadDataFromFile:Array[CharSequence] = {
    var is = openFileInput(Constant.SERVICES_FILENAME);
    var bytes = new Array[Byte](is.available)

    is.read(bytes)
    is.close

    var data = Json.parse(new String(bytes)).asInstanceOf[List[CharSequence]]
    var result = new Array[CharSequence](data.length)
    var i = 0

    data.foreach(service => {
      result(i) = service
      i += 1
    })

    result
  }

  private def saveDataToFile(data:Array[CharSequence]) {
    var os = openFileOutput(Constant.SERVICES_FILENAME, Context.MODE_PRIVATE)
    os.write(Json.build(data).toString.getBytes)
    os.close
  }
}
