package cards.nine.app.ui.share.models

import android.net.Uri

sealed trait ContentType

case object Web extends ContentType

case class SharedContent(contentType: ContentType, title: String, content: String, image: Option[Uri])
