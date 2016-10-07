package cards.nine.app.ui.wizard.jobs

import cards.nine.app.ui.components.widgets.{WizardCheckBox, WizardWifiCheckBox}
import cards.nine.models.types.NineCardsMoment
import cards.nine.process.collection.models.PackagesByCategory
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}

trait WizardDOM {

  finder: TypedFindView =>

  lazy val rootLayout = finder.findView(TR.wizard_root)

  lazy val loadingRootLayout = finder.findView(TR.wizard_loading_content)

  lazy val loadingBar = finder.findView(TR.wizard_loading_bar)

  lazy val loadingText = finder.findView(TR.wizard_loading_text)

  lazy val userRootLayout = finder.findView(TR.wizard_user_content)

  lazy val usersTerms = finder.findView(TR.wizard_user_terms)

  lazy val userAction = finder.findView(TR.wizard_user_action)

  lazy val titleDevice = finder.findView(TR.wizard_device_title)

  lazy val deviceRootLayout = finder.findView(TR.wizard_device_content)

  lazy val devicesGroup = finder.findView(TR.wizard_device_group)

  lazy val deviceAction = finder.findView(TR.wizard_device_action)

  lazy val stepsAction = finder.findView(TR.wizard_steps_action)

  lazy val wizardRootLayout = finder.findView(TR.wizard_steps_content)

  lazy val paginationPanel = finder.findView(TR.wizard_steps_pagination_panel)

  lazy val workspaces = finder.findView(TR.wizard_steps_workspace)

  lazy val newConfigurationContent = finder.findView(TR.wizard_steps_new_configuration_content)

  lazy val newConfigurationStep = finder.findView(TR.wizard_steps_new_configuration_step)

  lazy val newConfigurationPagers = finder.findView(TR.wizard_steps_new_configuration_pager)

  lazy val newConfigurationNext = finder.findView(TR.wizard_steps_new_configuration_next)

  def newConfigurationStep0HeaderContent = finder.findView(TR.wizard_steps_new_configuration_step0_header_content)

  def newConfigurationStep0HeaderImage = finder.findView(TR.wizard_steps_new_configuration_step0_header_image)

  def newConfigurationStep0Title = finder.findView(TR.wizard_steps_new_configuration_step0_title)

  def newConfigurationStep0Description = finder.findView(TR.wizard_steps_new_configuration_step0_description)

  def newConfigurationStep1Title = finder.findView(TR.wizard_steps_new_configuration_step1_title)

  def newConfigurationStep1Description = finder.findView(TR.wizard_steps_new_configuration_step1_description)

  def newConfigurationStep1AllApps = finder.findView(TR.wizard_steps_new_configuration_step1_all_apps)

  def newConfigurationStep1Best9 = finder.findView(TR.wizard_steps_new_configuration_step1_best9)

  def newConfigurationStep1CollectionCount = finder.findView(TR.wizard_steps_new_configuration_step1_collection_count)

  def newConfigurationStep1CollectionsContent = finder.findView(TR.wizard_steps_new_configuration_step1_collection_content)

  def newConfigurationStep2HeaderContent = finder.findView(TR.wizard_steps_new_configuration_step2_header_content)

  def newConfigurationStep2HeaderImage1 = finder.findView(TR.wizard_steps_new_configuration_step2_header_image1)

  def newConfigurationStep2HeaderImage2 = finder.findView(TR.wizard_steps_new_configuration_step2_header_image2)

  def newConfigurationStep2Title = finder.findView(TR.wizard_steps_new_configuration_step2_title)

  def newConfigurationStep2Description = finder.findView(TR.wizard_steps_new_configuration_step2_description)

  def newConfigurationStep3WifiContent = finder.findView(TR.wizard_steps_new_configuration_step3_wifi_content)

  def newConfigurationStep4Music = finder.findView(TR.wizard_moment_step4_music)

  def newConfigurationStep4Car = finder.findView(TR.wizard_moment_step4_car)

  def newConfigurationStep4Running = finder.findView(TR.wizard_moment_step4_running)

  def newConfigurationStep4Bike = finder.findView(TR.wizard_moment_step4_bike)

  def newConfigurationStep5HeaderContent = finder.findView(TR.wizard_steps_new_configuration_step5_header_content)

  def newConfigurationStep5HeaderImage = finder.findView(TR.wizard_steps_new_configuration_step5_header_image)

  def newConfigurationStep5Title = finder.findView(TR.wizard_steps_new_configuration_step5_title)

  def newConfigurationStep5Description = finder.findView(TR.wizard_steps_new_configuration_step5_description)

  def newConfigurationStep5GoTo9Cards = finder.findView(TR.wizard_moment_step5_go_to_9cards)

  def getWizardCheckBoxes: Seq[WizardCheckBox] = (0 to newConfigurationStep1CollectionsContent.getChildCount) flatMap { position =>
    newConfigurationStep1CollectionsContent.getChildAt(position) match {
      case widget: WizardCheckBox => Some(widget)
      case _ => None
    }
  }

  def getWizardWifiCheckBoxes: Seq[WizardWifiCheckBox] = (0 to newConfigurationStep3WifiContent.getChildCount) flatMap { position =>
    newConfigurationStep3WifiContent.getChildAt(position) match {
      case widget: WizardWifiCheckBox => Some(widget)
      case _ => None
    }
  }

  def areAllCollectionsChecked(): Boolean = getWizardCheckBoxes forall (_.isCheck)

  def countCollectionsChecked(): (Int, Int) = {
    val items = getWizardCheckBoxes
    (items count (_.isCheck), items.length)
  }

  def getCollectionsSelected: Seq[PackagesByCategory] = getWizardCheckBoxes flatMap(_.getDataIfSelected)

  def getWifisSelected: Seq[(NineCardsMoment, Option[String])] =
    getWizardWifiCheckBoxes flatMap (widget =>
      (widget.isCheck, widget.getMoment, widget.getWifiName) match {
        case (true, Some(moment), wifiName) => Option(moment, wifiName)
        case _ => None
      })

  def getMomentsSelected: Seq[NineCardsMoment] =
    Seq(
      newConfigurationStep4Music.getMomentIfSelected,
      newConfigurationStep4Car.getMomentIfSelected,
      newConfigurationStep4Running.getMomentIfSelected,
      newConfigurationStep4Bike.getMomentIfSelected
    ).flatten

}

trait WizardUiListener {

  def onClickAcceptTermsButton(termsAccepted: Boolean): Unit

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

  def onStartNewConfiguration(): Unit

  def onLoadBetterCollections(): Unit

  def onSaveCollections(collections: Seq[PackagesByCategory], best9Apps: Boolean): Unit

  def onLoadWifiByMoment(): Unit

  def onSaveMomentsWithWifi(infoMoment: Seq[(NineCardsMoment, Option[String])]): Unit

  def onSaveMoments(moments: Seq[NineCardsMoment]): Unit

}