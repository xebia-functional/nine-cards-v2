package cards.nine.app.ui.commons.adapters.sharedcollections

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import cards.nine.app.ui.commons.UiContext
import cards.nine.models.NineCardsTheme
import macroid._

case class ViewHolderSharedCollectionsLayoutAdapter(content: ViewGroup)(
    implicit val context: ActivityContextWrapper,
    val uiContext: UiContext[_],
    theme: NineCardsTheme)
    extends RecyclerView.ViewHolder(content)
    with SharedCollectionItem {

  initialize().run

}
