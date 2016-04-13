package com.fortysevendeg.ninecardslauncher.app.ui.collections.prefs

import java.util

import android.annotation.TargetApi
import android.app.SharedElementCallback
import android.os.Build
import android.support.v7.widget.Toolbar
import android.transition.{Transition, TransitionInflater}
import android.view.{ViewGroup, Gravity, View}
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsPagerPresenter
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ActivityContextWrapper

import scala.collection.JavaConversions._
import macroid._

trait AnimationsPref {

  val iconContent: Option[FrameLayout]

  val toolbar: Option[Toolbar]

  val root: Option[FrameLayout]

  def configureEnterTransition(
    position: Int)(implicit presenter: CollectionsPagerPresenter, activityContextWrapper: ActivityContextWrapper) = Lollipop.ifSupportedThen {
    configureEnterTransitionLollipop(position)
  } getOrElse presenter.ensureDrawCollection(position)

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private[this] def configureEnterTransitionLollipop(
    position: Int)(implicit presenter: CollectionsPagerPresenter, activityContextWrapper: ActivityContextWrapper) = {

    val activity = activityContextWrapper.getOriginal

    val enterTransition = TransitionInflater.from(activity).inflateTransition(R.transition.shared_element_enter_collection_detail)
    activity.getWindow.setSharedElementEnterTransition(enterTransition)

    iconContent foreach (_.setTransitionName(getContentTransitionName(position)))

    activity.setEnterSharedElementCallback(new SharedElementCallback {

      var snapshot: Option[View] = None

      override def onSharedElementStart(
        sharedElementNames: util.List[String],
        sharedElements: util.List[View],
        sharedElementSnapshots: util.List[View]): Unit = {
        addSnapshot(sharedElementNames, sharedElements, sharedElementSnapshots, relayoutContainer = false)
        ((snapshot <~ vVisible) ~ (toolbar <~ vInvisible)).run
      }

      override def onSharedElementEnd(
        sharedElementNames: util.List[String],
        sharedElements: util.List[View],
        sharedElementSnapshots: util.List[View]): Unit = {
        addSnapshot(sharedElementNames, sharedElements, sharedElementSnapshots, relayoutContainer = true)
        ((snapshot <~ vInvisible) ~ (toolbar <~ vVisible)).run
      }

      override def onMapSharedElements(
        names: util.List[String],
        sharedElements: util.Map[String, View]): Unit = (toolbar <~ vInvisible).run

      private[this] def addSnapshot(
        sharedElementNames: util.List[String],
        sharedElements: util.List[View],
        sharedElementSnapshots: util.List[View],
        relayoutContainer: Boolean) = {
        if (snapshot.isEmpty) {
          val transitionName = getContentTransitionName(position)
          sharedElementNames.zipWithIndex foreach {
            case (name, index) if name.equals(transitionName) =>
              val element = sharedElements.get(index).asInstanceOf[FrameLayout]
              val snapshotView = sharedElementSnapshots.get(index)
              val width = snapshotView.getWidth
              val height = snapshotView.getHeight
              val layoutParams = new FrameLayout.LayoutParams(width, height)
              layoutParams.gravity = Gravity.CENTER
              val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
              val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
              snapshotView.measure(widthSpec, heightSpec)
              snapshotView.layout(0, 0, width, height)
              snapshotView.setTransitionName(snapshotName)
              if (relayoutContainer) {
                root match {
                  case Some(container: ViewGroup) =>
                    val left = (container.getWidth - width) / 2
                    val top = (container.getHeight - height) / 2
                    element.measure(widthSpec, heightSpec)
                    element.layout(left, top, left + width, top + height)
                  case _ =>
                }
              }
              snapshot = Option(snapshotView)
              element.addView(snapshotView, layoutParams)
            case _ =>
          }
        }
      }

    })

    activity.getWindow.getSharedElementEnterTransition.addListener(new Transition.TransitionListener {
      override def onTransitionStart(transition: Transition): Unit = {}

      override def onTransitionCancel(transition: Transition): Unit = {}

      override def onTransitionEnd(transition: Transition): Unit = presenter.ensureDrawCollection(position)

      override def onTransitionPause(transition: Transition): Unit = {}

      override def onTransitionResume(transition: Transition): Unit = {}
    })
  }

}
