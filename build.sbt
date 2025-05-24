name := "ScalaGraphQLDemo"

version := "0.1"

scalaVersion := "2.13.14"

val http4sVersion = "0.23.14"

libraryDependencies ++= Seq(
  // Sangria and Circe
  "org.sangria-graphql" %% "sangria" % "2.1.6",
  "org.sangria-graphql" %% "sangria-circe" % "1.3.1",
  "io.circe" %% "circe-generic" % "0.13.0",
  "io.circe" %% "circe-parser" % "0.13.0",

  // HTTP4S
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion
)


