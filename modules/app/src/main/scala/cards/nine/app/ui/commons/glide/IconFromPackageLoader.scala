package cards.nine.app.ui.commons.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader

class IconFromPackageLoader extends ModelLoader[Int, Int] {

  override def getResourceFetcher(model: Int, width: Int, height: Int): DataFetcher[Int] =
    new DataFetcher[Int]() {
      override def cleanup(): Unit = {}

      override def loadData(priority: Priority): Int = model

      override def cancel(): Unit = {}

      override def getId: String = model.toString
    }

}
