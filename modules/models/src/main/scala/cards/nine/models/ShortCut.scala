package cards.nine.models

import android.content.Intent
import android.graphics.drawable.Drawable

//case class Shortcut (
//  title: String,
//  icon: Int,
//  name: String,
//  packageName: String)

case class Shortcut (
  title: String,
  icon: Option[Drawable],
  intent: Intent)
