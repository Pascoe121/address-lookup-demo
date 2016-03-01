credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

val hmrcRepoHost = java.lang.System.getProperty("hmrc.repo.host", "https://nexus-preview.tax.service.gov.uk")

resolvers ++= Seq(Resolver.url("hmrc-sbt-plugin-releases", url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(Resolver.ivyStylePatterns))

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.3")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.8")

addSbtPlugin("uk.gov.hmrc" % "sbt-settings" % "3.0.0")

addSbtPlugin("uk.gov.hmrc" % "hmrc-resolvers" % "0.4.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-distributables" % "0.9.0")
