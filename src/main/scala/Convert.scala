package com.buycheapviagraonlinenow.android.textconverter

import _root_.android.app.Activity
import _root_.android.app.ProgressDialog
import _root_.android.os.Bundle
import _root_.android.content.Intent
import _root_.android.content.Context
import _root_.java.net._
import _root_.java.io._
import _root_.com.twitter.json._

class Convert extends Activity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    var progressDialog = new ProgressDialog(this)
    var intent = getIntent

    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
    progressDialog.setMessage("Converting. Please wait ...")
    progressDialog.show

    new Thread(new Runnable {
      override def run {
        try {
          val result = convert(intent.getStringExtra(Constant.SERVICE_NAME), intent.getStringExtra(Constant.CONVERTED_TEXT));

          result match {
            case Some(str) => doFinish(str)
            case None => doFinish("")
          }
        } catch {
          case e => doFinish("aaa")
        }
      }
    }).start
  }

  private[this] def doFinish(str: String) {
    var data = new Intent
    data.putExtra(Constant.REPLACE_KEY, str)
    setResult(Activity.RESULT_OK, data)

    finish
  }

  private[this] def convert(name:String, text:String):Option[String] = {
    var url = new URL(new URI("http://youpy.no.de", "/tcs/" + name + "?text=" + text, null).toString)
    var in = new BufferedReader(new InputStreamReader(url.openStream))
    var responseText = ""
    var inputLine = ""

    /* http://www.ne.jp/asahi/hishidama/home/tech/scala/expression.html#h_let */
    while({inputLine = in.readLine; inputLine != null}) {
      responseText += inputLine
    }

    in.close

    var data = Json.parse(responseText).asInstanceOf[Map[String, String]]
    data.get("result")
  }
}
