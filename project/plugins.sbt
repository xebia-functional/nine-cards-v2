addSbtPlugin("org.brianmckenna" % "sbt-wartremover" % "0.13")

addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.5.12")

resolvers += Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(Resolver.ivyStylePatterns)

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.0")

resolvers += "Fabric public" at "https://maven.fabric.io/public"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-s3" % "1.10.44",
  "org.apache.httpcomponents" % "httpclient" % "4.5.1",
  "org.apache.httpcomponents" % "httpmime" % "4.5.1",
  "io.fabric.tools" % "gradle" % "1.21.4"
)