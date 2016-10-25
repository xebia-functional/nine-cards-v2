package cards.nine.app.ui.commons

import android.app.Activity
import cats.data.{Reader, _}
import com.fortysevendeg.ninecardslauncher.TypedResource

object ActivityFindViews  {
  def findView[A](tr: TypedResource[A]): Reader[Activity, A] =
    Reader((activity : Activity) => activity.findViewById(tr.id).asInstanceOf[A])
}