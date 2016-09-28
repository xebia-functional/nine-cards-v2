package com.fortysevendeg.ninecardslauncher.app.ui.commons.actions

import android.support.v4.app.{Fragment, FragmentManager}
import com.fortysevendeg.macroid.extras.FragmentExtras._
import macroid.{FragmentManagerContext, Ui}
import ActionsBehaviours._

trait ActionsBehaviours {

  def removeActionFragment(implicit managerContext: FragmentManagerContext[Fragment, FragmentManager]): Unit =
    findFragmentByTag(nameActionFragment) map removeFragment

  def isActionShowed(implicit managerContext: FragmentManagerContext[Fragment, FragmentManager]): Boolean =
    findFragmentByTag(nameActionFragment).isDefined

  def unrevealActionFragment(implicit managerContext: FragmentManagerContext[Fragment, FragmentManager]): Ui[_] =
    findFragmentByTag[BaseActionFragment](nameActionFragment) map (_.unreveal()) getOrElse Ui.nop

}

object ActionsBehaviours {
  val nameActionFragment = "action-fragment"
}