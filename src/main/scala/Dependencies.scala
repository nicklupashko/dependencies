import members._
import scala.sys.process._
import scala.reflect.io.{Directory, File}

object Dependencies {

  def fromJavaFiles(path: String): Unit = {
    val temp = Directory.makeTemp(prefix = "dpndncy")
    val command = Seq("javac", "*.java", "-g:vars", "-d", temp.path)
    val dir = Directory(path)
    Process(command, dir.jfile).!
    fromClassFiles(temp.path)
    temp.deleteRecursively
  }

  def fromClassFiles(path: String): Unit =
    dclassList(filesList(path)).foreach(println)

  def filesList(path: String): List[File] =
    Directory(path).deepFiles.toList

  def dclassList(files: List[File]): List[DClass] =
    files.filter(_.name.endsWith(".class")).map(Parser.classFileToDClass)

  def main(args: Array[String]): Unit = {
//    Dependencies.fromJavaFiles("d:/path/to/project/src/")
//    Dependencies.fromClassFiles("d:/path/to/project/out/")
  }
}