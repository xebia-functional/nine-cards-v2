package cards.nine.app.ui.commons

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import macroid.{ActivityContextWrapper, ContextWrapper, Ui}

import scala.annotation.tailrec
import scala.ref.WeakReference
import scala.util.{Failure, Try}

trait UiExtensions {

  @tailrec
  private[this] def getData[T](
      bundles: Seq[Bundle],
      conversor: (Bundle, String) => T,
      key: String,
      default: T): T =
    bundles match {
      case Nil                                   => default
      case Seq(h, t @ _ *) if h.containsKey(key) => conversor(h, key)
      case Seq(h, t @ _ *)                       => getData(t, conversor, key, default)
    }

  def getInt(bundles: Seq[Bundle], key: String, default: Int) =
    getData(flat(bundles), (b, k) => b.getInt(k), key, default)

  def getString(bundles: Seq[Bundle], key: String, default: String) =
    getData(flat(bundles), (b, k) => b.getString(k), key, default)

  def getBoolean(bundles: Seq[Bundle], key: String, default: Boolean) =
    getData(flat(bundles), (b, k) => b.getBoolean(k), key, default)

  def getSeqString(bundles: Seq[Bundle], key: String, default: Seq[String]) =
    getData(flat(bundles), (b, k) => b.getStringArray(k).toSeq, key, default)

  def getSerialize[T](bundles: Seq[Bundle], key: String, default: T) =
    getData(flat(bundles), (b, k) => b.getSerializable(k).asInstanceOf[T], key, default)

  private[this] def flat(bundles: Seq[Bundle]): Seq[Bundle] =
    bundles flatMap (b => Option(b))

}

object SafeUi {

  def uiStartIntent(intent: Intent)(implicit c: ActivityContextWrapper): Ui[Unit] =
    startIntent(_.startActivity(intent))

  def uiOpenUrlIntent(url: String)(implicit c: ContextWrapper): Ui[Unit] = {
    val intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
    Ui {
      Try(c.bestAvailable.startActivity(intent)) match {
        case Failure(e) => AppLog.printErrorMessage(e)
        case _          =>
      }
    }
  }

  def uiStartIntentWithOptions(intent: Intent, options: ActivityOptionsCompat)(
      implicit c: ActivityContextWrapper): Ui[Unit] =
    startIntent(_.startActivity(intent, options.toBundle))

  def uiStartIntentForResult(intent: Intent, requestCode: Int)(
      implicit c: ActivityContextWrapper): Ui[Unit] =
    startIntent(_.startActivityForResult(intent, requestCode))

  def uiStartServiceIntent(intent: Intent)(implicit c: ActivityContextWrapper): Ui[Unit] =
    Ui {
      Try(c.bestAvailable.startService(intent)) match {
        case Failure(e) => AppLog.printErrorMessage(e)
        case _          =>
      }
    }

  private[this] def startIntent(f: (Activity) => Unit)(
      implicit c: ActivityContextWrapper): Ui[Unit] =
    Ui {
      Try(c.original.get foreach f) match {
        case Failure(e) => AppLog.printErrorMessage(e)
        case _          =>
      }
    }

}
