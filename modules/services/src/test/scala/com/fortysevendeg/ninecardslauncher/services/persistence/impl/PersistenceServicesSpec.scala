package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.services.persistence.data.PersistenceServicesData
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

trait PersistenceServicesSpecification
  extends Specification
    with DisjunctionMatchers
    with Mockito {

  trait ValidRepositoryServicesResponses extends RepositoryServicesScope with PersistenceServicesData {

  }

  trait ErrorRepositoryServicesResponses extends RepositoryServicesScope with PersistenceServicesData {

  }

}

class PersistenceServicesSpec
  extends PersistenceServicesSpecification {



}
