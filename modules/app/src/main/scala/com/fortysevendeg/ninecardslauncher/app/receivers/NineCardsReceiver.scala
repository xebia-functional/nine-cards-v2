package com.fortysevendeg.ninecardslauncher.app.receivers

import android.content.{ComponentName, Intent, Context, BroadcastReceiver}
import android.net.Uri
import android.widget.Toast
import com.fortysevendeg.ninecardslauncher2.R

import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardsIntentExtras._

import scala.util.{Success, Failure, Try}

class NineCardsReceiver
  extends BroadcastReceiver {

  override def onReceive(context: Context, intent: Intent): Unit = {
    intent.getAction match {
      case `openApp` =>
        createIntentForApp(intent) map {
          i =>
            Try(context.startActivity(i)) match {
              case Success(e) =>
              case Failure(ex) => goToGooglePlay(context, intent)
            }
        } getOrElse {
          tryLaunchPackage(context, intent)
        }
      case _ =>
    }
  }

  private def createIntentForApp(intent: Intent): Option[Intent] = for {
    packageName <- extractPackageName(intent)
    className <- extractClassName(intent)
  } yield {
      val intent = new Intent(Intent.ACTION_MAIN)
      intent.addCategory(Intent.CATEGORY_LAUNCHER)
      intent.setComponent(new ComponentName(packageName, className))
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
      intent
    }

  private def goToGooglePlay(context: Context, intent: Intent) =
    extractPackageName(intent) map {
      pn =>
        val intent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.google_play_url, pn)))
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        Try (context.startActivity(intent)) match {
          case Success(e) =>
          case Failure(ex) => Toast.makeText(context, R.string.contactUsError, Toast.LENGTH_LONG).show()
        }
    } getOrElse {
      Toast.makeText(context, R.string.contactUsError, Toast.LENGTH_LONG).show()
    }

  private def tryLaunchPackage(context: Context, intent: Intent) =
    extractPackageName(intent) map {
      pn =>
        Try(context.startActivity(context.getPackageManager.getLaunchIntentForPackage(pn))) match {
          case Success(e) =>
          case Failure(ex) => goToGooglePlay(context, intent)
        }
    } getOrElse {
      Toast.makeText(context, R.string.contactUsError, Toast.LENGTH_LONG).show()
    }

  private def extractPackageName(intent: Intent): Option[String] =
    Option(intent.getStringExtra(nineCardExtraPackageName))

  private def extractClassName(intent: Intent): Option[String] =
    Option(intent.getStringExtra(nineCardExtraClassName))

}
