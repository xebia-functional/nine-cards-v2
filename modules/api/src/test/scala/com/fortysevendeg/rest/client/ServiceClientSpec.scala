package com.fortysevendeg.rest.client

import com.fortysevendeg.BaseTestSupport
import com.squareup.okhttp.OkHttpClient
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ServiceClientSupport
  extends ServiceClient
  with Mockito {

  override val baseUrl = ""

  override val okHttpClient: OkHttpClient = mock[OkHttpClient]

}

trait ServiceClientSpec
    extends Specification
    with Mockito
    with Scope
    with BaseTestSupport {

  "ServiceClient" should {

    "return " in new ServiceClientSupport {


      true shouldEqual true
    }

  }

}
