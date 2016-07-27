package com.fortysevendeg.ninecardslauncher.app.ui.collections.dialog

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.ContactsCollectionType
import macroid.{ActivityContextWrapper, Ui}

class PublishCollectionPresenter (actions: PublishCollectionActions)(implicit fragmentContextWrapper: ActivityContextWrapper)
  extends Presenter{

  def initialize(collection: Collection): Unit = actions.initialize(collection).run

}

trait PublishCollectionActions {

  def initialize(collection: Collection): Ui[Any]

}