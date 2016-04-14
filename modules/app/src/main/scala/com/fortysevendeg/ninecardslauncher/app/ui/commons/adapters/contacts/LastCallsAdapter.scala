package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.contacts

import java.util.Date

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.FastScrollerListener
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.ScrollingLinearLayoutManager
import com.fortysevendeg.ninecardslauncher.process.device.models.LastCallsContact
import com.fortysevendeg.ninecardslauncher.process.device.types._
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._
import org.ocpsoft.prettytime.PrettyTime

case class LastCallsAdapter(
  contacts: Seq[LastCallsContact],
  clickListener: (LastCallsContact) => Unit)
  (implicit val activityContext: ActivityContextWrapper, implicit val uiContext: UiContext[_])
  extends RecyclerView.Adapter[LastCallsContactHolder]
  with FastScrollerListener {

  val columnsLists = 1

  val heightItem = resGetDimensionPixelSize(R.dimen.height_contact_item)

  override def getItemCount: Int = contacts.length

  override def onBindViewHolder(vh: LastCallsContactHolder, position: Int): Unit =
    vh.bind(contacts(position), position).run

  override def onCreateViewHolder(parent: ViewGroup, i: Int): LastCallsContactHolder = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.last_call_item, parent, false)
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        v.getPosition foreach (tag => clickListener(contacts(tag)))
      }
    })
    LastCallsContactHolder(view)
  }

  def getLayoutManager: LinearLayoutManager = new ScrollingLinearLayoutManager(columnsLists)

  override def getHeightAllRows: Int = contacts.length / columnsLists * getHeightItem

  override def getHeightItem: Int = heightItem

  override def getColumns: Int = columnsLists
}

case class LastCallsContactHolder(content: View)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  val maxCalls = 3

  lazy val icon = Option(findView(TR.last_call_item_icon))

  lazy val name = Option(findView(TR.last_call_item_name))

  lazy val hour = Option(findView(TR.last_call_item_hour))

  lazy val callTypes = Option(findView(TR.last_call_item_types))

  (icon <~ (Lollipop ifSupportedThen vCircleOutlineProvider() getOrElse Tweak.blank)).run

  def bind(contact: LastCallsContact, position: Int)(implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] = {
    val date = new Date(contact.lastCallDate)
    val time = new PrettyTime().format(date)
    (icon <~ ivUriContact(contact.photoUri getOrElse "", contact.title, circular = true)) ~
      (name <~ tvText(contact.title)) ~
      (hour <~ tvText(time)) ~
      (callTypes <~ addCallTypesView(contact.calls take maxCalls map (_.callType))) ~
      (content <~ vSetPosition(position))
  }

  private[this] def addCallTypesView(callTypes: Seq[CallType])(implicit context: ActivityContextWrapper) = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_small)
    val callViews = callTypes map { ct =>
      (w[ImageView] <~ ivSrc(ct match {
        case IncomingType => R.drawable.icon_call_incoming
        case MissedType => R.drawable.icon_call_missed
        case _ => R.drawable.icon_call_outgoing
      }) <~  vPadding(paddingRight = padding)).get
    }
    vgRemoveAllViews + vgAddViews(callViews)
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}
