import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.effect.MotionBlur
import scalafx.scene.layout.{GridPane, HBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

object Zoo extends JFXApp {

  object Constants {
    val FIRST_FONT  = "New York"
    val SECOND_FONT = "Palatino"

    val COLOR_0     = "#001"
    val COLOR_1     = "#d92027"
    val COLOR_2     = "#ffcd3c"
    val COLOR_3     = "#35d0ba"
  }

  def createLabel(text: String): Label = {
    val label = new Label(text)
    setLabel(label, Constants.FIRST_FONT, Constants.COLOR_0)
    label
  }

  def setLabel(label: Label, font: String, color: String): Unit = {
    label.setFont(new Font(name = font, size = 120))
    label.setTextFill(Color.web(color))
  }

  // === Scene ===
  val label1 = createLabel("Z")
  val label2 = createLabel("O")
  val label3 = createLabel("O")

  val hbox = new HBox(label1, label2, label3)

  stage = new JFXApp.PrimaryStage {
    title = "Zoo"
    width = 1200
    height = 800
    scene = new Scene {
      root = new GridPane() {
        alignment = Pos.Center
        add(hbox, 0, 0)
      }
    }
  }

  // === Animation ===
  var startTime = 0L
  val midTime   = 6
  val endTime   = 9

  var SWITCH = false

  def switchLabels(): Unit = {
    if (SWITCH) {
      setLabel(label1, Constants.SECOND_FONT, Constants.COLOR_1)
      setLabel(label2, Constants.SECOND_FONT, Constants.COLOR_2)
      setLabel(label3, Constants.SECOND_FONT, Constants.COLOR_3)
    } else {
      setLabel(label1, Constants.FIRST_FONT, Constants.COLOR_0)
      setLabel(label2, Constants.FIRST_FONT, Constants.COLOR_0)
      setLabel(label3, Constants.FIRST_FONT, Constants.COLOR_0)
    }
  }

  val timer = AnimationTimer(_ => {
    val currentTime = (System.currentTimeMillis() - startTime) / 1000.0

    if (currentTime < midTime) {
      hbox.translateX = Math.cos(currentTime * 1000) * Math.pow(currentTime, 2.7)
      hbox.effect = new MotionBlur(0, if (currentTime < 2.5) 0 else (currentTime - 2) * 20)
    } else if (currentTime < endTime) {
      hbox.translateX = Math.cos(currentTime * 1000) * Math.pow(endTime - currentTime, 2.8)
      hbox.effect = new MotionBlur(0, (endTime - currentTime) * 20)
      switchLabels()
    }
  })

  hbox.onMouseClicked = _ => {
    startTime = System.currentTimeMillis()
    SWITCH = !SWITCH
    timer.start()
  }

}
