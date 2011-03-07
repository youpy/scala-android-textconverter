package com.buycheapviagraonlinenow.android.textconverter

import _root_.android.app.Activity
import _root_.android.widget.Button
import _root_.android.view.View
import _root_.android.os.Bundle

class DefaultActivity extends Activity {
  private[this] object Constant {
    var SERVICES_FILENAME = "services.json"
  }

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    var button = new Button(this)
    button.setText("Clear Cache")
    button.setOnClickListener(new View.OnClickListener {
      override def onClick(v:View) {
        button.setClickable(false)
        deleteFile(Constant.SERVICES_FILENAME)
      }
    })
    setContentView(button)
  }
}
