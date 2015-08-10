package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Collection, FormedCollection, UnformedItem}

trait CollectionProcess {
  def createCollectionsFromUnformedItems(apps: Seq[UnformedItem])(implicit context: ContextSupport): ServiceDef2[List[Collection], CollectionException]
  def createCollectionsFromFormedCollections(items: Seq[FormedCollection])(implicit context: ContextSupport): ServiceDef2[List[Collection], CollectionException]
  def getCollections: ServiceDef2[Seq[Collection], CollectionException]
}
