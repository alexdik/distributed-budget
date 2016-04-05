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

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.mockito" % "mockito-core" % "1.10.19" % "test",
  "com.jayway.awaitility" % "awaitility-scala" % "1.6.5" % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "io.spray" %% "spray-routing" % sprayVersion % "test",
  "com.github.docker-java" % "docker-java" % "3.0.0-RC2" % "test",

  "org.scala-lang" % "scala-library-all" % "2.11.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "com.aerospike" % "aerospike-client" % "latest.integration"
)

assemblyJarName in assembly := "budget.jar"
mainClass in assembly := Some("Launcher")
assemblyMergeStrategy in assembly := {
  case path@PathList("META-INF", xs@_*) if path.toString.contains("META-INF/services/java.sql.Driver") => MergeStrategy.first
  case path@PathList("META-INF", xs@_*) => MergeStrategy.discard
  case path@PathList("application.conf") => MergeStrategy.discard
  case x => MergeStrategy.first
}

parallelExecution in Test := false

logLevel := Level.Debug