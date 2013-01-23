import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "ARWars"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
    		"leodagdag" % "play2-morphia-plugin_2.9.1"  % "0.0.6",
    		"org.mongodb" % "mongo-java-driver" % "2.9.3", 
            "com.google.guava" % "guava" % "13.0.1", 
            "com.google.inject" % "guice" % "3.0", 
            "com.typesafe" % "play-plugins-guice" % "2.0.3", 
            "com.google.code.gson" % "gson" % "2.2.2", 
            "securesocial" % "securesocial_2.9.1" % "2.0.7", 
            "com.typesafe" %% "play-plugins-mailer" % "2.0.4", 
            "org.apache.commons" % "commons-math" % "2.2"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(   
    		resolvers += "LeoDagDag repository" at "http://leodagdag.github.com/repository/",
            resolvers += "Morphia repository" at "http://morphia.googlecode.com/svn/mavenrepo/", 
    		resolvers += "MongoDb Java Driver Repository" at "http://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/",
            resolvers += Resolver.url("SecureSocial Repository", url("http://securesocial.ws/repository/releases/"))(Resolver.ivyStylePatterns), 
            checksums := Nil
    )

}
