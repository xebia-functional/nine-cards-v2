package com.fortysevendeg.ninecardslauncher.services.contacts.impl

import com.fortysevendeg.ninecardslauncher.commons.contentresolver.ContentResolverWrapperImpl
import com.fortysevendeg.ninecardslauncher.repository.commons.GeoInfoUri
import com.fortysevendeg.ninecardslauncher.repository.provider.GeoInfoEntity._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ContactsServicesSpecification
  extends Specification
  with Mockito {

  trait ContactsServicesScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]
    lazy val contactsServices = new ContactsServicesImpl(contentResolverWrapper)
  }

  trait ValidContactsServicesResponses
    extends ContactsServicesScope
    with ContactsServicesImplData {

    self: ContactsServicesScope =>

    contentResolverWrapper.fetchAll(
      uri = GeoInfoUri,
      projection = allFields)(
        f = getListFromCursor(geoInfoEntityFromCursor)) returns Seq.empty
  }

  trait ErrorContactsServicesResponses
    extends ContactsServicesScope
    with ContactsServicesImplData {

    self: ContactsServicesScope =>
  }
}

class ContactsServicesImplSpec
extends ContactsServicesSpecification {

}
