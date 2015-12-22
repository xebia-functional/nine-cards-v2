package com.fortysevendeg.ninecardslauncher.app.ui.commons.actions

import android.support.v4.app.{Fragment, FragmentManager}
import android.view.View
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.FragmentExtras._
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}
import macroid.{ActivityContextWrapper, FragmentManagerContext, Tweak, Ui}

trait ActionsBehaviours {

  self: TypedFindView =>

  val nameActionFragment = "action-fragment"

  lazy val fragmentContent = Option(findView(TR.action_fragment_content))

  def turnOffFragmentContent(implicit activityContextWrapper: ActivityContextWrapper): Ui[_]

  def removeActionFragment(implicit managerContext: FragmentManagerContext[Fragment, FragmentManager]): Unit =
    findFragmentByTag(nameActionFragment) map removeFragment

  def isActionShowed(implicit managerContext: FragmentManagerContext[Fragment, FragmentManager]): Boolean =
    findFragmentByTag(nameActionFragment).isDefined

  def fragmentContentStyle(clickable: Boolean): Tweak[FrameLayout] = Tweak[View](_.setClickable(clickable))

  def unrevealActionFragment(implicit managerContext: FragmentManagerContext[Fragment, FragmentManager]): Ui[_] =
    findFragmentByTag[BaseActionFragment](nameActionFragment) map (_.unreveal()) getOrElse Ui.nop

}
