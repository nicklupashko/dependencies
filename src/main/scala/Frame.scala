import scalafx.application.JFXApp
import scalafx.scene._
import scalafx.stage.StageStyle
import scalafx.scene.layout.BorderPane
import member._;

object Frame extends JFXApp {
  private var list: List[DClass] = List.empty[DClass]

  def updateList(path: String, extension: String): Unit = {
    list = extension match {
      case ".class" => Dependencies.fromClassFiles(path)
      case ".java"  => Dependencies.fromJavaFiles(path)
    }
  }

  stage = new JFXApp.PrimaryStage {
    title = "Dependencies"
    initStyle(StageStyle.Unified)
    resizable = false
    width  = 980
    height = 680
    scene = new Scene {
      root = new BorderPane {
        top = Bar.get
      }
    }

  }

}
