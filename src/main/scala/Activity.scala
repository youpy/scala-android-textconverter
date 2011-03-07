package com.buycheapviagraonlinenow.senryu

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
  private[this] object Constant {
    var CONVERTED_TEXT = "converted_text"
    var SERVICE_NAME = "service_name"
    var REPLACE_KEY = "replace_key"
    var SERVICES_FILENAME = "services.json"
  }

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
          var intent = new Intent("com.buycheapviagraonlinenow.senryu.CONVERT");
          intent.setClassName("com.buycheapviagraonlinenow.senryu", "com.buycheapviagraonlinenow.senryu.Convert");
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

    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
    progressDialog.setMessage("Loading services ...")
    progressDialog.show

    var self = this

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
    var stringData = ""

    if(isDataStored) {
      stringData = loadDataFromFile
    } else {
      stringData = loadDataFromURL
      saveDataToFile(stringData)
    }

    var data = Json.parse(stringData).asInstanceOf[List[Map[String, Any]]]
    var result = new Array[CharSequence](data.length)
    var i = 0

    data.foreach(service => {
      result(i) = service.get("name").get.asInstanceOf[CharSequence]
      i += 1
    })

    result
  }

  private def isDataStored:Boolean = {
    fileList.exists(_ == Constant.SERVICES_FILENAME)
  }

  private def loadDataFromURL:String = {
    var url = new URL("http://wedata.net/databases/Text%20Conversion%20Services/items.json")
    var in = new BufferedReader(new InputStreamReader(url.openStream))
    var result = ""
    var inputLine = ""

    /* http://www.ne.jp/asahi/hishidama/home/tech/scala/expression.html#h_let */
    while({inputLine = in.readLine; inputLine != null}) {
      result += inputLine
    }

    in.close

    result
  }

  private def loadDataFromFile:String = {
    var is = openFileInput(Constant.SERVICES_FILENAME);
    var bytes = new Array[Byte](is.available)
    is.read(bytes)
    is.close
    new String(bytes)
  }

  private def saveDataToFile(data:String) {
    var os = openFileOutput(Constant.SERVICES_FILENAME, Context.MODE_PRIVATE)
    os.write(data.getBytes)
    os.close
  }
}
