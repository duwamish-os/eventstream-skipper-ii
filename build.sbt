name := "eventstream-skipper"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies += "com.google.cloud" % "google-cloud-pubsub" % "0.38.0-beta"

libraryDependencies += "com.google.cloud" % "google-cloud-logging-logback" % "0.40.0-alpha"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
