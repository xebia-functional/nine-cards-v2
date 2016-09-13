package com.fortysevendeg.ninecardslauncher.services.intents.impl

import android.app.Activity
import android.content.Intent
import com.fortysevendeg.ninecardslauncher.commons.contexts.ActivityContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException
import com.fortysevendeg.ninecardslauncher.services.intents.{IntentLauncherServicesException, IntentLauncherServicesPermissionException}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._
import cats.syntax.either._


trait LauncherIntentServicesImplSpecification
  extends Specification
  with Mockito
  with LauncherIntentServicesImplData {

  trait LauncherIntentServicesImplScope
    extends Scope {

    val mockContextSupport = mock[ActivityContextSupport]

    val mockActivity = mock[Activity]

    val mockIntentCreator = mock[IntentCreator]

    val services = new LauncherIntentServicesImpl {
      override val intentCreator: IntentCreator = mockIntentCreator
    }

    val mockIntent = mock[Intent]

  }

  trait WithActivity {

    self: LauncherIntentServicesImplScope =>

    mockContextSupport.getActivity returns Some(mockActivity)

  }

}

class LauncherIntentServicesImplSpec
  extends LauncherIntentServicesImplSpecification {

  "launchIntentAction" should {

    "call to startActivity with the Intent created by IntentCreator for an AppAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createAppIntent(any, any) returns mockIntent
        val result = services.launchIntentAction(appAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createAppIntent(packageName, className)
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for an AppGooglePlayAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createAppGooglePlayIntent(any, any) returns mockIntent
        val result = services.launchIntentAction(appGooglePlayAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createAppGooglePlayIntent(googlePlayUrl, packageName)
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for an AppLauncherAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createAppLaunchIntent(any)(any) returns mockIntent
        val result = services.launchIntentAction(appLauncherAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createAppLaunchIntent(packageName)(mockContextSupport)
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for an AppSettingsAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createAppSettingsIntent(any) returns mockIntent
        val result = services.launchIntentAction(appSettingsAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createAppSettingsIntent(packageName)
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for an AppUninstallAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createAppUninstallIntent(any) returns mockIntent
        val result = services.launchIntentAction(appUninstallAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createAppUninstallIntent(packageName)
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for a ContactAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createContactIntent(any) returns mockIntent
        val result = services.launchIntentAction(contactAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createContactIntent(lookupKey)
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for a EmailAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createEmailIntent(any, any) returns mockIntent
        val result = services.launchIntentAction(emailAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createEmailIntent(email, titleDialog)
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for a GlobalSettingsAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createGlobalSettingsIntent() returns mockIntent
        val result = services.launchIntentAction(globalSettingsAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createGlobalSettingsIntent()
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for a GooglePlayStoreAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createGooglePlayStoreIntent()(any) returns mockIntent
        val result = services.launchIntentAction(googlePlayStoreAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createGooglePlayStoreIntent()(mockContextSupport)
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for a GoogleWeatherAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createGoogleWeatherIntent() returns mockIntent
        val result = services.launchIntentAction(googleWeatherAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createGoogleWeatherIntent()
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for a PhoneCallAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createPhoneCallIntent(any) returns mockIntent
        val result = services.launchIntentAction(phoneCallAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createPhoneCallIntent(phoneNumber)
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for a PhoneDialAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createPhoneDialIntent(any) returns mockIntent
        val result = services.launchIntentAction(phoneDialAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createPhoneDialIntent(phoneDialAction.maybePhoneNumber)
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for a PhoneSmsAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createPhoneSmsIntent(any) returns mockIntent
        val result = services.launchIntentAction(phoneSmsAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createPhoneSmsIntent(phoneNumber)
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for a SearchGlobalAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createSearchGlobalIntent() returns mockIntent
        val result = services.launchIntentAction(searchGlobalAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createSearchGlobalIntent()
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for a SearchVoiceAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createSearchVoiceIntent() returns mockIntent
        val result = services.launchIntentAction(searchVoiceAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createSearchVoiceIntent()
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for a SearchWebAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createSearchWebIntent() returns mockIntent
        val result = services.launchIntentAction(searchWebAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createSearchWebIntent()
        there was one(mockActivity).startActivity(mockIntent)
      }

    "call to startActivity with the Intent created by IntentCreator for a ShareAction" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockIntentCreator.createShareIntent(any, any) returns mockIntent
        val result = services.launchIntentAction(shareAction)(mockContextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockIntentCreator).createShareIntent(shareText, titleDialog)
        there was one(mockActivity).startActivity(mockIntent)
      }

  }

  "withActivity" should {

    "execute the function if the ActivityContextSupport.getActivity returns Some of Activity" in
      new LauncherIntentServicesImplScope with WithActivity {

        val xor = Right[NineCardException, Unit]((): Unit)
        val result = services.withActivity(_ => xor)(mockContextSupport)
        result should be(xor)

        there was one(mockContextSupport).getActivity
      }

    "return a Left[IntentLauncherServicesException] if the ActivityContextSupport.getActivity returns None" in
      new LauncherIntentServicesImplScope {

        mockContextSupport.getActivity returns None
        val xor = Right[NineCardException, Unit]((): Unit)
        val result = services.withActivity(_ => xor)(mockContextSupport)
        result should beAnInstanceOf[Left[IntentLauncherServicesException,_]]

        there was one(mockContextSupport).getActivity
      }

  }

  "launchIntent" should {

    "return a Left[IntentLauncherServicesException] if Activity.startActivity throws a RuntimeException" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockActivity.startActivity(any) throws runtimeException
        val result = services.launchIntent(mockIntent)(mockContextSupport).value.run
        result should beAnInstanceOf[Left[IntentLauncherServicesException, _]]

        there was one(mockContextSupport).getActivity
        there was one(mockActivity).startActivity(mockIntent)
      }

    "return a Left[IntentLauncherServicesPermissionException] if Activity.startActivity throws a SecurityException" in
      new LauncherIntentServicesImplScope with WithActivity {

        mockActivity.startActivity(any) throws securityException
        val result = services.launchIntent(mockIntent)(mockContextSupport).value.run
        result should beAnInstanceOf[Left[IntentLauncherServicesPermissionException, _]]

        there was one(mockContextSupport).getActivity
        there was one(mockActivity).startActivity(mockIntent)
      }

  }

}
