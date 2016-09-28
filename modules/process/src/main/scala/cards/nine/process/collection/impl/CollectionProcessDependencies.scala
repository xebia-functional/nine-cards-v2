package cards.nine.process.collection.impl

import cards.nine.process.collection.CollectionProcessConfig
import cards.nine.services.api.ApiServices
import cards.nine.services.apps.AppsServices
import cards.nine.services.awareness.AwarenessServices
import cards.nine.services.contacts.ContactsServices
import cards.nine.services.persistence.PersistenceServices

trait CollectionProcessDependencies {

  val collectionProcessConfig: CollectionProcessConfig
  val persistenceServices: PersistenceServices
  val contactsServices: ContactsServices
  val appsServices: AppsServices
  val apiServices: ApiServices
  val awarenessServices: AwarenessServices

}
