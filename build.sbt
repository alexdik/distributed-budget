scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "SnowPlow Repo" at "http://maven.snplow.com/releases/",
  "Twitter Maven Repo" at "http://maven.twttr.com/",
  "Local Maven Repository" at "file:///" + Path.userHome + "/.m2/repository"
)

val akkaVersion = "2.3.14"
val sprayVersion = "1.3.2"
val logbackVersion = "1.1.3"

unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "jmh"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.mockito" % "mockito-core" % "1.10.19" % "test",
  "com.jayway.awaitility" % "awaitility-scala" % "1.6.5" % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "io.spray" %% "spray-routing" % sprayVersion % "test",
  "com.github.docker-java" % "docker-java" % "3.0.0-RC2" % "test",

  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "com.typesafe.slick" %% "slick-codegen" % "3.0.0",
  "io.spray" %% "spray-client" % sprayVersion,
  "io.spray" %% "spray-can" % sprayVersion,
  "io.spray" %% "spray-json" % sprayVersion,
  "org.mongodb" % "bson" % "3.0.0",
  "org.scala-lang" % "scala-library-all" % "2.11.7",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "ch.qos.logback" % "logback-core" % logbackVersion,
  "com.snowplowanalytics" %% "scala-maxmind-iplookups" % "0.2.0",
  "com.aerospike" % "aerospike-client" % "latest.integration",
  "org.apache.flume" % "flume-ng-embedded-agent" % "1.6.0" exclude("org.apache.zookeeper", "zookeeper") exclude("org.slf4j", "slf4j-log4j12"),
  "org.apache.flume" % "flume-ng-sdk" % "1.6.0",
  "org.jdom" % "jdom2" % "2.0.6",
  "org.apache.httpcomponents" % "httpclient" % "4.3.1"
)

assemblyJarName in assembly := "budget.jar"
mainClass in assembly := Some("Launcher")
assemblyMergeStrategy in assembly := {
  case path@PathList("META-INF", xs@_*) if path.toString.contains("META-INF/services/java.sql.Driver") => MergeStrategy.first
  case path@PathList("META-INF", xs@_*) => MergeStrategy.discard
  case path@PathList("application.conf") => MergeStrategy.discard
  case x => MergeStrategy.first
}

enablePlugins(JmhPlugin)

parallelExecution in Test := false
