name := "untitled11"
 
version := "1.0" 
      
lazy val `untitled11` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"

resolvers += Resolver.jcenterRepo
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  ehcache , ws , specs2 % Test , guice,
  "com.iheart" %% "ficus" % "1.4.7",
  "com.typesafe.play" %% "play-slick" % "4.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.0",
  "org.xerial" % "sqlite-jdbc" % "3.30.1",
  "com.mohiva" %% "play-silhouette" % "7.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "7.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "7.0.0",
  "com.mohiva" %% "play-silhouette-testkit" % "7.0.0" % "test",
  "com.mohiva" %% "play-silhouette-cas" % "7.0.0",
  "com.mohiva" %% "play-silhouette-totp" % "7.0.0",
  "net.codingwell" %% "scala-guice" % "4.1.0",
  cacheApi
)

libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.30.1"
libraryDependencies += "com.github.3tty0n" %% "jwt-scala" % "1.3.0"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )