object members {

  trait Printable {
    override def toString: String = this match {
      case DClass(n, sn, infs, flds, mtds) => s"Class: $n" +
        (if (sn != "java/lang/Object") s" extends $sn" else "") +
        (if (infs.nonEmpty) s" implements ${infs.mkString(", ")}" else "") +
        (if (flds.nonEmpty) s"\nFields: \n${flds.mkString("\n")}" else "") +
        (if (mtds.nonEmpty) s"\nMethods: \n${mtds.mkString("\n")}" else "") +
        "\n" + "#" * 21

      case DField(owner, name, desc) => s"$owner.$name: $desc"

      case DMethod(owner, name, desc, refs) => s"$name $desc" +
        (if (refs.nonEmpty) refs.mkString("\n   ", "\n   ", "") else "")

      case DRef(flag, reference) => s"$flag $reference"

    }
  }

  case class DClass(name: String, superName: String, interfaces: List[String],
                    fields: List[DField], methods: List[DMethod]) extends Printable

  case class DField(owner: String, name: String, desc: String) extends Printable

  case class DMethod(owner: String, name: String, desc: String, refs: List[DRef]) extends Printable

  case class DRef(flag: String, reference: String) extends Printable

}