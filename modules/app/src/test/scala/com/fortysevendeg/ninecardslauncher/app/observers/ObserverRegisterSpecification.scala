package com.fortysevendeg.ninecardslauncher.app.observers

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import cards.nine.commons.contentresolver.UriCreator
import cards.nine.commons.contexts.ContextSupport
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ObserverRegisterSpecification
  extends Specification
  with Mockito {

  trait ObserverRegisterScope
    extends Scope {

    lazy implicit val contextSupport = mock[ContextSupport]

    lazy val contextResolver = mock[ContentResolver]

    lazy val uriCreator = mock[UriCreator]

    lazy val mockUri = mock[Uri]

    lazy val observerRegister = new ObserverRegister(uriCreator)

  }

}


class ObserverRegisterSpec
  extends ObserverRegisterSpecification {

  "ObserverRegister" should {

    "call to register observer with the right params" in new ObserverRegisterScope {

      uriCreator.parse(any) returns mockUri
      contextSupport.getContentResolver returns contextResolver

      observerRegister.registerObserver

      there was one(contextResolver).registerContentObserver(any[Uri], any[Boolean], any[ContentObserver])

    }

    "call to unregister observer with the right params" in new ObserverRegisterScope {

      uriCreator.parse(any) returns mockUri
      contextSupport.getContentResolver returns contextResolver

      observerRegister.unregisterObserver

      there was one(contextResolver).unregisterContentObserver(any[ContentObserver])

    }

  }

}