import javafx.scene.text.Text
import javax.swing.SwingUtilities

import scalafx.application.{JFXApp, Platform}
import scalafx.scene._
import scalafx.stage.StageStyle
import scalafx.scene.layout.BorderPane
import scalafx.scene.control.{TreeItem, TreeView}
import member._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.io.Path
import scalafx.embed.swing.SwingNode

object Frame extends JFXApp {

  private var classList = List.empty[DClass]

  private val bar = Bar.toolBar

  private val graph = new SwingNode {
    translateX = 64
    translateY = 8
  }

  private val tree = new TreeView[String] {
    focusTraversable = false
    root = new TreeItem[String]("There's nothing to show")
  }
  tree.onMouseClicked = e => {
    val node = e.getPickResult().getIntersectedNode()
    if (e.getClickCount == 2 && node.isInstanceOf[Text]) {
      val nodeName = tree.getSelectionModel.getSelectedItem.getValue

      Bar.progressBar.visible = true
      Bar.processingLabel.visible = true

      println(s"*** $nodeName ***")
      val future = Future {
        SwingUtilities.invokeAndWait(() =>
          updateGraph(findAllOfIt(nodeName)))
      }

      future onComplete {
        case _ =>
          Bar.progressBar.visible = false
          Bar.processingLabel.visible = false
      }
    }
  }

  def findAllOfIt(name: String): List[String] = {
    var i = 0
    val allMethods = classList.flatMap(_.methods)

    def findMethod(methodName: String): Option[DMethod] =
      allMethods.find(m => (m.name + m.desc) == methodName)

    def isMethodExists(methodName: String): Boolean =
      findMethod(methodName).nonEmpty

    def methodX(methodName: String): List[String] =
      findMethod(methodName) match {
        case None => List.empty[String]
        case Some(m) =>
          val fields: List[String] = m.refs.filter(_.flag == "F")
            .map(f => s"$methodName^${f.flag} ${f.reference}")
          val vars: List[String] = m.refs.filter(_.flag == "V")
            .map(v => s"$methodName^${v.flag} (${i=i+1; i}) ${v.reference}")
          val methods: List[String] = m.refs.filter(_.flag == "M")
            .filter(m => isMethodExists(m.reference))
            .map(m => s"$methodName^${m.reference}")

          fields ++ vars ++ methods ++
            m.refs.filter(_.flag == "M").map(_.reference).flatMap(methodX)
      }
    methodX(name)
  }

  def update(path: String, extension: String): Unit = {
    classList = extension match {
      case ".class" => Dependencies.fromClassFiles(path)
      case ".java"  => Dependencies.fromJavaFiles(path)
    }
    Platform.runLater(updateTreeView(Path(path).name, classList))
  }

  def updateGraph(list: List[String]): Unit = {
    import scala.collection.JavaConverters._
    graph.content = Graph343.node(list.asJava)
  }

  def updateTreeView(name: String, list: List[DClass]): Unit = {
    tree.root = new TreeItem[String] {
      value = name
      children = list.map(clazz => {
        new TreeItem[String] {
          value = clazz.name
          children = clazz.methods.map(method =>
            new TreeItem[String](method.name + method.desc))
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
        top = bar
        left = tree
        center = graph
        style = "-fx-background-color: #eeeeee"
      }
    }
  }

}
