import scala.sys.process._
import scala.reflect.io.{Directory, File}

object Dependencies {

  def makeLegacyGreatAgain(projectPath: String) = {
    //    val temp = Directory.makeTemp(prefix = "dpndncy")
    val temp = Directory.Current.get
    println(temp)
    val command = Seq("javac", "*.java", "-g:vars", "-parameters", "-d", temp.path)
    val file = File(projectPath).jfile
    //    Process(command, file).!

    //    temp.deepFiles.filter(_.name.endsWith(".class"))
    //      .map(DParser.classFileToDClass).foreach(println)

    //   temp.deleteRecursively
  }

  def main(args: Array[String]): Unit = {
    Dependencies.makeLegacyGreatAgain("d:/work/idea/java/src/")
  }
}