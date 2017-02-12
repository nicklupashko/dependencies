import members._
import org.objectweb.asm._
import org.objectweb.asm.tree._
import org.objectweb.asm.Opcodes._
import scala.collection.JavaConverters._
import scala.reflect.io.File

object Parser {

  def classFileToDClass(file: File): DClass = {
    val reader = new ClassReader(file.toByteArray)
    val node = new ClassNode()
    reader.accept(node, 0)
    classNodeToDClass(node)
  }

  private type AL[T] = java.util.ArrayList[T]

  private val primitives: Set[String] = Set("B", "C", "D", "F", "J", "I", "S", "Z")

  private def classNodeToDClass(node: ClassNode): DClass = {
    val interfaces: List[String] =
      node.interfaces.asInstanceOf[AL[String]].asScala.toList
    val fields: List[DField] =
      node.fields.asInstanceOf[AL[FieldNode]].asScala.toList
        .map(fld => DField(node.name, fld.name, fld.desc))
    val methods: List[DMethod] =
      node.methods.asInstanceOf[AL[MethodNode]].asScala.toList
        .filterNot(_.name.matches("<[a-z]+>"))
        .map(mtd => methodNodeToDMethod(node.name, mtd))

    DClass(node.name, node.superName, interfaces, fields, methods)
  }

  private def methodNodeToDMethod(owner: String, node: MethodNode): DMethod = {
    val refs = scala.collection.mutable.Set.empty[DRef]
    node.instructions.toArray.foreach(ain => ain.getOpcode match {
      case GETSTATIC | PUTSTATIC | GETFIELD | PUTFIELD =>
        val field = ain.asInstanceOf[FieldInsnNode]
        if (!field.owner.startsWith("java/"))
          refs += DRef("F", s"${field.owner}.${field.name}: ${field.desc}")

      case INVOKEVIRTUAL | INVOKESTATIC =>
        val method = ain.asInstanceOf[MethodInsnNode]
        if (!method.owner.startsWith("java/"))
          refs += DRef("M", s"${method.owner}.${method.name} ${method.desc}")

      case _ => None
    })

    val localVars =
      Option(node.localVariables.asInstanceOf[AL[LocalVariableNode]]) match {
        case Some(locals) => locals.asScala
          .filterNot(lv => lv.name.matches("this") || primitives.contains(lv.desc))
          .map(lv => DRef("LV", s"${lv.name}: ${lv.desc}"))
        case None => Nil
      }

    DMethod(owner, node.name, node.desc, refs.toList.sortBy(_.flag) ++ localVars)
  }

}