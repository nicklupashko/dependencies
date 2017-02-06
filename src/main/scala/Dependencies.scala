import scala.sys.process._
import scala.reflect.io.{Directory, File}

object Dependencies {

  def createFromJavaFiles(path: String): Unit = {
    val temp = Directory.makeTemp(prefix = "dpndncy")
    val command = Seq("javac", "*.java", "-g:vars", "-d", temp.path)
    val dir = File(path).jfile
    Process(command, dir).!
    createFromClassFiles(temp.path)
    temp.deleteRecursively
  }

  def createFromClassFiles(path: String): Unit = {
    Directory(path).deepFiles
      .filter(_.name.endsWith(".class"))
      .map(Parser.classFileToDClass)
      .foreach(println)
  }

  def main(args: Array[String]): Unit = {
//    Dependencies.createFromJavaFiles("d:/path/to/project/src/")
//    Dependencies.createFromClassFiles("d:/path/to/project/out/")
  }
}