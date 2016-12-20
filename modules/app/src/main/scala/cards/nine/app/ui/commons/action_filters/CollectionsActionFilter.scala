package cards.nine.app.ui.commons.action_filters

sealed trait CollectionsActionFilter {
  val action: String
}

case object CollectionAddedActionFilter extends CollectionsActionFilter {
  override val action: String = "collections-added-action-filter"
}

object CollectionsActionFilter {

  val cases = Seq(CollectionAddedActionFilter)

  def apply(action: String): Option[CollectionsActionFilter] =
    cases find (_.action == action)

}
