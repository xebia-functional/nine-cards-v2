package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.fortysevendeg.ninecardslauncher.app.commons.{ContextSupportProvider, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.collections.ActionsScreenListener
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ActivityResult._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.WizardActivity
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, Contact, IterableApps, IterableContacts}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.FullDsl._
import macroid.{Contexts, Ui}
import rapture.core.Answer

import scalaz.concurrent.Task

class LauncherActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with TypedFindView
  with ActionsScreenListener
  with LauncherComposer
  with LauncherTasks
  with SystemBarsTint
  with NineCardIntentConversions
  with DrawerListeners {

  implicit lazy val di: Injector = new Injector

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(this)

  implicit lazy val theme: NineCardsTheme = di.themeProcess.getSelectedTheme.run.run match {
    case Answer(t) => t
    case _ => getDefaultTheme
  }

  val tagDialog = "dialog"

  var hasFocus = false

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    Task.fork(di.userProcess.register.run).resolveAsync()
    setContentView(R.layout.launcher_activity)
    runUi(initUi ~ initDrawerUi)
    initAllSystemBarsTint
    loadCollectionsAndDockApps()
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    (requestCode, resultCode) match {
      case (`wizard`, Activity.RESULT_OK) => loadCollectionsAndDockApps()
      case _ =>
    }
  }

  override def onStartFinishAction(): Unit = runUi(turnOffFragmentContent)

  override def onEndFinishAction(): Unit = removeActionFragment

  override def onBackPressed(): Unit = runUi(backByPriority)

  override def onWindowFocusChanged(hasFocus: Boolean): Unit = {
    super.onWindowFocusChanged(hasFocus)
    this.hasFocus = hasFocus
  }

  override def onNewIntent(intent: Intent): Unit = {
    super.onNewIntent(intent)
    val alreadyOnHome = hasFocus && ((intent.getFlags &
      Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
      != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
    if (alreadyOnHome) runUi(backByPriority)
  }

  override def dispatchKeyEvent(event: KeyEvent): Boolean = (event.getAction, event.getKeyCode) match {
    case (KeyEvent.ACTION_DOWN | KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HOME) => true
    case _ => super.dispatchKeyEvent(event)
  }

  def addCollection(collection: Collection) = runUi(uiActionCollection(Add, collection))

  def removeCollection(collection: Collection) = {
    val overOneCollection = workspaces.exists(_.data.filterNot(_.workSpaceType.isMomentWorkSpace).headOption.exists(_.collections.length!=1))
    if (overOneCollection) {
      val ft = getSupportFragmentManager.beginTransaction()
      Option(getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
      ft.addToBackStack(javaNull)
      val dialog = new RemoveCollectionDialogFragment(() => {
        Task.fork(di.collectionProcess.deleteCollection(collection.id).run).resolveAsyncUi(
          onResult = (_) => uiActionCollection(Remove, collection),
          onException = (_) => showMessage(R.string.contactUsError)
        )
      })
      dialog.show(ft, tagDialog)
    } else {
      runUi(showMessage(R.string.minimumOneCollectionMessage))
    }
  }

  private[this] def loadCollectionsAndDockApps(): Unit = Task.fork(getLauncherApps.run).resolveAsyncUi(
    onResult = {
      // Check if there are collections in DB, if there aren't we go to wizard
      case (Nil, Nil) => goToWizard()
      case (collections, dockApps) =>
        getUserInfo()
        createCollections(collections, dockApps)
    },
    onException = (ex: Throwable) => goToWizard(),
    onPreTask = () => showLoading
  )

  private[this] def getUserInfo() = Task.fork(di.userConfigProcess.getUserInfo.run).resolveAsyncUi(
    onResult = userInfoMenu
  )

  private[this] def goToWizard(): Ui[_] = Ui {
    val wizardIntent = new Intent(LauncherActivity.this, classOf[WizardActivity])
    startActivityForResult(wizardIntent, wizard)
  }

  private[this] def toGetAppOrder(appsMenuOption: AppsMenuOption): GetAppOrder = appsMenuOption match {
    case AppsAlphabetical => GetByName
    case AppsByCategories => GetByCategory
    case AppsByLastInstall => GetByInstallDate
  }

  private[this] def toGetContactOrder(contactMenuOption: ContactsMenuOption): ContactsFilter = contactMenuOption match {
    case ContactsAlphabetical => AllContacts
    case ContactsFavorites => FavoriteContacts
    case ContactsByLastCall => AllContacts // TODO We should create a new adapter in ticket #204
  }

  override def loadApps(appsMenuOption: AppsMenuOption): Unit = {
    val getAppOrder = toGetAppOrder(appsMenuOption)
    Task.fork(di.deviceProcess.getIterableApps(getAppOrder).run).resolveAsyncUi(
      onResult = (apps: IterableApps) => addApps(apps, getAppOrder, (app: App) => {
        execute(toNineCardIntent(app))
      }, (app: App) => {
        launchSettings(app.packageName)
      })
    )
  }

  override def loadContacts(contactsMenuOption: ContactsMenuOption): Unit = {
    val getContactOrder = toGetContactOrder(contactsMenuOption)
    Task.fork(di.deviceProcess.getIterableContacts(filter = getContactOrder).run).resolveAsyncUi(
      onResult = (contacts: IterableContacts) => addContacts(contacts, (contact: Contact) => {
        execute(contact)
      })
    )
  }

}
