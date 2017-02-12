name := "dependencies"

version := "1.0"

scalaVersion := "2.12.1"

resolvers ++= Seq(
  "Maven Central Server"          at "http://repo1.maven.org/maven2",
  "TypeSafe Repository Releases"  at "http://repo.typesafe.com/typesafe/releases/",
  "TypeSafe Repository Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Sonatype"                      at "https://oss.sonatype.org/content/groups/public",
  "Twitter"                       at "http://maven.twttr.com"
)

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "org.ow2.asm"    % "asm-all"       % "5.0.3",
  "org.scala-lang" % "scala-reflect" % "2.12.1",
  "org.scala-lang" % "scala-library" % "2.12.1",
  "org.scalafx"    % "scalafx_2.11"  % "8.0.102-R11"
)
