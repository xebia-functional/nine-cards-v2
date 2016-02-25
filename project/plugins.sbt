addSbtPlugin("org.brianmckenna" % "sbt-wartremover" % "0.13")

addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.5.12")

resolvers += Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(Resolver.ivyStylePatterns)

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.0")

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-s3" % "1.10.44",
  "org.apache.httpcomponents" % "httpclient" % "4.5.1",
  "org.apache.httpcomponents" % "httpmime" % "4.5.1",
  "crashlytics-devtools" % "crashlytics-devtools" % "1.0"
    from s"file://${(baseDirectory.value / "modules/app/crashlytics/crashlytics-devtools.jar").getAbsolutePath}"
)