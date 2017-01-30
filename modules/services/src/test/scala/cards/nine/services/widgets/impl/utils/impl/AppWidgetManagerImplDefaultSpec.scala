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

package cards.nine.services.widgets.impl.utils.impl

import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.pm.PackageManager
import cards.nine.commons.contexts.ContextSupport
import cards.nine.services.widgets.utils.impl.AppWidgetManagerImplDefault
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait AppWidgetManagerDefaultImplSpecification extends Specification with Mockito {

  trait AppWidgetManagerDefaultImplScope extends Scope with AppWidgetManagerData {

    implicit val mockContextSupport = mock[ContextSupport]
    val mockPackageManager          = mock[PackageManager]

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
    ): Seq[AppWidgetProviderInfo] = List.tabulate(num)(item => appWidgetProviderInfo)

    val seqAppWidgetProviderInfo: Seq[AppWidgetProviderInfo] = createSeqAppWidgetProviderInfo()

    val appWidgetManagerImplDefault = new AppWidgetManagerImplDefault {

      override protected def getAppWidgetProviderInfo: Seq[AppWidgetProviderInfo] =
        seqAppWidgetProviderInfo

      override protected def getLabel(info: AppWidgetProviderInfo): String = label

      override protected def getUser(info: AppWidgetProviderInfo): Option[Int] = userHashCodeOption
    }
  }

}

class AppWidgetManagerDefaultImplSpec extends AppWidgetManagerDefaultImplSpecification {

  "Get All Providers" should {

    "returns the list of widgets" in
      new AppWidgetManagerDefaultImplScope {

        val result = appWidgetManagerImplDefault.getAllProviders
        result shouldEqual seqWidget
      }

    "returns an empty list when no AppWidgetProviderInfo is found" in
      new AppWidgetManagerDefaultImplScope {

        override val appWidgetManagerImplDefault = new AppWidgetManagerImplDefault {
          override protected def getAppWidgetProviderInfo: Seq[AppWidgetProviderInfo] = Nil
        }

        val result = appWidgetManagerImplDefault.getAllProviders
        result shouldEqual Nil
      }
  }

}
