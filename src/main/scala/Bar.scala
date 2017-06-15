import javafx.scene.input.KeyCode
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.layout.Region
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

object Bar {

  private val label = new Label {
    text = "Dependencies"
    font = new Font("Calibri", 19)
    textFill = Color.web("#e6e6e6")
    padding  = Insets(0, 8, 0, 4)
    margin   = Insets(0, 0, 2, 0)
  }

  private val textField = new TextField {
    promptText = "Path to files directory"
    minWidth   = 200
  }
  textField.onKeyPressed = k => k.getCode match {
    case KeyCode.ENTER => button.fire
    case _ => None
  }

  private val tog = new ToggleGroup()
  tog.selectedToggle.onChange((_, _, t) => {
    val ext = t.asInstanceOf[javafx.scene.control.RadioButton].getText
    textField.promptText = s"Path to $ext files directory"
  })

  private def radioButton
  (txt: String, select: Boolean = false): RadioButton = {
    new RadioButton {
      text = txt
      toggleGroup = tog
      selected = select
      padding = Insets(0, 0, 0, 6)
    }
  }

  val processingLabel = new Label {
    text = "Processing"
    font = new Font("Calibri", 14)
    textFill = Color.web("#2BB2DD")
    padding  = Insets(0, 8, 0, 8)
    margin   = Insets(0, 0, 2, 0)
    visible  = false
  }

  val progressBar = new ProgressBar {
    prefWidth = 170
    padding = Insets(0, 4, 0, 4)
    visible = false
  }

  private val button = new Button {
    text = "Let's go"
    minWidth = 40
    onAction = _ => {
      progressBar.visible     = true
      processingLabel.visible = true

      val future = Future {
        val path: String = textField.text.value
        val extension: String = tog.selectedToggle.value
          .asInstanceOf[javafx.scene.control.RadioButton].getText
        Frame.update(path, extension)
      }

      future onComplete {
        case _ =>
          progressBar.visible     = false
          processingLabel.visible = false
      }
    }
  }

  val toolBar = new ToolBar {
    focusTraversable = true
    style = "-fx-base: #333333"
    prefHeight = 30
    content = List(
      label,
      region(112),
      textField,
      radioButton(".class", true),
      radioButton(".java"),
      region(72),
      processingLabel,
      progressBar,
      region(6),
      button
    )
  }

  private def region(wid: Double): Region = {
    new Region {
      prefWidth = wid
    }
  }

}
