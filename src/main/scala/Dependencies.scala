import member._
import scala.sys.process._
import scala.reflect.io.Directory

object Dependencies {

  def fromJavaFiles(sourcePath: String): List[DClass] = {
    val tempDir = Directory.makeTemp(prefix = "dpndncy")
    val command = Seq("javac", "*.java", "-g:vars", "-d", tempDir.path)
    Process(command, Directory(sourcePath).jfile).!
    val dcList = dclassListFrom(tempDir.path)
    tempDir.deleteRecursively
    dcList
  }

  def fromClassFiles(classPath: String): List[DClass] =
    dclassListFrom(classPath)

  private def dclassListFrom(classPath: String): List[DClass] =
    Directory(classPath).deepFiles.toList
      .filter(_.name.endsWith(".class"))
      .map(Parser.classFileToDClass)

  def main(args: Array[String]): Unit = {
    Dependencies.fromJavaFiles("c:/path/to/project/src/").foreach(println)
    Dependencies.fromClassFiles("c:/path/to/project/out/").foreach(println)
  }
}