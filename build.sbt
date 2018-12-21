name := "fptothemax"

organization := "org.gwgs"

version := "0.1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect"  %  "1.1.0",
  "org.scalatest" %% "scalatest"     % "3.0.5"    % Test,
  "org.mockito"    % "mockito-core"  % "1.10.19"  % Test
)

publishArtifact in Test := false

parallelExecution in Test := false
