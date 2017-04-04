import scalafx.application.JFXApp
import scalafx.scene._
import scalafx.stage.StageStyle
import scalafx.scene.layout.{VBox}
import member._;

object Frame extends JFXApp {
  private var list: List[DClass] = List.empty[DClass]

  def updateList(newList: List[DClass]): Unit =
    this.list = newList

  stage = new JFXApp.PrimaryStage {
    title = "Dependencies"
    initStyle(StageStyle.Unified)
    resizable = false
    width = 980
    height = 680
    scene = new Scene {
      root = new VBox {
        spacing = 5
        children = List(
          Bar.get
        )
      }
    }

  }

}
