package com.fortysevendeg.ninecardslauncher.services.shortcuts.impl

import android.content.Intent
import android.content.pm.{ApplicationInfo, ActivityInfo, ResolveInfo, PackageManager}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.services.shortcuts.ShortcutServicesException
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.Shortcut
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Errata, Answer}
import scala.collection.JavaConversions._

trait ShortcutsImplSpecification
  extends Specification
  with Mockito {

  trait ShortcutsImplScope
    extends Scope
    with ShortcutsServicesImplData {

    val packageManager = mock[PackageManager]
    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns packageManager

    val mockIntent = mock[Intent]

    def createMockResolveInfo(sampleShortCut: Shortcut) : ResolveInfo = {
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

    val mockShortCuts = List(createMockResolveInfo(sampleShortcut1), createMockResolveInfo(sampleShortcut2))

    packageManager.queryIntentActivities(mockIntent, 0) returns mockShortCuts

    val shortcutsServicesImpl = new ShortcutsServicesImpl {
      override protected def shortcutsIntent(): Intent = mockIntent
    }
  }

  trait ShortCutsErrorScope {
    self : ShortcutsImplScope =>

    case class CustomException(message: String, cause: Option[Throwable] = None)
      extends RuntimeException(message)

    val exception = CustomException("")

    packageManager.queryIntentActivities(mockIntent, 0) throws exception

  }

}

class ShortcutsServicesImplSpec
  extends  ShortcutsImplSpecification {

  "returns the ordered list of shortcuts when they exist" in
    new ShortcutsImplScope {
      val result = shortcutsServicesImpl.getShortcuts(contextSupport).run.run
      result must beLike {
        case Answer(resultShortCutList) => resultShortCutList shouldEqual shotcutsList.sortBy(_.title)
      }
    }

  "returns an ShortcutException when no shortcuts exist" in
    new ShortcutsImplScope with ShortCutsErrorScope {
      val result = shortcutsServicesImpl.getShortcuts(contextSupport).run.run
      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, shortcutsException)) => shortcutsException must beLike {
            case e: ShortcutServicesException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }

}
