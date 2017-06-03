import scalafx.application.{JFXApp, Platform}
import scalafx.scene._
import scalafx.Includes._
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
    val root = new TreeItem[String](name)

    root.children = list.map(node => {
      val classItem = new TreeItem[String](node.name.replaceAll(".+/(.+)", "$1"))

      val fieldItems = new TreeItem[String]("Fields:")
      fieldItems.children = node.fields.map(fld => {
        new TreeItem[String](s"${fld.name}: ${fld.desc.replaceAll(".+/(.+);", "$1")}")
      })

      val methodItems = new TreeItem[String]("Methods:")
      methodItems.children = node.methods.map(mtd => {
        new TreeItem[String](s"${mtd.name}")
      })

      classItem.children = List(fieldItems, methodItems)
      classItem
    })

    tree.root = root
  }

  stage = new JFXApp.PrimaryStage {
    title = "Dependencies"
    initStyle(StageStyle.Unified)
    width = 980
    height = 680
    resizable = false
    scene = new Scene {
      stylesheets = List(
        getClass.getResource("style.css")
          .toExternalForm)
      root = new BorderPane {
        top = Bar.get
        left = tree
      }
    }
  }

}
