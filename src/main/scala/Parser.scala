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
    val name = node.name.simplifyPath

    val superName = node.superName.simplifyPath

    val interfaces: List[String] =
      node.interfaces.toScalaList[String].map(_.simplifyPath)

    val fields: List[DField] = node.fields.toScalaList[FieldNode]
      .map(fld => DField(name, fld.name, fld.desc))

    val methods: List[DMethod] = node.methods.toScalaList[MethodNode]
      .map(mtd => methodNodeToDMethod(name, mtd))

    DClass(name, superName, interfaces, fields, methods)
  }

  private def methodNodeToDMethod(owner: String, node: MethodNode): DMethod = {
    val refs = scala.collection.mutable.LinkedHashSet.empty[DRef]

    node.instructions.toArray.foreach(ain => ain.getOpcode match {
      case GETSTATIC | PUTSTATIC | GETFIELD | PUTFIELD =>
        val fld = ain.asInstanceOf[FieldInsnNode]
        if (!fld.owner.startsWith("java/")) {
          val fldOwner = fld.owner.simplifyPath
          val fldDesc = fld.desc.removeL.swapBracketsAndDesc
            .simplifyPath.removeSemicolons.doubleBrackets
          refs += DRef("F", s"$fldOwner.${fld.name}: $fldDesc")
        }

      case INVOKEVIRTUAL | INVOKESTATIC =>
        val mtd = ain.asInstanceOf[MethodInsnNode]
        if (!mtd.owner.startsWith("java/")) {
          val mtdOwner = mtd.owner.simplifyPath
          val mtdParams = mtd.desc.parametersX
          val mtdRetType = mtd.desc.returnTypeX
          val methodX = s"${mtd.name}($mtdParams): $mtdRetType"
            .removeSemicolons.doubleBrackets
          refs += DRef("M", s"$mtdOwner.$methodX")
        }

      case _ => None
    })

    Option(node.localVariables.toArrayList[LocalVariableNode]).map(_.asScala
      .filterNot(v => v.name.matches("this") || primitives.contains(v.desc.replaceAll("\\[", "")))
      .foreach(v => refs += DRef("V", s"${v.name}: ${v.desc.removeL
        .swapBracketsAndDesc.simplifyPath.removeSemicolons.doubleBrackets}")))

    DMethod(owner, s"$owner.${node.name}(${node.desc.parametersX})".doubleBrackets,
      s": ${node.desc.returnTypeX}".removeSemicolons.doubleBrackets, refs.toList)
  }

  private val primitives: Set[String] = Set("B", "C", "D", "F", "J", "I", "S", "Z")

  private implicit class list2arrayList[T](list: java.util.List[T]) {
    def toArrayList[A] = list.asInstanceOf[java.util.ArrayList[A]]
    def toScalaList[A] = list.toArrayList[A].asScala.toList
  }

  private implicit class string2newView(str: String) {
    def removeL = str.replaceFirst("L", "")
    def simplifyPath = str.replaceFirst(".+/(.+)", "$1")
    def doubleBrackets = str.replaceAll("\\[", "[]")
    def removeSemicolons = str.replaceAll(";", "")
    def swapBracketsAndDesc = str.replaceAll("(\\[*)?(.+)", "$2$1")

    def parametersX = str.replaceFirst("\\((.+)?\\).+", "$1").split(";")
      .map(_.removeL.swapBracketsAndDesc.simplifyPath).mkString(", ")
    def returnTypeX = str.replaceFirst(".+\\)(\\[*)?(.+/)?(.+)", "$3$1")
  }

}