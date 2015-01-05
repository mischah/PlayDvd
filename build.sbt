name := """dvdDataBase"""

version := "1.8-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"


libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.18",
  "net.coobird" % "thumbnailator" % "0.4.4",
  "commons-io" % "commons-io" % "2.0.1",
  "commons-collections" % "commons-collections" % "3.2.1",
  "commons-lang" % "commons-lang" % "2.6",
  "com.google.code.gson" % "gson" % "2.2.4",
  "com.timgroup" % "jgravatar" % "1.0",
  // RSS FEEDS
  "rome" % "rome" % "1.0",
  // grabbers
  "com.omertron" % "thetvdbapi" % "1.6",
  "com.omertron" % "themoviedbapi" % "3.10",
  "com.omertron" % "traileraddictapi" % "1.4",
  "com.google.zxing" % "core" % "3.1.0",
  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.1",
  // webjars
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars" % "bootstrap" % "2.3.2",
  "org.webjars" % "jquery" % "1.11.2",
  "org.webjars" % "jquerypp" % "1.0b2",
  "org.webjars" % "select2" % "3.5.2",
  "org.webjars" % "font-awesome" % "3.2.1",
  "org.webjars" % "famfamfam-flags" % "0.0",
  "org.webjars" % "holderjs" % "2.4.0",
  "org.webjars" % "hammerjs" % "2.0.3-1",
  "org.webjars" % "animate.css" % "3.2.0",
  "org.julienrf" %% "play-jsmessages" % "1.6.2",
  "com.github.tuxBurner" %% "play-jsannotations" % "1.2.2",
  "com.github.tuxBurner" %% "play-twbs3" % "1.0",
  "com.amazonaws" % "aws-java-sdk" % "1.6.10"
)

resolvers ++= Seq(
  "tuxburner.github.io" at "http://tuxburner.github.io/repo",
  "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
  "Neo4j" at "http://m2.neo4j.org/content/repositories/releases/",
  Resolver.sonatypeRepo("snapshots")
)

