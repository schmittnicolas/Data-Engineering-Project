
// The simplest possible sbt build file is just one line:

scalaVersion := "2.13.12"

lazy val root = project
  .in(file("."))
  .settings(
    name := "data_engineering",
    version := "0.1.0-SNAPSHOT",
    fork := true,
    

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.apache.kafka" % "kafka-clients" % "3.4.0",
      "org.apache.kafka" %% "kafka-streams-scala" % "3.4.0",
      "org.apache.spark" %% "spark-sql" % "3.4.0",
      "com.typesafe" % "config" % "1.4.1",
      "org.apache.logging.log4j" % "log4j-core" % "2.14.1",
      "org.apache.logging.log4j" % "log4j-api" % "2.14.1",
      "org.apache.spark" %% "spark-core" % "3.4.0",
      "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.4.0",
      "org.apache.hadoop" % "hadoop-aws" % "3.3.4",
      "org.apache.hadoop" % "hadoop-common" % "3.3.4",
      "org.apache.hadoop" % "hadoop-client" % "3.3.4",
      "org.postgresql" % "postgresql" % "42.2.18"

  )
)
