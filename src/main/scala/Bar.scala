import scalafx.geometry.Pos
import scalafx.scene.control._
import scalafx.scene.layout.Region
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

object Bar {
  def get = toolBar

  private val label = new Label {
    text = "Dependencies"
    font = new Font("Helvetica", 18)
    textFill = Color.web("#e6e6e6")
    alignment = Pos.Center
  }

  private val textField = new TextField {
    promptText = "Path to files directory"
    minWidth = 200
  }

  private val tog = new ToggleGroup()
  tog.selectedToggle.onChange((_, _, t) => {
    val ext = t.asInstanceOf[javafx.scene.control.RadioButton].getText
    textField.promptText = s"Path to ${ext} files directory"
    button.disable = false
  })

  private val button = new Button {
    disable = true
    minWidth = 40
    text = "Let's go"
    onAction = _ => {
      Frame.list =
        tog.selectedToggle.value
          .asInstanceOf[javafx.scene.control.RadioButton].getText match {
          case ".class" => Dependencies.fromClassFiles(textField.text.value)
          case ".java"  => Dependencies.fromJavaFiles(textField.text.value)
        }
    }
  }

  private val toolBar = new ToolBar {
    focusTraversable = true
    style = "-fx-base: #333333"
    prefHeight = 30
    content = List(
      label, region(20),
      textField, region(10),
      new RadioButton {
        selected = true
        text = ".class"
        toggleGroup = tog
      },
      new RadioButton {
        text = ".java"
        toggleGroup = tog
      },
      region(435),
      button
    )
  }

  private def region(wid: Double): Region = {
    new Region {
      prefWidth = wid
    }
  }
}
