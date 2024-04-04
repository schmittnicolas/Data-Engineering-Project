val scala3Version = "3.4.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "data_engineering",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.apache.kafka" % "kafka-clients" % "3.2.0"
    )
  )
