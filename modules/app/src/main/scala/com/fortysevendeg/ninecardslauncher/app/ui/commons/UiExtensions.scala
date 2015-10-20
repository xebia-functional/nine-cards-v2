package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import macroid.{ActivityContextWrapper, Ui}

import scala.annotation.tailrec
import scala.ref.WeakReference
import scala.util.Try

trait UiExtensions {

  @tailrec
  private[this] def getData[T](bundles: Seq[Bundle], conversor: (Bundle, String) => T, key: String, default: T): T =
    bundles match {
      case Nil => default
      case Seq(h, t @ _ *) if h.containsKey(key) => conversor(h, key)
      case Seq(h, t @ _ *) => getData(t, conversor, key, default)
    }

  def getInt(bundles: Seq[Bundle], key: String, default: Int) =
    getData(flat(bundles), (b, k) => b.getInt(k), key, default)

  def getString(bundles: Seq[Bundle], key: String, default: String) =
    getData(flat(bundles), (b, k) => b.getString(k), key, default)

  def getBoolean(bundles: Seq[Bundle], key: String, default: Boolean) =
    getData(flat(bundles), (b, k) => b.getBoolean(k), key, default)

  def getSeqString(bundles: Seq[Bundle], key: String, default: Seq[String]) =
    getData(flat(bundles), (b, k) => b.getStringArray(k).toSeq, key, default)

  private[this] def flat(bundles: Seq[Bundle]): Seq[Bundle] =
    bundles flatMap (b => Option(b))

}

object SafeUi {

  def activity[A](implicit c: ActivityContextWrapper): Option[A] =
    c.original match {
      case WeakReference(activity: A) => Some(activity)
      case _ => None
    }

  def createIntent[T <: Activity](implicit c: ActivityContextWrapper, m: Manifest[T]) =
    new Intent(c.application, m.runtimeClass)

  def uiStartIntent(intent: Intent)(implicit c: ActivityContextWrapper): Ui[Unit] =
    Ui {
      Try {
        c.original.get foreach (_.startActivity(intent))
      }
    }

  def uiStartIntentWithOptions(intent: Intent, options: ActivityOptionsCompat)(implicit c: ActivityContextWrapper): Ui[Unit] =
    Ui {
      Try {
        c.original.get foreach (_.startActivity(intent, options.toBundle))
      }
    }

  def uiStartIntentForResult(intent: Intent, result: Int)(implicit c: ActivityContextWrapper): Ui[Unit] =
    Ui {
      Try {
        c.original.get foreach (_.startActivityForResult(intent, result))
      }
    }

}
