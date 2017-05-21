import scalafx.application.{JFXApp, Platform}
import scalafx.scene._
import scalafx.stage.StageStyle
import scalafx.scene.layout.BorderPane
import scalafx.scene.control.{TreeItem, TreeView}
import member._

object Frame extends JFXApp {

  private val tree = new TreeView[String](new TreeItem[String](""))

  def update(path: String, extension: String): Unit = {
    val list = extension match {
      case ".class" => Dependencies.fromClassFiles(path)
      case ".java"  => Dependencies.fromJavaFiles(path)
    }
    val name = path.replaceAll(".+/(.+)", "$1")
    Platform.runLater(updateTreeView(name, list))
  }

  def updateTreeView(name: String, list: List[DClass]): Unit = {
    val root = new TreeItem[String](name)
    root.children = list.map(n => {
      val item = new TreeItem[String](n.name)
      item.children =
        n.fields.map(f => new TreeItem[String](f.name)) ++
          n.methods.map(m => new TreeItem[String](m.name))
      item
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
      root = new BorderPane {
        top = Bar.get
        center = tree
      }
    }
  }

}
