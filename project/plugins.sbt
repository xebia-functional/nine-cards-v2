addSbtPlugin("org.brianmckenna" % "sbt-wartremover" % "0.13")

addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.5.12")

resolvers += Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(Resolver.ivyStylePatterns)

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.0")

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.amazonaws" % "aws-java-sdk-s3" % "1.10.44"

addSbtPlugin("de.johoop" % "ant4sbt" % "1.1.2")