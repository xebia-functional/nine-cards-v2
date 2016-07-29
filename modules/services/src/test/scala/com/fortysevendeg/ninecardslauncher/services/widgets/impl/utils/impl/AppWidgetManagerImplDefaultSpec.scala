package com.fortysevendeg.ninecardslauncher.services.widgets.impl.utils.impl

import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.pm.PackageManager
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.services.widgets.utils.impl.AppWidgetManagerImplDefault
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait AppWidgetManagerDefaultImplSpecification
  extends Specification
  with Mockito {

  trait AppWidgetManagerDefaultImplScope
    extends Scope
    with AppWidgetManagerData {

    implicit val mockContextSupport = mock[ContextSupport]
    val mockPackageManager = mock[PackageManager]

    mockContextSupport.getPackageManager returns mockPackageManager

    def createComponentName: ComponentName = {
      val sampleComponentName = mock[ComponentName]
      sampleComponentName.getClassName returns className
      sampleComponentName.getPackageName returns packageName
      sampleComponentName
    }

    def createAppWidgetProviderInfo: AppWidgetProviderInfo = {
      val sampleAppWidgetProviderInfo = mock[AppWidgetProviderInfo]
      sampleAppWidgetProviderInfo.autoAdvanceViewId = autoAdvanceViewId
      sampleAppWidgetProviderInfo.initialLayout = initialLayout
      sampleAppWidgetProviderInfo.minHeight = minHeight
      sampleAppWidgetProviderInfo.minResizeHeight = minResizeHeight
      sampleAppWidgetProviderInfo.minResizeWidth = minResizeWidth
      sampleAppWidgetProviderInfo.minWidth = minWidth
      sampleAppWidgetProviderInfo.resizeMode = resizeMode
      sampleAppWidgetProviderInfo.updatePeriodMillis = updatePeriodMillis
      sampleAppWidgetProviderInfo.previewImage = preview
      sampleAppWidgetProviderInfo.provider = createComponentName
      sampleAppWidgetProviderInfo
    }

    def createSeqAppWidgetProviderInfo(
      num: Int = 5,
      appWidgetProviderInfo: AppWidgetProviderInfo = createAppWidgetProviderInfo
      ): Seq[AppWidgetProviderInfo] = List.tabulate(num)(
      item => appWidgetProviderInfo)

    val seqAppWidgetProviderInfo: Seq[AppWidgetProviderInfo] = createSeqAppWidgetProviderInfo()

    val appWidgetManagerImplDefault = new AppWidgetManagerImplDefault {

      override protected def getAppWidgetProviderInfo: Seq[AppWidgetProviderInfo] = seqAppWidgetProviderInfo

      override protected def getLabel(info: AppWidgetProviderInfo): String = label

      override protected def getUser(info: AppWidgetProviderInfo): Option[Int] = userHashCodeOption
    }

  }

  trait AppWidgetManagerDefaultErrorScope {
    self : AppWidgetManagerDefaultImplScope =>

    case class CustomException(message: String, cause: Option[Throwable] = None)
      extends RuntimeException(message)

    override val appWidgetManagerImplDefault = new AppWidgetManagerImplDefault {

      override protected def getAppWidgetProviderInfo: Seq[AppWidgetProviderInfo] = Nil

    }

  }

}

class AppWidgetManagerDefaultImplSpec
  extends  AppWidgetManagerDefaultImplSpecification {

  "Get All Providers" should {

    "returns the list of widgets" in
      new AppWidgetManagerDefaultImplScope {
        val result = appWidgetManagerImplDefault.getAllProviders
        result shouldEqual seqWidget
      }

    "returns an empty list when no AppWidgetProviderInfo is found" in
      new AppWidgetManagerDefaultImplScope with AppWidgetManagerDefaultErrorScope {
        val result = appWidgetManagerImplDefault.getAllProviders
        result shouldEqual Nil
      }
  }

}
