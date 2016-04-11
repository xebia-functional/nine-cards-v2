package com.fortysevendeg.ninecardslauncher.app.ui.commons.actions

import android.support.v4.app.{Fragment, FragmentManager}
import com.fortysevendeg.macroid.extras.FragmentExtras._
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}
import macroid.{ContextWrapper, FragmentManagerContext, Ui}

trait ActionsBehaviours {

  self: TypedFindView =>

  val nameActionFragment = "action-fragment"

  lazy val fragmentContent = Option(findView(TR.action_fragment_content))

  def turnOffFragmentContent(implicit contextWrapper: ContextWrapper): Ui[_]

  def removeActionFragment(implicit managerContext: FragmentManagerContext[Fragment, FragmentManager]): Unit =
    findFragmentByTag(nameActionFragment) map removeFragment

  def isActionShowed(implicit managerContext: FragmentManagerContext[Fragment, FragmentManager]): Boolean =
    findFragmentByTag(nameActionFragment).isDefined

  def unrevealActionFragment(implicit managerContext: FragmentManagerContext[Fragment, FragmentManager]): Ui[_] =
    findFragmentByTag[BaseActionFragment](nameActionFragment) map (_.unreveal()) getOrElse Ui.nop

}
