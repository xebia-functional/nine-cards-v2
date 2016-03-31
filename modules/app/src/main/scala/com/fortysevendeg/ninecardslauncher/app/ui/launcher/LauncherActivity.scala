package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.view.{View, KeyEvent}
import com.fortysevendeg.ninecardslauncher.app.analytics._
import com.fortysevendeg.ninecardslauncher.app.commons.{ContextSupportProvider, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.app.ui.collections.{CollectionsDetailsActivity, ActionsScreenListener}
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.dialogs.RemoveCollectionDialogFragment
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.WizardActivity
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.process.user.models.User
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid._

class LauncherActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with TypedFindView
  with LauncherActions
  with ActionsScreenListener
  with LauncherComposer
  with SystemBarsTint
  with NineCardIntentConversions
  with AnalyticDispatcher { self =>

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(this)

  implicit lazy val presenter: LauncherPresenter = new LauncherPresenter(self)

  implicit lazy val theme: NineCardsTheme = presenter.getTheme.get

  val tagDialog = "dialog"

  var hasFocus = false

  var userProfileStatuses = UserProfileStatuses()

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    setContentView(R.layout.launcher_activity)
    (presenter.registerUser() ~ initUi).run
    initAllSystemBarsTint
  }

  override def onResume(): Unit = {
    super.onResume()
    if (isEmptyCollections) {
      presenter.loadCollectionsAndDockApps().run
    }
  }

  override def onStartFinishAction(): Unit = turnOffFragmentContent.run

  override def onEndFinishAction(): Unit = removeActionFragment

  override def onBackPressed(): Unit = backByPriority.run

  override def onWindowFocusChanged(hasFocus: Boolean): Unit = {
    super.onWindowFocusChanged(hasFocus)
    this.hasFocus = hasFocus
  }

  override def onNewIntent(intent: Intent): Unit = {
    super.onNewIntent(intent)
    val alreadyOnHome = hasFocus && ((intent.getFlags &
      Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
      != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
    if (alreadyOnHome) backByPriority.run
  }

  override def dispatchKeyEvent(event: KeyEvent): Boolean = (event.getAction, event.getKeyCode) match {
    case (KeyEvent.ACTION_DOWN | KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HOME) => true
    case _ => super.dispatchKeyEvent(event)
  }

  override def addCollection(collection: Collection): Ui[Any] = uiActionCollection(Add, collection)

  override def showDialogForRemoveCollection(collection: Collection): Ui[Any] = Ui {
    val ft = getSupportFragmentManager.beginTransaction()
    Option(getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
    ft.addToBackStack(javaNull)
    val dialog = new RemoveCollectionDialogFragment(() => {
      presenter.removeCollection(collection).run
    })
    dialog.show(ft, tagDialog)
  }

  override def removeCollection(collection: Collection): Ui[Any] = uiActionCollection(Remove, collection)

  override def showContactUsError(): Ui[Any] = showMessage(R.string.contactUsError)

  override def showMinimumOneCollectionMessage(): Ui[Any] = showMessage(R.string.minimumOneCollectionMessage)

  override def goToCollection(view: View, collection: Collection): Ui[Any] = {
    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
      self,
      new Pair[View, String](view, getContentTransitionName(collection.position)))
    val intent = createIntent[CollectionsDetailsActivity]
    intent.putExtra(startPosition, collection.position)
    intent.putExtra(indexColorToolbar, collection.themedColorIndex)
    intent.putExtra(iconToolbar, collection.icon)
    uiStartIntentWithOptions(intent, options)
  }

  override def canRemoveCollections(): Ui[Boolean] = Ui(getCountCollections > 1)

  def onConnectedUserProfile(name: String, email: String, avatarUrl: Option[String]): Unit = userProfileMenu(name, email, avatarUrl).run

  def onConnectedPlusProfile(coverPhotoUrl: String): Unit = plusProfileMenu(coverPhotoUrl).run

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    userProfileStatuses.userProfile foreach (_.connectUserProfile(requestCode, resultCode, data))
    (requestCode, resultCode) match {
      case (RequestCodes.goToProfile, ResultCodes.logoutSuccessful) =>
        ((workspaces <~ lwsClean) ~ goToWizard()).run
      case _ =>
    }
  }

  override def showLoading(): Ui[Any] = showLoadingView

  override def goToWizard(): Ui[_] = Ui {
    val wizardIntent = new Intent(LauncherActivity.this, classOf[WizardActivity])
    startActivity(wizardIntent)
  }

  override def loadCollections(collections: Seq[Collection], apps: Seq[DockApp]): Ui[Any] =
    createCollections(collections, apps)

  override def loadUserProfile(user: User): Ui[Any] = Ui {
    val userProfile = user.email map { email =>
      new UserProfileProvider(
        account = email,
        onConnectedUserProfile = onConnectedUserProfile,
        onConnectedPlusProfile = onConnectedPlusProfile)
    }
    userProfileStatuses = userProfileStatuses.copy(userProfile = userProfile)
    userProfileStatuses.userProfile foreach (_.connect())
  }

  override def reloadAppsInDrawer(
    apps: IterableApps,
    getAppOrder: GetAppOrder = GetByName,
    counters: Seq[TermCounter] = Seq.empty): Ui[Any] =
    addApps(
      apps = apps,
      clickListener = (app: App) => {
        if (isTabsOpened) {
          closeTabs.run
        } else {
          self !>>
            TrackEvent(
              screen = CollectionDetailScreen,
              category = AppCategory(app.category),
              action = OpenAction,
              label = Some(ProvideLabel(app.packageName)),
              value = Some(OpenAppFromAppDrawerValue))
          execute(toNineCardIntent(app))
        }
      },
      longClickListener = (app: App) => {
        if (isTabsOpened) {
          closeTabs.run
        } else {
          launchSettings(app.packageName)
        }
      },
      getAppOrder = getAppOrder,
      counters = counters)

  override def reloadContactsInDrawer(
    contacts: IterableContacts,
    counters: Seq[TermCounter] = Seq.empty): Ui[_] =
    addContacts(
      contacts = contacts,
      clickListener = (contact: Contact) =>
        if (isTabsOpened) {
          closeTabs.run
        } else {
          executeContact(contact.lookupKey)
        },
      counters = counters)

  override def reloadLastCallContactsInDrawer(contacts: Seq[LastCallsContact]): Ui[Any] =
    addLastCallContacts(contacts, (contact: LastCallsContact) => {
      if (isTabsOpened) {
        closeTabs.run
      } else {
        execute(phoneToNineCardIntent(contact.number))
      }
    })

}
