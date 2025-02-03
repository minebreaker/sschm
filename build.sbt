lazy val root = (project in file("."))
  .aggregate(server)

lazy val server = (project in file("server"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .enablePlugins(AshScriptPlugin)
  .settings(
    name := "sschm",
    organization := "rip.deadcode",
    scalaVersion := "3.6.3",
    version := "0.1.0-SNAPSHOT",
    scalacOptions := Seq(
      "-explain"
    ),
    libraryDependencies ++= Seq(
      // Core
      "org.eclipse.jetty" % "jetty-server" % "11.0.24",
//      "org.apache.commons" % "commons-fileupload2" % "2.0.0-M1", // somehow sbt can't resolve dependencies correctly (maybe it's commons' documentation issue?)
      "org.apache.commons" % "commons-fileupload2-core" % "2.0.0-M1",
      "org.apache.commons" % "commons-fileupload2-jakarta" % "2.0.0-M1",
      "com.google.guava" % "guava" % "33.4.0-jre",
      "com.typesafe" % "config" % "1.4.3",
      "ch.qos.logback" % "logback-classic" % "1.5.16",
      // Database
      "org.jdbi" % "jdbi3-core" % "3.47.0",
      "org.postgresql" % "postgresql" % "42.7.5",
      "com.zaxxer" % "HikariCP" % "6.2.1",
      "org.flywaydb" % "flyway-core" % "9.22.3",
      // Scala things
      "org.typelevel" %% "cats-effect" % "3.5.7",
      "io.scalaland" %% "chimney" % "1.7.1"
    ) ++ Seq(
      "org.scalatest" %% "scalatest-funspec" % "3.2.18",
      "org.scalatestplus" %% "mockito-4-11" % "3.2.18.0"
    ).map(_ % "test"),
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest),
    Test / fork := true,
    Test / testForkedParallel := true,

    // Docker
    dockerBaseImage := "amazoncorretto:23-alpine3.21",
    Docker / packageName := "sschm",
    Docker / dockerExposedPorts := Seq(8080)
  )
