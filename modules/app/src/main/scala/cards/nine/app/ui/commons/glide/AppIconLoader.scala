package cards.nine.app.ui.commons.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader

class AppIconLoader extends ModelLoader[String, String] {

  override def getResourceFetcher(model: String, width: Int, height: Int): DataFetcher[String] =
    new DataFetcher[String]() {
      override def cleanup(): Unit = {}

      override def loadData(priority: Priority): String = model

      override def cancel(): Unit = {}

      override def getId: String = model
    }

}
