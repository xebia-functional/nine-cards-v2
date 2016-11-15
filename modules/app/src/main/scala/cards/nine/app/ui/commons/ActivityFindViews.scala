package cards.nine.app.ui.commons

import android.app.Activity
import android.view.ViewGroup
import cats.data.{Reader, _}
import com.fortysevendeg.ninecardslauncher.TypedResource

object ActivityFindViews  {
  def findView[A](tr: TypedResource[A]): Reader[Activity, A] =
    Reader((activity : Activity) => activity.findViewById(tr.id).asInstanceOf[A])
}

object ViewGroupFindViews  {
  def findView[A](tr: TypedResource[A]): Reader[ViewGroup, A] =
    Reader((viewGroup: ViewGroup) => viewGroup.findViewById(tr.id).asInstanceOf[A])
}