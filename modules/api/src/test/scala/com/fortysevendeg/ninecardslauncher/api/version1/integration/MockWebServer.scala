package com.fortysevendeg.ninecardslauncher.api.version1.integration

import org.mockserver.integration.ClientAndServer._
import org.mockserver.logging.Logging
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest._
import org.mockserver.model.HttpResponse._
import org.specs2.specification.BeforeAfterEach

trait MockServerService
    extends BeforeAfterEach {

  val userConfigIdFirst = "12345678"
  val packageName1 = "com.fortysevendeg.scala.android"
  val packageName2 = "com.fortysevendeg.android.scaladays"

  val sharedCollectionIdFirst = "12345678"
  val sharedCollectionIdLast = "87654321"
  val sharedCollectionSize = 2
  val sharedCollectionType = "collectionType"
  val sharedCollectionCategory = "collectionCategory"

  val userConfigPathPrefix = "/ninecards/userconfig"
  val sharedCollectionPathPrefix = "/ninecards/collections"
  val googlePlayPathPrefix = "/googleplay/package"
  val googlePlayPackagesPathPrefix = "/googleplay/packages/detailed"
  val recommendationsPathPrefix = "/collections"
  val regexpPath = "[a-zA-Z0-9,\\.\\/]*"
  val jsonHeader = new Header("Content-Type", "application/json; charset=utf-8")
  val userConfigJson = "userConfig.json"
  val sharedCollectionJsonSingle = "sharedCollection.json"
  val sharedCollectionJsonList = "sharedCollectionList.json"
  val googlePlayPackageJsonSingle = "googlePlayPackage.json"
  val googlePlayPackageJsonList = "googlePlayPackageList.json"
  // TODO
  val recommendedCollectionsJson = ""
  val recommendedCollectionAppsJson = ""
  val recommendedAppsJson = "recommendationApps.json"

  lazy val mockServer = startClientAndServer(9999)

  def beforeAll = {
    Logging.overrideLogLevel("ERROR")
    mockServer
  }

  def loadJson(file: String): String =
    scala.io.Source.fromInputStream(getClass.getResourceAsStream(s"/$file"), "UTF-8").mkString

  def afterAll = {
    mockServer.stop()
  }

}

trait UserConfigServer {

  self: MockServerService =>

  Logging.overrideLogLevel("ERROR")

  mockServer.when(
    request()
        .withMethod("GET")
        .withPath(s"$userConfigPathPrefix"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(userConfigJson)))

}

trait SharedCollectionsServer {

  self: MockServerService =>

  Logging.overrideLogLevel("ERROR")

  mockServer.when(
    request()
        .withMethod("GET")
        .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionType/$regexpPath"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(sharedCollectionJsonList)))

  mockServer.when(
    request()
        .withMethod("POST")
        .withPath(sharedCollectionPathPrefix))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(sharedCollectionJsonSingle)))
}

trait GooglePlayServer {

  self: MockServerService =>

  Logging.overrideLogLevel("ERROR")

  mockServer.when(
    request()
        .withMethod("GET")
        .withPath(s"$googlePlayPathPrefix/$packageName1"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(googlePlayPackageJsonSingle)))

  mockServer.when(
    request()
        .withMethod("POST")
        .withPath(googlePlayPackagesPathPrefix))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(googlePlayPackageJsonList)))

}

trait RecommendationsServer {

  self: MockServerService =>

  Logging.overrideLogLevel("ERROR")

  mockServer.when(
    request()
        .withMethod("POST")
        .withPath(s"$recommendationsPathPrefix/recommendations/apps"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(recommendedAppsJson)))

}
