package cards.nine.services.shortcuts.impl

import android.content.ComponentName
import android.content.pm.{ActivityInfo, ApplicationInfo, PackageManager, ResolveInfo}
import android.graphics.drawable.Drawable
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.models.Shortcut
import cards.nine.services.shortcuts.ShortcutServicesException
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

import scala.collection.JavaConversions._

trait ShortcutsImplSpecification extends TaskServiceSpecification with Mockito {

  trait ShortcutsImplScope extends Scope with ShortcutsServicesImplData {

    val packageManager = mock[PackageManager]
    val contextSupport = mock[ContextSupport]
    val mockIcon       = mock[Drawable]
    contextSupport.getPackageManager returns packageManager

    def createMockResolveInfo(sampleShortcut: Shortcut): ResolveInfo = {
      val sampleResolveInfo   = mock[ResolveInfo]
      val mockActivityInfo    = mock[ActivityInfo]
      val mockApplicationInfo = mock[ApplicationInfo]
      sampleResolveInfo.loadLabel(packageManager) returns sampleShortcut.title

      mockApplicationInfo.packageName = packageName
      mockActivityInfo.applicationInfo = mockApplicationInfo
      mockActivityInfo.name = name
      packageManager.getActivityIcon(any[ComponentName]) returns mockIcon
      sampleResolveInfo.activityInfo = mockActivityInfo
      sampleResolveInfo
    }

    val mockShortcuts =
      List(createMockResolveInfo(sampleShortcut1), createMockResolveInfo(sampleShortcut2))

    val shortcutsServicesImpl = new ShortcutsServicesImpl
  }
}

class ShortcutsServicesImplSpec extends ShortcutsImplSpecification {

  "returns the ordered list of shortcuts when they exist" in
    new ShortcutsImplScope { //TODO  we need to improve this tests - issue #907

      packageManager.queryIntentActivities(any, any) returns mockShortcuts

      shortcutsServicesImpl.getShortcuts(contextSupport).mustRight { result =>
        result.size shouldEqual shotcutsList.size
      }
    }

  "returns an ShortcutException when no shortcuts exist" in
    new ShortcutsImplScope {

      val exception = ShortcutServicesException("")
      packageManager.queryIntentActivities(any, any) throws exception

      shortcutsServicesImpl.getShortcuts(contextSupport).mustLeft[ShortcutServicesException]
    }

}
