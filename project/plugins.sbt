addSbtPlugin("org.brianmckenna" % "sbt-wartremover" % "0.13")

addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.4.9")

resolvers += Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(Resolver.ivyStylePatterns)

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.0")

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-s3" % "0.8")