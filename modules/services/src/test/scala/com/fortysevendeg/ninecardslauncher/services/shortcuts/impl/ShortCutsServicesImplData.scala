package com.fortysevendeg.ninecardslauncher.services.shortcuts.impl

import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.ShortCut

trait ShortCutsServicesImplData {

  val sampleShortCut1 =  ShortCut(
    title = "Sample Name 1",
    icon = 0,
    name = "Name 1",
    packageName = "com.fortysevendeg.ninecardslauncher.test.sampleapp1")

  val sampleShortCut2 =  ShortCut(
    title = "Sample Name 2",
    icon = 0,
    name = "Name 2",
    packageName = "com.fortysevendeg.ninecardslauncher.test.sampleapp2")

  val shotCutsList = Seq(sampleShortCut1, sampleShortCut2)

}
