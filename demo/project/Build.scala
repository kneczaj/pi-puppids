import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "demo for WebApp Lab Course"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
    		// Add your project dependencies here,
    		"leodagdag" % "play2-morphia-plugin_2.9.1"  % "0.0.6",
    		"org.mongodb" % "mongo-java-driver" % "2.9.3"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
    		// Add your own project settings here    
    		resolvers += "LeoDagDag repository" at "http://leodagdag.github.com/repository/",
            resolvers += "Morphia repository" at "http://morphia.googlecode.com/svn/mavenrepo/", 
    		resolvers += "MongoDb Java Driver Repository" at "http://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/",
            checksums := Nil
    )

}
