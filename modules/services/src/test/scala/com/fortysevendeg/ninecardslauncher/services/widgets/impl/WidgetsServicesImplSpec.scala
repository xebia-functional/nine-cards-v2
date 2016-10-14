package cards.nine.services.widgets.impl

import android.content.pm.PackageManager
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.models.Conversions
import cards.nine.services.widgets.WidgetServicesException
import cards.nine.services.widgets.utils.AppWidgetManagerCompat
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait WidgetsImplSpecification
  extends Specification
  with Mockito {

  trait WidgetsImplScope
    extends Scope
    with WidgetsServicesImplData {

    val mockContextSupport = mock[ContextSupport]
    val mockPackageManager = mock[PackageManager]
    val mockAppWidgetManager = mock[AppWidgetManagerCompat with Conversions]

    val widgetsServicesImpl = new WidgetsServicesImpl {
      override protected def getAppWidgetManager(implicit context: ContextSupport) = mockAppWidgetManager
    }

    val exception = WidgetServicesException("")
  }

}

class WidgetsServicesImplSpec
  extends WidgetsImplSpecification {

  "returns the list of widgets" in
    new WidgetsImplScope {

      mockContextSupport.getPackageManager returns mockPackageManager
      mockAppWidgetManager.getAllProviders returns seqWidget

      val result = widgetsServicesImpl.getWidgets(mockContextSupport).value.run
      result shouldEqual Right(seqWidget)
    }

  "returns an WidgetException when no widgets exist" in
    new WidgetsImplScope {

      mockAppWidgetManager.getAllProviders throws exception
      val result = widgetsServicesImpl.getWidgets(mockContextSupport).value.run
      result must beAnInstanceOf[Left[WidgetServicesException, _]]
    }

}
