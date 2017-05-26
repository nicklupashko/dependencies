import member._
import org.objectweb.asm._
import org.objectweb.asm.tree._
import org.objectweb.asm.Opcodes._
import scala.collection.JavaConverters._
import scala.reflect.io.File

object Parser {

  def classFileToDClass(file: File): DClass = {
    val node = new ClassNode()
    val reader = new ClassReader(file.toByteArray)
    reader.accept(node, 0)
    classNodeToDClass(node)
  }

  private def classNodeToDClass(node: ClassNode): DClass = {
    val interfaces: List[String] = node.interfaces.toScalaList[String]

    val fields: List[DField] = node.fields.toScalaList[FieldNode]
      .map(fld => DField(node.name, fld.name, fld.desc))

    val methods: List[DMethod] = node.methods.toScalaList[MethodNode]
      .filterNot(_.name.matches("<[a-z]+>"))
      .map(mtd => methodNodeToDMethod(node.name, mtd))

    DClass(node.name, node.superName, interfaces, fields, methods)
  }

  private def methodNodeToDMethod(owner: String, node: MethodNode): DMethod = {
    val refs = scala.collection.mutable.LinkedHashSet.empty[DRef]

    node.instructions.toArray.foreach(ain => ain.getOpcode match {
      case GETSTATIC | PUTSTATIC | GETFIELD | PUTFIELD =>
        val fld = ain.asInstanceOf[FieldInsnNode]
        if (!fld.owner.startsWith("java/"))
          refs += DRef("F", s"${fld.owner}.${fld.name}: ${fld.desc}")

      case INVOKEVIRTUAL | INVOKESTATIC =>
        val mtd = ain.asInstanceOf[MethodInsnNode]
        if (!mtd.owner.startsWith("java/"))
          refs += DRef("M", s"${mtd.owner}.${mtd.name} ${mtd.desc}")

      case _ => None
    })

    Option(node.localVariables.toArrayList[LocalVariableNode]).map(_.asScala
      .filterNot(v => v.name.matches("this") || primitives.contains(v.desc.replaceAll("\\[", "")))
      .foreach(v => refs += DRef("V", s"${v.name}: ${v.desc}")))

    DMethod(owner, node.name, node.desc, refs.toList)
  }

  private val primitives: Set[String] = Set("B", "C", "D", "F", "J", "I", "S", "Z")

  private implicit class List2ArrayList[T](list: java.util.List[T]) {
    def toArrayList[A] = list.asInstanceOf[java.util.ArrayList[A]]
    def toScalaList[A] = list.toArrayList[A].asScala.toList
  }

}