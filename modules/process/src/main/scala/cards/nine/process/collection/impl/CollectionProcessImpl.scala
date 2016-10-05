package cards.nine.process.collection.impl

import cards.nine.process.collection._
import cards.nine.services.api.ApiServices
import cards.nine.services.apps.AppsServices
import cards.nine.services.awareness.AwarenessServices
import cards.nine.services.contacts.ContactsServices
import cards.nine.services.persistence.{ImplicitsPersistenceServiceExceptions, PersistenceServices}

class CollectionProcessImpl(
  val collectionProcessConfig: CollectionProcessConfig,
  val persistenceServices: PersistenceServices,
  val contactsServices: ContactsServices,
  val appsServices: AppsServices,
  val apiServices: ApiServices,
  val awarenessServices: AwarenessServices)
  extends CollectionProcess
  with CollectionProcessDependencies
  with CollectionsProcessImpl
  with CardsProcessImpl
  with ImplicitsPersistenceServiceExceptions
  with FormedCollectionConversions
  with FormedCollectionDependencies
