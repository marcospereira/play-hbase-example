name := """play-hbase-example"""
organization := "com.lightbend.play"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test

libraryDependencies += "org.apache.hbase" % "hbase-client" % "1.3.1"
libraryDependencies += "org.apache.hbase" % "hbase-common" % "1.3.1"
libraryDependencies += "org.apache.hbase" % "hbase" % "1.3.1"
libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.5.1"