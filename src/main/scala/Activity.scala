package com.buycheapviagraonlinenow.senryu

import _root_.android.app.Activity
import _root_.android.app.ProgressDialog
import _root_.android.os.Bundle
import _root_.android.content.Intent
import _root_.java.net._
import _root_.java.io._

class TestActivity extends Activity {
  private[this] object Constant {
    var REPLACE_KEY = "replace_key"
  }

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    var progressDialog = new ProgressDialog(this)
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
    progressDialog.setMessage("Please wait for few seconds...")
    progressDialog.show

    new Thread(new Runnable {
      override def run {
        try {
          var senryu = getSenryu
          doFinish(senryu)
        } catch {
          case e => doFinish("")
        }
      }
    }).start
  }

  private[this] def doFinish(str: String) {
    var data = new Intent
    data.putExtra(Constant.REPLACE_KEY, str)
    setResult(Activity.RESULT_OK, data)

    finish
    System.exit(Activity.RESULT_OK)
  }

  private[this] def getSenryu:String = {
    var url = new URL("http://senryu.jgate.de/?" + System.currentTimeMillis)
    var in = new BufferedReader(new InputStreamReader(url.openStream))
    var responseText = ""
    var inputLine = ""

    /* http://www.ne.jp/asahi/hishidama/home/tech/scala/expression.html#h_let */
    while({inputLine = in.readLine; inputLine != null}) {
      responseText += inputLine
    }

    in.close

    responseText
  }
}
