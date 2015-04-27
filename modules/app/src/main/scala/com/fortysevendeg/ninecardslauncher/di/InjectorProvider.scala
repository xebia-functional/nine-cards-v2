package com.fortysevendeg.ninecardslauncher.di

import android.app.{Activity, Application}
import android.support.v4.app.Fragment

trait InjectorProvider {
  
  def application: Application

  implicit def di: DependencyInjector =
    application match {
      case injector: DependencyInjector => injector
      case _ => throw new IllegalStateException("Application must be a DependencyInjector")
    }
}

trait ActivityInjectorProvider extends InjectorProvider {

  self: Activity =>

  def application = getApplication

}

trait FragmentInjectorProvider extends InjectorProvider {

  self: Fragment =>
  
  def application = (Option(getActivity) map (_.getApplication)).getOrElse(throw new IllegalStateException("Activity not accessible"))
  
}
