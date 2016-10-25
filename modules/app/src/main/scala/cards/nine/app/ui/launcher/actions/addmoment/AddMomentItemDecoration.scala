package cards.nine.app.ui.launcher.actions.addmoment

import android.graphics.{Canvas, Paint}
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.State
import cards.nine.process.theme.models.NineCardsTheme
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

class AddMomentItemDecoration(implicit theme: NineCardsTheme, contextWrapper: ContextWrapper)
  extends RecyclerView.ItemDecoration {

  val paint: Paint = {
    val paint = new Paint
    paint.setAntiAlias(true)
    paint.setColor(resGetColor(R.color.divider_color_info_dialog))
    paint.setStrokeWidth(resGetDimensionPixelSize(R.dimen.stroke_thin))
    paint.setStyle(Paint.Style.STROKE)
    paint
  }

  override def onDraw(c: Canvas, recyclerView: RecyclerView, state: State): Unit = {
    super.onDraw(c, recyclerView, state)
    (0 to recyclerView.getChildCount flatMap (i => Option(recyclerView.getChildAt(i)))) foreach { view =>
      c.drawLine(view.getLeft, view.getBottom, view.getRight, view.getBottom, paint)
    }
  }

}
