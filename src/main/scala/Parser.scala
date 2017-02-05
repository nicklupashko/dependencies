import org.objectweb.asm._
import org.objectweb.asm.tree._
import org.objectweb.asm.Opcodes._

import java.io.FileInputStream
import scala.reflect.io.File
import scala.collection.JavaConverters._

object Parser {
  import members._

  private type AList[T] = java.util.ArrayList[T]

  private val primitives: Set[String] = Set("B", "C", "D", "F", "J", "I", "S", "Z")

  def classFileToDClass(file: File): DClass = {
    val reader = new ClassReader(new FileInputStream(file.jfile))
    val node = new ClassNode()
    reader.accept(node, 0)
    parseClass(node)
  }

  private def parseClass(node: ClassNode): DClass = {
    implicit val owner: String = node.name

    val interfaces: List[String] =
      node.interfaces.asInstanceOf[AList[String]].asScala.toList
    val fields: List[DField] =
      node.fields.asInstanceOf[AList[FieldNode]].asScala.map(parseField).toList
    val methods: List[DMethod] =
      node.methods.asInstanceOf[AList[MethodNode]].asScala
        .filter(_.name.matches("<[a-z]+>")).map(parseMethod).toList

    DClass(node.name, node.superName, interfaces, fields, methods)
  }

  private def parseField(node: FieldNode)(implicit owner: String): DField =
    DField(owner, node.name, node.desc)

  private def parseMethod(node: MethodNode)(implicit owner: String): DMethod = {
    val localVars =
      Option(node.localVariables.asInstanceOf[AList[LocalVariableNode]].asScala) match {
        case Some(locals) => locals.map(lv => DRef("LV", s"${lv.name}: ${lv.desc}"))
        case None => Nil
      }

    val refs = scala.collection.mutable.Set[DRef]()
    node.instructions.toArray.foreach(ain => ain.getOpcode match {
      case GETSTATIC | PUTSTATIC | GETFIELD | PUTFIELD =>
        val field = ain.asInstanceOf[FieldInsnNode]
        if (!primitives.contains(field.desc) && field.name != "this")
          refs.add(DRef("F", s"${field.owner}.${field.name}: ${field.desc}"))

      case INVOKEVIRTUAL | INVOKESTATIC =>
        val method = ain.asInstanceOf[MethodInsnNode]
        if (!method.owner.startsWith("java/"))
          refs.add(DRef("M", s"${method.owner}.${method.name} ${method.desc}"))
    })

    DMethod(owner, node.name, node.desc, refs.toList.sortBy(_.flag) ++ localVars)
  }

}