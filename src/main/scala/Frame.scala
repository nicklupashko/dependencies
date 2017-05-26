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

    root.children = list.map(node => {
      val classItem = new TreeItem[String](node.name)

      val fieldItems = new TreeItem[String]("Fields:")
      fieldItems.children = node.fields.map(fld => {
        new TreeItem[String](s"${fld.name}: ${fld.desc.replaceAll(".+/(.+);", "$1")}")
      })

      val methodItems = new TreeItem[String]("Methods:")
      methodItems.children = node.methods.map(mtd => {
        val metItem = new TreeItem[String](s"${mtd.name}")
        metItem.children = mtd.refs.map(ref => {
          val refOpt = ref.flag match {
            case "F" => ref.reference.replaceAll(".+/(.+)", "$1")
            case "M" => ref.reference.replaceAll(".+/(.+) .+", "$1(args)")
            case "V" => ref.reference.replaceAll("(.+:) .+/(.+);", "$1 $2")
          }
          new TreeItem[String](s"${ref.flag} ${refOpt}")
        })

        metItem
      })

      classItem.children =
        List(fieldItems, methodItems)
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
      root = new BorderPane {
        top = Bar.get
        center = tree
      }
    }
  }

}