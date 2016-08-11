package com.fortysevendeg.ninecardslauncher.app.ui.components.dialogs

import android.content.DialogInterface
import android.content.DialogInterface.OnDismissListener
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class CollectionDialog(
  moments: Seq[Collection],
  onCollection: (Int) => Any,
  onDismissDialog: () => Any)(implicit contextWrapper: ContextWrapper)
  extends BottomSheetDialog(contextWrapper.getOriginal)
  with TypedFindView { dialog =>

  lazy val selectCollectionList = findView(TR.select_collection_list)

  val sheetView = getLayoutInflater.inflate(TR.layout.select_collection_dialog)

  setContentView(sheetView)

  val views = moments map (moment => new CollectionItem(moment))

  (selectCollectionList <~
    vgAddViews(views)).run

  setOnDismissListener(new OnDismissListener {
    override def onDismiss(dialog: DialogInterface): Unit = onDismissDialog()
  })

  class CollectionItem(collection: Collection)
    extends LinearLayout(contextWrapper.getOriginal)
    with TypedFindView {

    LayoutInflater.from(getContext).inflate(TR.layout.select_collection_item, this)

    val icon = findView(TR.select_collection_item_icon)

    val text = findView(TR.select_collection_item_text)

    val colorIcon = resGetColor(R.color.item_list_popup_moments_menu)

    ((this <~ On.click(
      Ui {
        onCollection(collection.id)
        dialog.dismiss()
      })) ~
      (icon <~ ivSrc(iconCollectionDetail(collection.icon)) <~ tivDefaultColor(colorIcon)) ~
      (text <~ tvText(collection.name))).run


  }

}
