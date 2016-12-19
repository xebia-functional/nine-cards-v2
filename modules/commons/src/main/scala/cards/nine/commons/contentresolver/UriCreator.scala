package cards.nine.commons.contentresolver

import android.net.Uri

class UriCreator {

  def parse(uriString: String): Uri = Uri.parse(uriString)

  def withAppendedPath(uri: Uri, path: String) = Uri.withAppendedPath(uri, path)

}
