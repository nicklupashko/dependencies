import scala.concurrent.Future
import scala.concurrent
.ExecutionContext.Implicits.global
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.layout.Region
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

object Bar {
  def get: ToolBar = toolBar

  private val label = new Label {
    text = "Dependencies"
    font = new Font("Helvetica", 18)
    textFill = Color.web("#e6e6e6")
    padding  = Insets(0, 8, 0, 4)
    margin   = Insets(0, 0, 2, 0)
  }

  private val textField = new TextField {
    promptText = "Path to files directory"
    minWidth   = 200
  }

  private val tog = new ToggleGroup()
  tog.selectedToggle.onChange((_, _, t) => {
    val ext = t.asInstanceOf[javafx.scene.control.RadioButton].getText
    textField.promptText = s"Path to ${ext} files directory"
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

  private val processingLabel = new Label {
    text = "Processing"
    font = new Font("Helvetica", 12)
    textFill = Color.web("#2BB2DD")
    padding  = Insets(0, 8, 0, 8)
    margin   = Insets(0, 0, 2, 0)
    visible  = false
  }

  private val progressBar = new ProgressBar {
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

      future.onComplete {
        case _ =>
          progressBar.visible     = false
          processingLabel.visible = false
      }
    }
  }

  private val toolBar = new ToolBar {
    focusTraversable = true
    style = "-fx-base: #333333"
    prefHeight = 30
    content = List(
      label,
      textField,
      radioButton(".class", true),
      radioButton(".java"),
      region(200),
      processingLabel,
      progressBar,
      region(8),
      button
    )
  }

  private def region(wid: Double): Region = {
    new Region {
      prefWidth = wid
    }
  }

}
