/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.commons.adapters.contacts

import java.util.Date

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.ImageView
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.layouts.FastScrollerListener
import cards.nine.app.ui.components.widgets.ScrollingLinearLayoutManager
import cards.nine.app.ui.preferences.commons.FontSize
import cards.nine.models.types.theme.DrawerTextColor
import cards.nine.models.{LastCallsContact, NineCardsTheme}
import cards.nine.models.types._
import macroid.extras.DeviceVersion.Lollipop
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.TypedResource._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._
import org.ocpsoft.prettytime.PrettyTime

case class LastCallsAdapter(
    contacts: Seq[LastCallsContact],
    clickListener: (LastCallsContact) => Unit)(
    implicit val activityContext: ActivityContextWrapper,
    implicit val uiContext: UiContext[_],
    theme: NineCardsTheme)
    extends RecyclerView.Adapter[LastCallsContactHolder]
    with FastScrollerListener {

  val columnsLists = 1

  val heightItem = resGetDimensionPixelSize(R.dimen.height_contact_item)

  override def getItemCount: Int = contacts.length

  override def onBindViewHolder(vh: LastCallsContactHolder, position: Int): Unit =
    vh.bind(contacts(position), position).run

  override def onCreateViewHolder(parent: ViewGroup, i: Int): LastCallsContactHolder = {
    val view =
      LayoutInflater.from(parent.getContext).inflate(TR.layout.last_call_item, parent, false)
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit =
        v.getPosition foreach (tag => clickListener(contacts(tag)))
    })
    LastCallsContactHolder(view)
  }

  def getLayoutManager: LinearLayoutManager =
    new ScrollingLinearLayoutManager(columnsLists)

  override def getHeightAllRows: Int =
    contacts.length / columnsLists * getHeightItem

  override def getHeightItem: Int = heightItem

  override def getColumns: Int = columnsLists
}

case class LastCallsContactHolder(content: View)(
    implicit context: ActivityContextWrapper,
    uiContext: UiContext[_],
    theme: NineCardsTheme)
    extends RecyclerView.ViewHolder(content)
    with TypedFindView {

  val maxCalls = 3

  lazy val icon = Option(findView(TR.last_call_item_icon))

  lazy val name = Option(findView(TR.last_call_item_name))

  lazy val hour = Option(findView(TR.last_call_item_hour))

  lazy val callTypes = Option(findView(TR.last_call_item_types))

  (icon <~ (Lollipop ifSupportedThen vCircleOutlineProvider() getOrElse Tweak.blank)).run

  def bind(contact: LastCallsContact, position: Int): Ui[_] = {
    val date  = new Date(contact.lastCallDate)
    val time  = new PrettyTime().format(date)
    val color = theme.get(DrawerTextColor)
    (icon <~ ivUriContact(contact.photoUri getOrElse "", contact.title, circular = true)) ~
      (name <~ tvSizeResource(FontSize.getContactSizeResource) <~ tvText(contact.title) <~ tvColor(
        color)) ~
      (hour <~ tvSizeResource(FontSize.getSizeResource) <~ tvText(time) <~ tvColor(color)) ~
      (callTypes <~ addCallTypesView(contact.calls take maxCalls map (_.callType))) ~
      (content <~ vSetPosition(position))
  }

  private[this] def addCallTypesView(callTypes: Seq[CallType])(
      implicit context: ActivityContextWrapper) = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_small)
    val callViews = callTypes map { ct =>
      (w[ImageView] <~ ivSrc(ct match {
        case IncomingType => R.drawable.icon_call_incoming
        case MissedType   => R.drawable.icon_call_missed
        case _            => R.drawable.icon_call_outgoing
      }) <~ vPadding(paddingRight = padding)).get
    }
    vgRemoveAllViews + vgAddViews(callViews)
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}
