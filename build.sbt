name := "dependencies"

version := "1.0"

scalaVersion := "2.12.1"

resolvers ++= Seq(
  "Twitter"                       at "http://maven.twttr.com",
  "Maven Central Server"          at "http://repo1.maven.org/maven2",
  "TypeSafe Repository Releases"  at "http://repo.typesafe.com/typesafe/releases/",
  "TypeSafe Repository Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Sonatype"                      at "https://oss.sonatype.org/content/groups/public"
)

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "net.sf.jung"    %  "jung-api"           % "2.1.1",
  "net.sf.jung"    %  "jung-graph-impl"    % "2.1.1",
  "net.sf.jung"    %  "jung-algorithms"    % "2.1.1",
  "net.sf.jung"    %  "jung-io"            % "2.1.1",
  "net.sf.jung"    %  "jung-visualization" % "2.1.1",
  "net.sf.jung"    %  "jung-samples"       % "2.1.1",
  "org.ow2.asm"    %  "asm-all"            % "5.0.3",
  "org.scala-lang" %  "scala-reflect"      % "2.12.1",
  "org.scala-lang" %  "scala-library"      % "2.12.1",
  "org.scalafx"    %% "scalafx"            % "8.0.102-R11"
)