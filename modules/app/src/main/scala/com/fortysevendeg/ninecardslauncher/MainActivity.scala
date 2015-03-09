package com.fortysevendeg.ninecardslauncher

import android.app.Activity
import android.os.Bundle
import com.fortysevendeg.ninecardslauncher.api.Api

class MainActivity extends Activity with TypedViewHolder {
    override def onCreate(b: Bundle) {
        super.onCreate(b)
        setContentView(R.layout.hello)
        findView(TR.test_textview).setText(Api.hello)
    }
}
