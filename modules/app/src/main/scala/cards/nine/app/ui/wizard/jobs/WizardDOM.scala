package cards.nine.app.ui.wizard.jobs

import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}

trait WizardDOM {

  finder: TypedFindView =>

  lazy val rootLayout = finder.findView(TR.wizard_root)

  lazy val loadingRootLayout = finder.findView(TR.wizard_loading_content)

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

}