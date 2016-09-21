package com.fortysevendeg.ninecardslauncher.services.shortcuts.impl

import android.content.Intent
import android.content.pm.{ActivityInfo, ApplicationInfo, PackageManager, ResolveInfo}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.services.shortcuts.ShortcutServicesException
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.Shortcut
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._


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

    def createMockResolveInfo(sampleShortcut: Shortcut) : ResolveInfo = {
      val sampleResolveInfo = mock[ResolveInfo]
      val mockActivityInfo = mock[ActivityInfo]
      val mockApplicationInfo = mock[ApplicationInfo]
      sampleResolveInfo.loadLabel(packageManager) returns sampleShortcut.title
      mockApplicationInfo.packageName = sampleShortcut.packageName
      mockActivityInfo.applicationInfo = mockApplicationInfo
      mockActivityInfo.name = sampleShortcut.name
      mockActivityInfo.icon = sampleShortcut.icon
      sampleResolveInfo.activityInfo = mockActivityInfo
      sampleResolveInfo
    }

    val mockShortcuts = List(createMockResolveInfo(sampleShortcut1), createMockResolveInfo(sampleShortcut2))

    val shortcutsServicesImpl = new ShortcutsServicesImpl
  }
}

class ShortcutsServicesImplSpec
  extends ShortcutsImplSpecification {

  "returns the ordered list of shortcuts when they exist" in
    new ShortcutsImplScope {

      packageManager.queryIntentActivities(any, any) returns mockShortcuts
      val result = shortcutsServicesImpl.getShortcuts(contextSupport).value.run
      result shouldEqual Right(shotcutsList.sortBy(_.title))
    }

  "returns an ShortcutException when no shortcuts exist" in
    new ShortcutsImplScope {

      val exception = ShortcutServicesException("")
      packageManager.queryIntentActivities(any, any) throws exception

      val result = shortcutsServicesImpl.getShortcuts(contextSupport).value.run
      result must beAnInstanceOf[Left[ShortcutServicesException, _]]
    }

}
