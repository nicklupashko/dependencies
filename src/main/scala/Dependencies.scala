import members._
import scala.sys.process._
import scala.reflect.io.Directory

object Dependencies {

  def fromJavaFiles(srcDirPath: String): List[DClass] = {
    val temp = Directory.makeTemp(prefix = "dpndncy")
    val command = Seq("javac", "*.java", "-g:vars", "-d", temp.path)
    val dir = Directory(srcDirPath)
    Process(command, dir.jfile).!
    val dcList = dclassListFrom(temp.path)
    temp.deleteRecursively
    dcList
  }

  def fromClassFiles(classDirPath: String): List[DClass] = dclassListFrom(classDirPath)

  private def dclassListFrom(path: String): List[DClass] =
    Directory(path).deepFiles.toList
      .filter(_.name.endsWith(".class"))
      .map(Parser.classFileToDClass)

  def main(args: Array[String]): Unit = {
    Dependencies.fromJavaFiles("d:/path/to/project/src/")
    Dependencies.fromClassFiles("d:/path/to/project/out/")
  }
}