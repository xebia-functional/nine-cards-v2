package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.app.Activity
import android.content.Intent
import android.content.Intent._
import android.graphics.{Bitmap, BitmapFactory}
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view._
import com.fortysevendeg.ninecardslauncher.app.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps.AppsFragment
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts.ContactsFragment
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations.RecommendationsFragment
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.shortcuts.ShortcutFragment
import com.fortysevendeg.ninecardslauncher.app.ui.collections.dialog.PublishCollectionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.collections.jobs._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.{AppInstalledActionFilter, AppsActionFilter}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ActivityUiContext, UiContext, UiExtensions}
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.{CircleOpeningCollectionAnimation, CollectionOpeningAnimations, NineCardsPreferencesValue}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.util.Try

class CollectionsDetailsActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with GroupCollectionsDOM
  with GroupCollectionsUiListener
  with TypedFindView
  with UiExtensions
  with ActionsScreenListener
  with BroadcastDispatcher { self =>

  val defaultPosition = 0

  val defaultIndexColor = 0

  val defaultIcon = ""

  val defaultStateChanged = false

  var firstTime = true

  lazy val preferenceValues = new NineCardsPreferencesValue

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(this)

  implicit lazy val groupCollectionsJobs = new GroupCollectionsJobs(new GroupCollectionsUiActions(self))

  implicit lazy val toolbarJobs = new ToolbarJobs(new ToolbarUiActions(self))

  implicit lazy val sharedCollectionJobs = new SharedCollectionJobs(new SharedCollectionUiActions(self))

  override val actionsFilters: Seq[String] = AppsActionFilter.cases map (_.action)

  override def manageCommand(action: String, data: Option[String]): Unit = (AppsActionFilter(action), data) match {
    case (Some(AppInstalledActionFilter), _) => groupCollectionsJobs.reloadCards(true).resolveAsync()
    case _ =>
  }

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)

    val position = getInt(
      Seq(bundle, getIntent.getExtras),
      startPosition,
      defaultPosition)

    val indexColor = getInt(
      Seq(bundle, getIntent.getExtras),
      indexColorToolbar,
      defaultIndexColor)

    val icon = getString(
      Seq(bundle, getIntent.getExtras),
      iconToolbar,
      defaultIcon)

    val isStateChanged = getBoolean(
      Seq(bundle, getIntent.getExtras),
      stateChanged,
      defaultStateChanged)

    setContentView(R.layout.collections_detail_activity)

    groupCollectionsJobs.initialize(indexColor, icon, position, isStateChanged).
      resolveAsyncServiceOr(_ => groupCollectionsJobs.showGenericError())

    registerDispatchers

  }

  override def onResume(): Unit = {
    val anim = CollectionOpeningAnimations.readValue(preferenceValues)
    if (firstTime && anim == CircleOpeningCollectionAnimation && anim.isSupported) {
      overridePendingTransition(0, 0)
      firstTime = false
    } else {
      overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom)
    }
    super.onResume()
    groupCollectionsJobs.resume().resolveAsync()
  }

  override def onPause(): Unit = {
    overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom)
    super.onPause()
    groupCollectionsJobs.pause().resolveAsync()
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    outState.putInt(startPosition, getCurrentPosition getOrElse defaultPosition)
    outState.putBoolean(stateChanged, true)
    // TODO call to DOM it's not possible
    getCurrentCollection foreach { collection =>
      outState.putInt(indexColorToolbar, collection.themedColorIndex)
      outState.putString(iconToolbar, collection.icon)
    }
    super.onSaveInstanceState(outState)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    requestCode match {
      case `shortcutAdded` => Option(data) flatMap (i => Option(i.getExtras)) match {
        case Some(b: Bundle) if b.containsKey(EXTRA_SHORTCUT_NAME) && b.containsKey(EXTRA_SHORTCUT_INTENT) =>
          val shortcutName = b.getString(EXTRA_SHORTCUT_NAME)
          val shortcutIntent = b.getParcelable[Intent](EXTRA_SHORTCUT_INTENT)
          val maybeBitmap = getBitmapFromShortcutIntent(b)
          groupCollectionsJobs.addShortcut(shortcutName, shortcutIntent, maybeBitmap).
            resolveAsyncServiceOr(_ => groupCollectionsJobs.showGenericError())
        case _ =>
      }
    }
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.collection_detail_menu, menu)
    super.onCreateOptionsMenu(menu)
  }

  override def onPrepareOptionsMenu (menu: Menu): Boolean = {
    groupCollectionsJobs.savePublishStatus().resolveAsync()
    super.onPrepareOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = item.getItemId match {
    case android.R.id.home =>
      groupCollectionsJobs.close().resolveAsync()
      false
    case R.id.action_make_public =>
      sharedCollectionJobs.showPublishCollectionWizard().resolveAsync() // TODO Review error
      true
    case R.id.action_share =>
      sharedCollectionJobs.shareCollection().resolveAsync() // TODO Review error
      true
    case _ => super.onOptionsItemSelected(item)
  }

  override def onRequestPermissionsResult(requestCode: Int, permissions: Array[String], grantResults: Array[Int]): Unit = {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    groupCollectionsJobs.requestPermissionsResult(requestCode, permissions, grantResults).
      resolveAsyncServiceOr(_ => groupCollectionsJobs.showGenericError())
  }

  override def onBackPressed(): Unit = groupCollectionsJobs.back().resolveAsync()

  override def onStartFinishAction(): Unit = groupCollectionsJobs.resetAction().resolveAsync()

  override def onEndFinishAction(): Unit = groupCollectionsJobs.destroyAction().resolveAsync()

  private[this] def getBitmapFromShortcutIntent(bundle: Bundle): Option[Bitmap] = bundle match {
    case b if b.containsKey(EXTRA_SHORTCUT_ICON) =>
      Try(b.getParcelable[Bitmap](EXTRA_SHORTCUT_ICON)).toOption
    case b if b.containsKey(EXTRA_SHORTCUT_ICON_RESOURCE) =>
      val extra = Try(b.getParcelable[ShortcutIconResource](EXTRA_SHORTCUT_ICON_RESOURCE)).toOption
      extra flatMap { e =>
        val resources = getPackageManager.getResourcesForApplication(e.packageName)
        val id = resources.getIdentifier(e.resourceName, javaNull, javaNull)
        Option(BitmapFactory.decodeResource(resources, id))
      }
    case _ => None
  }

  override def closeEditingMode(): Unit =
    groupCollectionsJobs.statuses.collectionMode match {
      case EditingCollectionMode => groupCollectionsJobs.closeEditingMode()
      case _ =>
    }

  override def isNormalMode: Boolean = groupCollectionsJobs.statuses.collectionMode == NormalCollectionMode

  override def isEditingMode: Boolean = groupCollectionsJobs.statuses.collectionMode == EditingCollectionMode

  override def showPublicCollectionDialog(collection: Collection): Unit = showDialog(PublishCollectionFragment(collection))

  override def showAppsDialog(args: Bundle): Ui[Any] = launchDialog(f[AppsFragment], args)

  override def showContactsDialog(args: Bundle): Ui[Any] = launchDialog(f[ContactsFragment], args)

  override def showShortcutsDialog(args: Bundle): Ui[Any] = launchDialog(f[ShortcutFragment], args)

  override def showRecommendationsDialog(args: Bundle): Ui[Any] = launchDialog(f[RecommendationsFragment], args)

  override def addCards(cards: Seq[AddCardRequest]): Unit = groupCollectionsJobs.addCards(cards).resolveAsync()
}

trait ActionsScreenListener {
  def onStartFinishAction()

  def onEndFinishAction()
}

object CollectionsDetailsActivity {
  val startPosition = "start_position"
  val indexColorToolbar = "color_toolbar"
  val iconToolbar = "icon_toolbar"
  val stateChanged = "state_changed"
  val snapshotName = "snapshot"

  val cardAdded = "cardAdded"

  def getContentTransitionName(position: Int) = s"icon_$position"
}