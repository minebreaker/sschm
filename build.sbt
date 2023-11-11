lazy val root = (project in file("."))
  .settings(
    name := "sschm",
    organization := "rip.deadcode",
    scalaVersion := "3.3.1",
    version := "0.1.0-SNAPSHOT",
    scalacOptions := Seq(
      "-explain"
    ),
    libraryDependencies ++= Seq(
      // Core
      "org.eclipse.jetty" % "jetty-server" % "11.0.14",
//      "org.apache.commons" % "commons-fileupload2" % "2.0.0-M1", // somehow sbt can't resolve dependencies correctly (maybe it's commons' documentation issue?)
      "org.apache.commons" % "commons-fileupload2-core" % "2.0.0-M1",
      "org.apache.commons" % "commons-fileupload2-jakarta" % "2.0.0-M1",
      "com.github.jknack" % "handlebars" % "4.3.1",
//      "org.apache.velocity" % "velocity-engine-core" % "2.3",
      "com.github.jknack" % "handlebars" % "4.3.1",
      "com.google.guava" % "guava" % "32.1.3-jre",
      "com.typesafe" % "config" % "1.4.2",
      "ch.qos.logback" % "logback-classic" % "1.4.7",
      // Database
      "org.jdbi" % "jdbi3-core" % "3.41.3",
      "org.postgresql" % "postgresql" % "42.6.0",
      "com.zaxxer" % "HikariCP" % "5.0.1",
      "org.flywaydb" % "flyway-core" % "9.22.3",
      // Scala things
      "org.typelevel" %% "cats-effect" % "3.5.2",
      "io.scalaland" %% "chimney" % "0.8.0"
    ) ++ Seq(
      "org.scalatest" %% "scalatest-funspec" % "3.2.15",
      "org.scalatestplus" %% "mockito-4-11" % "3.2.17.0"
    ).map(_ % "test"),
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest),
    Test / fork := true,
    Test / testForkedParallel := true
//    dockerBaseImage :=
//    packageName := "",
  )
