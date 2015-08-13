package com.fortysevendeg.ninecardslauncher.services.contacts.impl

import com.fortysevendeg.ninecardslauncher.commons.contentresolver.ContentResolverWrapperImpl
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
    with ContactsServicesImplData

  trait ErrorContactsServicesResponses
    extends ContactsServicesScope
    with ContactsServicesImplData

}

class ContactsServicesImplSpec
  extends ContactsServicesSpecification {

}
