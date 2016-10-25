package cards.nine.process.device.utils

import cards.nine.models.types._

trait KnownCategoriesUtil {

  def findCategory(packageName: String): Option[NineCardsCategory] =
    knownCategories.find(_._1 == packageName).map(_._2)

  private[this] lazy val knownCategories = Seq(
    ("com.google.android.youtube", MediaAndVideo),
    ("org.videolan.vlc", MediaAndVideo),
    ("org.videolan.vlc.betav7neon", MediaAndVideo),
    ("com.google.android.videos", MediaAndVideo),
    ("com.netflix.mediaclient", MediaAndVideo),
    ("com.imdb.mobile", MediaAndVideo),
    ("com.google.android.music", MusicAndAudio),
    ("com.spotify.music", MusicAndAudio),
    ("com.soundcloud.android", MusicAndAudio),
    ("com.shazam.android", MusicAndAudio),
    ("com.andrwq.recorder", MusicAndAudio),
    ("com.google.android.apps.photos", Photography),
    ("com.instagram.boomerang", Photography),
    ("com.instagram.layout", Photography),
    ("com.google.android.GoogleCamera", Photography),
    ("com.picsart.studio", Photography),
    ("com.boxer.calendar", Productivity),
    ("com.boxer.calendar", Productivity),
    ("com.dropbox.android", Productivity),
    ("com.boxer.calendar", Productivity),
    ("com.google.android.apps.inbox", Productivity),
    ("com.google.android.apps.docs", Productivity),
    ("com.google.android.apps.docs.editors.docs", Productivity),
    ("com.google.android.apps.docs.editors.slides", Productivity),
    ("com.pinterest", Social),
    ("com.twitter.android", Social),
    ("com.facebook.katana", Social),
    ("com.pinterest", Social),
    ("com.instagram.android", Social),
    ("com.tumblr", Social),
    ("com.google.android.apps.plus", Social),
    ("com.whatsapp", Communication),
    ("com.skype.raider", Communication),
    ("com.microsoft.cortana", Communication),
    ("com.google.android.talk", Communication),
    ("com.facebook.orca", Communication),
    ("jp.naver.line.android", Communication),
    ("com.google.android.gm", Communication),
    ("com.android.messaging", Communication),
    ("com.Slack", Communication),
    ("com.google.android.apps.magazines", NewsAndMagazines),
    ("com.devhd.feedly", NewsAndMagazines),
    ("flipboard.app", NewsAndMagazines),
    ("com.google.android.play.games", Entertainment),
    ("com.android.deskclock", Tools),
    ("com.google.android.apps.translate", Tools),
    ("com.google.android.calculator", Tools),
    ("com.android.calculator2", Tools),
    ("com.android.contacts", Tools),
    ("com.android.chrome", Tools),
    ("com.quickoffice.android", Tools)
  )

}
