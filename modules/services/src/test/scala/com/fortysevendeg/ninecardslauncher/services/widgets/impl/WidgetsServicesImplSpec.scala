package com.fortysevendeg.ninecardslauncher.services.widgets.impl

import android.content.pm.PackageManager
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.services.widgets.WidgetServicesException
import com.fortysevendeg.ninecardslauncher.services.widgets.models.Conversions
import com.fortysevendeg.ninecardslauncher.services.widgets.utils.AppWidgetManagerCompat
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Errata, Answer}

trait WidgetsImplSpecification
  extends Specification
  with Mockito {

  trait WidgetsImplScope
    extends Scope
    with WidgetsServicesImplData {

    val mockContextSupport = mock[ContextSupport]
    val mockPackageManager = mock[PackageManager]

    mockContextSupport.getPackageManager returns mockPackageManager

    val mockAppWidgetManager = mock[AppWidgetManagerCompat with Conversions]
    mockAppWidgetManager.getAllProviders returns seqWidget

    val widgetsServicesImpl = new WidgetsServicesImpl {
      override protected def getAppWidgetManager(implicit context: ContextSupport) = mockAppWidgetManager
    }

  }

  trait WidgetsErrorScope {
    self : WidgetsImplScope =>

    case class CustomException(message: String, cause: Option[Throwable] = None)
      extends RuntimeException(message)

    val exception = CustomException("")

    mockAppWidgetManager.getAllProviders throws exception
  }

}

class WidgetsServicesImplSpec
  extends  WidgetsImplSpecification {

  "returns the list of widgets" in
    new WidgetsImplScope {
      val result = widgetsServicesImpl.getWidgets(mockContextSupport).run.run
      result must beLike {
        case Answer(resultWidgetsList) => resultWidgetsList shouldEqual seqWidget
      }
    }

  "returns an WidgetException when no widgets exist" in
    new WidgetsImplScope with WidgetsErrorScope {
      val result = widgetsServicesImpl.getWidgets(mockContextSupport).run.run
      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, widgetsException)) => widgetsException must beLike {
            case e: WidgetServicesException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }

}
