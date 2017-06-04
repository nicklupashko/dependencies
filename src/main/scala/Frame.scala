import scalafx.application.{JFXApp, Platform}
import scalafx.scene._
import scalafx.stage.StageStyle
import scalafx.scene.layout.BorderPane
import scalafx.scene.control.{TreeItem, TreeView}
import member._

import scala.reflect.io.Path

object Frame extends JFXApp {

  private val tree = new TreeView[String] {
    focusTraversable = false
    root = new TreeItem[String]("There's nothing to show")
  }

  def update(path: String, extension: String): Unit = {
    val list = extension match {
      case ".class" => Dependencies.fromClassFiles(path)
      case ".java"  => Dependencies.fromJavaFiles(path)
    }
    Platform.runLater(updateTreeView(Path(path).name, list))
  }

  def updateTreeView(name: String, list: List[DClass]): Unit = {
    tree.root = new TreeItem[String] {
      value = name
      children = list.map(node => {
        new TreeItem[String] {
          value = node.name.replaceFirst(".+/(.+)", "$1").replaceAll("\\$", ".")
          children = node.methods.map(mtd => {
            val params = mtd.desc.replaceFirst("\\((.+)?\\).+", "$1").split(";")
              .map(_.replaceFirst("(\\[*)?.+/(.+)", "$2$1")).mkString(", ")
            val retType = mtd.desc.replaceFirst(".+\\)(\\[*)?(.+/)?(.+)", "$3$1")
            new TreeItem[String](s"${mtd.name}($params): $retType"
              .replaceAll("\\$", ".").replaceAll(";", "").replaceAll("\\[", "[]"))
          })
        }
      })
    }
  }

  stage = new JFXApp.PrimaryStage {
    title = "Dependencies"
    initStyle(StageStyle.Unified)
    width = 980
    height = 680
    resizable = false
    scene = new Scene {
      root = new BorderPane {
        top = Bar.get
        left = tree
      }
    }
  }

}
