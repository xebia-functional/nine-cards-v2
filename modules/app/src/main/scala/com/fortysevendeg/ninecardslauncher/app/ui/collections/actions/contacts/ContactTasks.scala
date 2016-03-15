package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.device.models.{IterableContacts, TermCounter}
import com.fortysevendeg.ninecardslauncher.process.device.{ContactException, ContactsFilter}

trait ContactTasks {

  def getLoadContacts(order: ContactsFilter)(implicit context: ContextSupport, di: Injector): ServiceDef2[(IterableContacts, Seq[TermCounter]), ContactException] =
    for {
      iterableContacts <- di.deviceProcess.getIterableContacts(order)
      counters <- di.deviceProcess.getTermCountersForContacts(order)
    } yield (iterableContacts, counters)

}
