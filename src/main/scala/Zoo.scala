import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.effect.{GaussianBlur, MotionBlur}
import scalafx.scene.layout.{GridPane, HBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

object Zoo extends JFXApp {

  val fontSize = 120

  val initialFont = "Georgia"
  val secondFont  = "Palatino"

  def label(text: String, color: String = "#001"): Label = {
    val label = new Label(text)
    label.setFont(new Font(initialFont, fontSize))
    label.setTextFill(Color.web(color))
    label
  }

  val label1 = label("Z")
  val label2 = label("O")
  val label3 = label("O")

  val hbox = new HBox(label1, label2, label3)

  val pane = new GridPane()
  pane.alignment = Pos.Center
  pane.add(hbox, 0, 0)

  stage = new JFXApp.PrimaryStage {
    title = "Zoo"
    width = 1200
    height = 800
    scene = new Scene {
      root = pane
    }
  }

  // === Animation ===
  var startTime  = 0L
  val middleTime = 6
  val endTime    = 9


  val timer = AnimationTimer(time => {
    val currentTime = (System.currentTimeMillis() - startTime) / 1000.0

    if (currentTime < middleTime) {
      hbox.translateX = Math.cos(currentTime * 1000) * Math.pow(currentTime, 2.5)

      // TODO
//      hbox.effect = new MotionBlur(0, currentTime * 10)
    } else if (currentTime < endTime) {

      List(label1, label2, label3) foreach {
        _.setFont(new Font(secondFont, fontSize))
      }

      label1.setTextFill(Color.web("#d92027"))
      label2.setTextFill(Color.web("#ffcd3c"))
      label3.setTextFill(Color.web("#35d0ba"))

      hbox.translateX = Math.cos(time) * Math.sqrt(currentTime)

      // TODO
//      hbox.effect = new MotionBlur(0, (endTime - currentTime) * 25)
    } else {

    }
  })

  hbox.onMouseClicked = _ => {
    startTime = System.currentTimeMillis()
    timer.start()
  }

}
