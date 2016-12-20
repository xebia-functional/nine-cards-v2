package cards.nine.app.ui.wizard.jobs.uiactions

import android.app.Activity
import cards.nine.app.ui.commons.ActivityFindViews
import cards.nine.app.ui.components.widgets.{WizardCheckBox, WizardWifiCheckBox}
import cards.nine.models.PackagesByCategory
import cards.nine.models.types.NineCardsMoment
import com.fortysevendeg.ninecardslauncher.TR

class WizardDOM(activity: Activity) {

  import ActivityFindViews._

  lazy val rootLayout = findView(TR.wizard_root).run(activity)

  lazy val loadingRootLayout =
    findView(TR.wizard_loading_content).run(activity)

  lazy val loadingBar = findView(TR.wizard_loading_bar).run(activity)

  lazy val loadingText = findView(TR.wizard_loading_text).run(activity)

  lazy val userRootLayout = findView(TR.wizard_user_content).run(activity)

  lazy val usersTerms = findView(TR.wizard_user_terms).run(activity)

  lazy val userLogo = findView(TR.wizard_user_logo).run(activity)

  lazy val userTitle = findView(TR.wizard_user_title).run(activity)

  lazy val userAction = findView(TR.wizard_user_action).run(activity)

  lazy val titleDevice = findView(TR.wizard_device_title).run(activity)

  lazy val deviceRootLayout = findView(TR.wizard_device_content).run(activity)

  lazy val devicesGroup = findView(TR.wizard_device_group).run(activity)

  lazy val deviceAction = findView(TR.wizard_device_action).run(activity)

  lazy val stepsBackground = findView(TR.wizard_steps_background).run(activity)

  lazy val stepsAction = findView(TR.wizard_steps_action).run(activity)

  lazy val stepsDownloadingMessage =
    findView(TR.wizard_steps_downloading_message).run(activity)

  lazy val wizardRootLayout = findView(TR.wizard_steps_content).run(activity)

  lazy val paginationPanel =
    findView(TR.wizard_steps_pagination_panel).run(activity)

  lazy val workspaces = findView(TR.wizard_steps_workspace).run(activity)

  lazy val newConfigurationContent =
    findView(TR.wizard_steps_new_configuration_content).run(activity)

  lazy val newConfigurationStep =
    findView(TR.wizard_steps_new_configuration_step).run(activity)

  lazy val newConfigurationPagers =
    findView(TR.wizard_steps_new_configuration_pager).run(activity)

  lazy val newConfigurationNext =
    findView(TR.wizard_steps_new_configuration_next).run(activity)

  lazy val newConfigurationNextText =
    findView(TR.wizard_steps_new_configuration_next_text).run(activity)

  lazy val newConfigurationNextIcon =
    findView(TR.wizard_steps_new_configuration_next_icon).run(activity)

  def newConfigurationStep0HeaderContent =
    findView(TR.wizard_steps_new_configuration_step0_header_content).run(activity)

  def newConfigurationStep0HeaderImage =
    findView(TR.wizard_steps_new_configuration_step0_header_image).run(activity)

  def newConfigurationStep0Title =
    findView(TR.wizard_steps_new_configuration_step0_title).run(activity)

  def newConfigurationStep0Description =
    findView(TR.wizard_steps_new_configuration_step0_description).run(activity)

  def newConfigurationStep1Title =
    findView(TR.wizard_steps_new_configuration_step1_title).run(activity)

  def newConfigurationStep1Description =
    findView(TR.wizard_steps_new_configuration_step1_description).run(activity)

  def newConfigurationStep1AllCollections =
    findView(TR.wizard_steps_new_configuration_step1_all_collections).run(activity)

  def newConfigurationStep1CollectionCount =
    findView(TR.wizard_steps_new_configuration_step1_collection_count).run(activity)

  def newConfigurationStep1CollectionsContent =
    findView(TR.wizard_steps_new_configuration_step1_collection_content).run(activity)

  def newConfigurationStep2HeaderContent =
    findView(TR.wizard_steps_new_configuration_step2_header_content).run(activity)

  def newConfigurationStep2HeaderImage1 =
    findView(TR.wizard_steps_new_configuration_step2_header_image1).run(activity)

  def newConfigurationStep2HeaderImage2 =
    findView(TR.wizard_steps_new_configuration_step2_header_image2).run(activity)

  def newConfigurationStep2Title =
    findView(TR.wizard_steps_new_configuration_step2_title).run(activity)

  def newConfigurationStep2Description =
    findView(TR.wizard_steps_new_configuration_step2_description).run(activity)

  def newConfigurationStep3WifiContent =
    findView(TR.wizard_steps_new_configuration_step3_wifi_content).run(activity)

  def newConfigurationStep4Music =
    findView(TR.wizard_moment_step4_music).run(activity)

  def newConfigurationStep4Car =
    findView(TR.wizard_moment_step4_car).run(activity)

  def newConfigurationStep4Sport =
    findView(TR.wizard_moment_step4_sport).run(activity)

  def newConfigurationStep5HeaderContent =
    findView(TR.wizard_steps_new_configuration_step5_header_content).run(activity)

  def newConfigurationStep5HeaderImage =
    findView(TR.wizard_steps_new_configuration_step5_header_image).run(activity)

  def newConfigurationStep5Title =
    findView(TR.wizard_steps_new_configuration_step5_title).run(activity)

  def newConfigurationStep5Description =
    findView(TR.wizard_steps_new_configuration_step5_description).run(activity)

  def newConfigurationStep5GoTo9Cards =
    findView(TR.wizard_moment_step5_go_to_9cards).run(activity)

  def getWizardCheckBoxes: Seq[WizardCheckBox] =
    (0 to newConfigurationStep1CollectionsContent.getChildCount) flatMap { position =>
      newConfigurationStep1CollectionsContent.getChildAt(position) match {
        case widget: WizardCheckBox => Some(widget)
        case _                      => None
      }
    }

  def getWizardWifiCheckBoxes: Seq[WizardWifiCheckBox] =
    (0 to newConfigurationStep3WifiContent.getChildCount) flatMap { position =>
      newConfigurationStep3WifiContent.getChildAt(position) match {
        case widget: WizardWifiCheckBox => Some(widget)
        case _                          => None
      }
    }

  def areAllCollectionsChecked(): Boolean =
    getWizardCheckBoxes forall (_.isCheck)

  def countCollectionsChecked(): (Int, Int) = {
    val items = getWizardCheckBoxes
    (items count (_.isCheck), items.length)
  }

  def getCollectionsSelected: Seq[PackagesByCategory] =
    getWizardCheckBoxes flatMap (_.getDataIfSelected)

  def getWifisSelected: Seq[(NineCardsMoment, Option[String])] =
    getWizardWifiCheckBoxes flatMap (widget =>
                                       (widget.isCheck, widget.getMoment, widget.getWifiName) match {
                                         case (true, Some(moment), wifiName) =>
                                           Option(moment, wifiName)
                                         case _ => None
                                       })

  def getMomentsSelected: Seq[NineCardsMoment] =
    Seq(
      newConfigurationStep4Music.getMomentIfSelected,
      newConfigurationStep4Car.getMomentIfSelected,
      newConfigurationStep4Sport.getMomentIfSelected).flatten

}

trait WizardUiListener {

  def onClickAcceptTermsButton(): Unit

  def onClickSelectV1DeviceButton(packages: Seq[PackagesByCategory]): Unit

  def onClickSelectDeviceButton(maybeCloudId: Option[String]): Unit

  def onClickFinishWizardButton(): Unit

  def onClickOkMarketPermissionDialog(): Unit

  def onClickCancelMarketPermissionDialog(): Unit

  def onClickOkGooglePermissionDialog(): Unit

  def onClickCancelGooglePermissionDialog(): Unit

  def onClickOkSelectAccountsDialog(): Unit

  def onClickCancelSelectAccountsDialog(): Unit

  def onClickOkPermissionsDialog(): Unit

  def onClickCancelPermissionsDialog(): Unit

  def onStartLoadConfiguration(cloudId: String): Unit

  def onStartNewConfiguration(packages: Seq[PackagesByCategory]): Unit

  def onLoadBetterCollections(packages: Seq[PackagesByCategory]): Unit

  def onSaveCollections(collections: Seq[PackagesByCategory]): Unit

  def onLoadMomentWithWifi(): Unit

  def onSaveMomentsWithWifi(infoMoment: Seq[(NineCardsMoment, Option[String])]): Unit

  def onSaveMoments(moments: Seq[NineCardsMoment]): Unit

}
