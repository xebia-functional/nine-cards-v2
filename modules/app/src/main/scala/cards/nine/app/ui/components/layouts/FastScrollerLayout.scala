package cards.nine.app.ui.components.layouts

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.{Drawable, GradientDrawable}
import android.os.Build.VERSION._
import android.os.Build.VERSION_CODES._
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView._
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.AttributeSet
import android.view.MotionEvent._
import android.view.ViewGroup.LayoutParams._
import android.view._
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import cards.nine.models.types.NineCardsCategory
import macroid.extras.ImageViewTweaks._
import macroid.extras.TextViewTweaks._
import macroid.extras.RecyclerViewTweaks._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.commons._
import cards.nine.models.TermCounter
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class FastScrollerLayout(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends FrameLayout(context, attr, defStyleAttr) { self =>

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  lazy val fastScroller = new FastScrollerView(context)

  override def onFinishInflate(): Unit = {
    val ll = new LayoutParams(WRAP_CONTENT, MATCH_PARENT)
    ll.gravity = Gravity.RIGHT
    (this <~ vgAddView(fastScroller, ll)).run
    super.onFinishInflate()
  }

  def linkRecycler(recyclerView: RecyclerView) = (fastScroller <~ fsRecyclerView(recyclerView)).run

  def setColor(color: Int, backgroundColor: Int) = (fastScroller <~ fsColor(color) + fsBackgroundColor(backgroundColor)).run

  def setMarginRightBarContent(pixels: Int) = (fastScroller <~ fsMarginRightBarContent(pixels)).run

  def setSignalType(signalType: FastScrollerSignalType) = (fastScroller <~ fsSignalType(signalType)).run

  def reset = (fastScroller <~ fsReset).run

  def setCounters(counters: Seq[TermCounter]) = (fastScroller <~ fsCounters(counters)).run

  def setEnabledScroller(enabled: Boolean) =
    (fastScroller <~
      fsEnabledScroller(enabled) <~
      (if (enabled) fsShow else fsHide)).run

  private[this] def fsSignalType(signalType: FastScrollerSignalType) = Tweak[FastScrollerView](_.setFastScrollerSignalType(signalType))

  private[this] def fsReset = Tweak[FastScrollerView](_.reset())

  private[this] def fsMarginRightBarContent(pixels: Int) = Tweak[FastScrollerView](_.setBarContentMargin(pixels).run)

  private[this] def fsCounters(counters: Seq[TermCounter]) = Tweak[FastScrollerView](_.setCounters(counters))

  private[this] def fsEnabledScroller(enabled: Boolean) = Tweak[FastScrollerView](_.setEnabledScroller(enabled))

  private[this] def fsRecyclerView(rv: RecyclerView) = Tweak[FastScrollerView](view => view.setRecyclerView(rv))

  private[this] def fsColor(color: Int) = Tweak[FastScrollerView] { view =>
    ((view.bar <~ ivSrc(changeColor(R.drawable.fastscroller_bar, color))) ~
      (view.signal <~ Tweak[FrameLayout](_.setBackground(changeColor(R.drawable.fastscroller_signal, color))))).run
  }

  private[this] def fsBackgroundColor(color: Int) = Tweak[FastScrollerView] { view =>
    (view.barContent <~ vBackgroundColor(color)).run
  }

  private[this] def fsShow = Tweak[FastScrollerView] (_.show.run)

  private[this] def fsHide = Tweak[FastScrollerView] (_.hide.run)

  private[this] def changeColor(res: Int, color: Int): Drawable = getDrawable(res) match {
    case drawable: GradientDrawable =>
      drawable.setColor(color)
      drawable
    case drawable => drawable
  }

  private[this] def getDrawable(res: Int): Drawable = if (SDK_INT < LOLLIPOP_MR1) {
    context.getResources.getDrawable(res)
  } else {
    context.getResources.getDrawable(res, javaNull)
  }

}

class FastScrollerView(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends FrameLayout(context, attr, defStyleAttr)
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  private[this] var recyclerView = slot[RecyclerView]

  private[this] var scrollListener: Option[ScrollListener] = None

  var statuses = new FastScrollerStatuses

  setClipChildren(false)

  LayoutInflater.from(context).inflate(R.layout.fastscroller, this)

  lazy val barContent = findView(TR.fastscroller_bar_content)

  val bar = findView(TR.fastscroller_bar)

  val signal = findView(TR.fastscroller_signal)

  val text = findView(TR.fastscroller_signal_text)

  val icon = findView(TR.fastscroller_signal_icon)

  val barSize = context.getResources.getDimensionPixelOffset(R.dimen.fastscroller_bar_height)

  override def onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int): Unit = {
    super.onSizeChanged(w, h, oldw, oldh)
    if (statuses.heightScroller != h) {
      Option(getParent) match {
        case Some(parent: View) =>
          // Try to expand the touch event in the right side to improve the feedback to user
          val delegateArea = new Rect()
          getHitRect(delegateArea)
          delegateArea.right = delegateArea.right + context.getResources.getDimensionPixelOffset(R.dimen.padding_default)
          parent.setTouchDelegate(new TouchDelegate(delegateArea, this) {
            override def onTouchEvent(event: MotionEvent): Boolean = touchEvent(event)
          })
        case _ =>
      }

      statuses = statuses.copy(heightScroller = h)
      recyclerView foreach (rv => statuses = statuses.resetRecyclerInfo(rv, statuses.heightScroller))
      changePosition(0).run
    }
  }

  override def onTouchEvent(event: MotionEvent): Boolean = touchEvent(event)

  private[this] def touchEvent(event: MotionEvent): Boolean = {
    val action = MotionEventCompat.getActionMasked(event)
    val y = flatInBoundaries(MotionEventCompat.getY(event, 0))
    (statuses.enabled, action) match {
      case (false, _) => super.onTouchEvent(event)
      case (_, ACTION_DOWN) =>
        statuses = statuses.startScroll()
        true
      case (_, ACTION_MOVE) =>
        statuses = statuses.movingScroll()
        (changePosition(y) ~
          (if (statuses.usingCounters) showSignal else hideSignal) ~
          (recyclerView <~ rvScrollToPosition(y))).run
        true
      case (_, ACTION_UP | ACTION_CANCEL) =>
        statuses = statuses.resetScrollPosition()
        // If the ScrollState of Recycler is SCROLL_STATE_SETTLING, we wait to resetScroll
        // when the animation is finished using method onScrollStateChanged in OnScrollListener class
        recyclerView foreach { rv =>
          if (rv.getScrollState != SCROLL_STATE_SETTLING) {
            statuses = statuses.resetScroll()
          }
        }
        ((recyclerView <~ removeKeys() <~ rvInvalidateItemDecorations) ~ changePosition(y) ~ hideSignal).run
        true
      case _ => super.onTouchEvent(event)
    }
  }

  private[this] def addKeys(position: Int, count: Int) =
    vAddField(FastScrollerView.fastScrollerPositionKey, position) +
      vAddField(FastScrollerView.fastScrollerCountKey, count)

  private[this] def removeKeys() =
    vRemoveField(FastScrollerView.fastScrollerPositionKey) +
      vRemoveField(FastScrollerView.fastScrollerCountKey)

  def setBarContentMargin(pixels: Int) = barContent <~ vMargin(marginRight = pixels)

  def show: Ui[_] = (signal <~ vGone) ~ (bar <~ vVisible)

  def hide: Ui[_] = (signal <~ vGone) ~ (bar <~ vGone)

  def showSignal: Ui[_] = signal <~ vVisible

  def hideSignal: Ui[_] = signal <~ vGone

  def setFastScrollerSignalType(signalType: FastScrollerSignalType): Unit = {
    statuses = statuses.copy(fastScrollerSignalType = signalType)
    signalType match {
      case FastScrollerText => ((icon <~ vGone) ~ (text <~ vVisible)).run
      case FastScrollerCategory => ((icon <~ vVisible) ~ (text <~ vGone)).run
      case FastScrollerInstallationDate => ((icon <~ vVisible) ~ (text <~ vGone)).run
    }
  }

  def setRecyclerView(rv: RecyclerView): Unit = {
    statuses = statuses.resetRecyclerInfo(rv, statuses.heightScroller)
    scrollListener foreach rv.removeOnScrollListener
    val sl = new ScrollListener
    scrollListener = Option(sl)
    rv.addOnScrollListener(sl)
    recyclerView = Option(rv)
  }

  def reset(): Unit = {
    recyclerView foreach (rv => statuses = statuses.resetRecyclerInfo(rv, statuses.heightScroller))
    scrollListener foreach (_.reset())
    changePosition(0).run
  }

  def setEnabledScroller(enabled: Boolean) = statuses = statuses.copy(enabled = enabled)

  def setCounters(counters: Seq[TermCounter]) = statuses = statuses.copy(counters = counters)

  private[this] def changePosition(y: Float): Ui[_] = {
    val position = y / statuses.heightScroller
    val value = ((statuses.heightScroller - barSize) * position).toInt
    val max = statuses.heightScroller - barSize
    val minimum = math.max(0, value)
    val barPosY = math.min(minimum, max)
    val signalPosY = math.max(0, barPosY - barSize)
    (bar <~ vY(barPosY)) ~ (signal <~ vY(signalPosY))
  }

  private[this] def rvScrollToPosition(y: Float) = Tweak[RecyclerView] { view =>
    val position = getPosition(y)
    if (position != statuses.lastScrollToPosition) {
      statuses = statuses.copy(lastScrollToPosition = position)
      if (statuses.usingCounters) {
        val item = statuses.counters(position)
        val toPosition = (statuses.counters take position map (_.count)).sum
        (updateSignal(item.term) ~
          (recyclerView <~ addKeys(toPosition, item.count) <~ rvInvalidateItemDecorations)).run
        view.smoothScrollToPosition(toPosition)
      } else {
        val toPosition = position * statuses.columns
        view.smoothScrollToPosition(toPosition)
      }
    }
  }

  private[this] def updateSignal(term: String): Ui[_] = statuses.fastScrollerSignalType match {
    case FastScrollerText => text <~ tvText(term)
    case FastScrollerCategory =>
      icon <~
        tvCompoundDrawablesWithIntrinsicBoundsResources(top = getIconResource(NineCardsCategory(term).getIconResource)) <~
        tvText(getStringResource(NineCardsCategory(term).getStringResource))
    case FastScrollerInstallationDate =>
      icon <~
        tvCompoundDrawablesWithIntrinsicBoundsResources(top = R.drawable.app_drawer_filter_installation_date) <~
        tvText(getStringResource(term))
  }

  private[this] def getIconResource(name: String) = {
    val resourceName = s"icon_collection_${name}_detail"
    val resource = getResources.getIdentifier(resourceName, "drawable", context.getPackageName)
    if (resource == 0) R.drawable.icon_collection_default_detail else resource
  }

  private[this] def getStringResource(name: String) = {
    val resource = getResources.getIdentifier(name, "string", context.getPackageName)
    if (resource == 0) R.string.app_name else resource
  }

  private[this] def getPosition(y: Float) = if (statuses.usingCounters) {
    ((y * statuses.counters.length) / statuses.heightScroller).toInt
  } else {
    ((y * statuses.rows) / statuses.heightScroller).toInt
  }

  private[this] def getRowFirstItem(recyclerView: RecyclerView): Int = {
    // Update child count if it's necessary
    if (statuses.visibleRows == 0) {
      val visibleRows = statuses.childCountToRows(recyclerView.getChildCount)
      statuses = statuses.copy(visibleRows = visibleRows)
    }

    val firstVisiblePosition = recyclerView.getLayoutManager match {
      case lm: LinearLayoutManager => lm.findFirstVisibleItemPosition()
      case _ => 0
    }

    statuses.childCountToRows(firstVisiblePosition)
  }

  private[this] def flatInBoundaries(y: Float) = y match {
    case v if v < 0 => 0f
    case v if v > statuses.heightScroller => statuses.heightScroller.toFloat
    case v => v
  }

  class ScrollListener
    extends OnScrollListener {

    private[this] var lastRowFirstItem = 0

    private[this] var offsetY = 0f

    private[this] var oldState = SCROLL_STATE_IDLE

    override def onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int): Unit =
      if (!statuses.moving) {
        val rowFirstItem = getRowFirstItem(recyclerView)
        val y = statuses.projectToBar(rowFirstItem)
        val maxRows = statuses.maxRows
        val move = if (rowFirstItem == lastRowFirstItem && maxRows > 0) {
          // We calculate the displacement between the last and current row
          offsetY = offsetY + dy
          val ratio = offsetY / statuses.heightRow.toFloat
          val space = statuses.heightScroller / maxRows
          y + (ratio * space)
        } else {
          lastRowFirstItem = rowFirstItem
          offsetY = 0
          y
        }
        changePosition(move).run
      } else if (recyclerView.getScrollState != statuses.scrollState) {
        statuses = statuses.copy(scrollState = recyclerView.getScrollState)
      }

    override def onScrollStateChanged(recyclerView: RecyclerView, newState: Int): Unit = {
      if (statuses.moving && oldState == SCROLL_STATE_SETTLING && newState == SCROLL_STATE_IDLE) {
        statuses = statuses.resetScroll()
      }
      oldState = newState
      super.onScrollStateChanged(recyclerView, newState)
    }

    def reset(): Unit = {
      lastRowFirstItem = 0
      offsetY = 0f
    }

  }

}

object FastScrollerView {
  val fastScrollerPositionKey = "position"
  val fastScrollerCountKey = "count"
}

case class FastScrollerStatuses(
  enabled: Boolean = true,
  heightScroller: Int = 0,
  heightAllRows: Int = 0,
  heightRow: Int = 0,
  columns: Int = 0,
  moving: Boolean = false,
  totalItems: Int = 0,
  visibleRows: Int = 0,
  scrollState: Int = SCROLL_STATE_IDLE,
  lastScrollToPosition: Int = -1,
  fastScrollerSignalType: FastScrollerSignalType = FastScrollerText,
  counters: Seq[TermCounter] = Seq.empty) {

  /**
    * Update information related to data from recyclerview
    */
  def resetRecyclerInfo(recyclerView: RecyclerView, height: Int): FastScrollerStatuses = {
    val (allRows, item, columns) = Option(recyclerView.getAdapter) match {
      case Some(listener: FastScrollerListener) =>
        (listener.getHeightAllRows - height, listener.getHeightItem, listener.getColumns)
      case _ => (0, 0, 0)
    }
    val total = Option(recyclerView.getAdapter) match {
      case Some(adapter) => adapter.getItemCount
      case _ => 0
    }
    copy(heightAllRows = allRows, heightRow = item, totalItems = total, columns = columns, visibleRows = 0, moving = false)
  }

  // Number of rows of recyclerview given the number of columns
  def rows: Int = math.ceil(totalItems.toFloat / columns).toInt

  // Maximum number of rows for calculate the position of bar in fastscroller
  def maxRows: Int = rows - visibleRows

  // Number of rows for a number of items
  def childCountToRows(count: Int) = math.ceil(count.toFloat / columns).toInt

  // Calculate y position given first rom position
  def projectToBar(rowFirstItem: Int) = heightScroller * (rowFirstItem.toFloat / maxRows.toFloat)

  def startScroll(): FastScrollerStatuses = copy(moving = true)

  def movingScroll(): FastScrollerStatuses = copy(moving = true)

  def resetScrollPosition(): FastScrollerStatuses = copy(lastScrollToPosition = -1)

  def resetScroll(): FastScrollerStatuses = copy(moving = false, scrollState = SCROLL_STATE_IDLE)

  def usingCounters = counters.nonEmpty

}

trait FastScrollerListener {

  def getHeightAllRows: Int

  def getHeightItem: Int

  def getColumns: Int

}

sealed trait FastScrollerSignalType

case object FastScrollerCategory extends FastScrollerSignalType

case object FastScrollerInstallationDate extends FastScrollerSignalType

case object FastScrollerText extends FastScrollerSignalType