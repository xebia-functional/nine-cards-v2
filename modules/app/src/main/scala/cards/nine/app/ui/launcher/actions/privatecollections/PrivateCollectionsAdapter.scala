package cards.nine.app.ui.launcher.actions.privatecollections

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, ViewGroup}
import cards.nine.app.ui.commons.UiContext
import cards.nine.models.CollectionData
import cards.nine.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.TR
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import macroid.ActivityContextWrapper

case class PrivateCollectionsAdapter(privateCollections: Seq[CollectionData])
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], presenter: PrivateCollectionsPresenter, theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderPrivateCollectionsLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPrivateCollectionsLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.private_collections_item, parent, false)
    ViewHolderPrivateCollectionsLayoutAdapter(view)
  }

  override def getItemCount: Int = privateCollections.size

  override def onBindViewHolder(viewHolder: ViewHolderPrivateCollectionsLayoutAdapter, position: Int): Unit = {
    val privateCollection = privateCollections(position)
    viewHolder.bind(privateCollection, position).run
  }

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}
