package com.fortysevendeg.ninecardslauncher.services.shortcuts.impl

import android.content.Intent
import android.content.pm.{ApplicationInfo, ActivityInfo, ResolveInfo, PackageManager}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.services.shortcuts.ShortCutException
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.ShortCut
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Errata, Answer}
import scala.collection.JavaConversions._

trait ShortCutsImplSpecification
  extends Specification
  with Mockito {

  trait ShortCutsImplScope
    extends Scope
    with ShortCutsServicesImplData {

    val packageManager = mock[PackageManager]
    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns packageManager

    val mockIntent = mock[Intent]

    def createMockResolveInfo(sampleShortCut: ShortCut) : ResolveInfo = {
      val sampleResolveInfo = mock[ResolveInfo]
      val mockActivityInfo = mock[ActivityInfo]
      val mockApplicationInfo = mock[ApplicationInfo]
      sampleResolveInfo.loadLabel(packageManager) returns sampleShortCut.title
      mockApplicationInfo.packageName = sampleShortCut.packageName
      mockActivityInfo.applicationInfo = mockApplicationInfo
      mockActivityInfo.name = sampleShortCut.name
      mockActivityInfo.icon = sampleShortCut.icon
      sampleResolveInfo.activityInfo = mockActivityInfo
      sampleResolveInfo
    }

    val mockShortCuts = List(createMockResolveInfo(sampleShortCut1), createMockResolveInfo(sampleShortCut2))

    packageManager.queryIntentActivities(mockIntent, 0) returns mockShortCuts

    val shortcutsServicesImpl = new ShortCutsServicesImpl {
      override protected def shortCutsIntent(): Intent = mockIntent
    }
  }

  trait ShortCutsErrorScope {
    self : ShortCutsImplScope =>

    case class CustomException(message: String, cause: Option[Throwable] = None)
      extends RuntimeException(message)

    val exception = CustomException("")

    packageManager.queryIntentActivities(mockIntent, 0) throws exception

  }

}

class ShortCutsServicesImplSpec
  extends  ShortCutsImplSpecification {

  "returns the ordered list of shortcuts when they exist" in
    new ShortCutsImplScope {
      val result = shortcutsServicesImpl.getShortCuts(contextSupport).run.run
      result must beLike {
        case Answer(resultShortCutList) => resultShortCutList shouldEqual shotCutsList.sortBy(_.title)
      }
    }

  "returns an ShortCutException when no shortcuts exist" in
    new ShortCutsImplScope with ShortCutsErrorScope {
      val result = shortcutsServicesImpl.getShortCuts(contextSupport).run.run
      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, shortcutsException)) => shortcutsException must beLike {
            case e: ShortCutException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }

}
