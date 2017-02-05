object members {

  trait Printable {
    override def toString: String = this match {
      case DClass(n, sn, infs, flds, mthds) =>
        s"Class: $n" + (if(sn != "java/lang/Object") s" extends $sn" else "") +
          (if (!infs.isEmpty) s" implements ${infs.mkString(", ")}" else "") +
          (if (!flds.isEmpty)  s"\nFields: ${flds.mkString("\n")}" else "") +
          (if (!mthds.isEmpty) s"\nMethods: ${mthds.mkString("\n")}" else "")

      case DField(owner, name, desc) => s"$owner.$name $desc"

      case DMethod(owner, name, desc, refs) => s"$owner.$name $desc" +
        (if (!refs.isEmpty) "\n   " + refs.mkString("\n   ") else "")

      case DRef(flag, reference) => s"$flag $reference"

      case wildcard => wildcard.toString
    }
  }

  case class DClass(name: String, superName: String, interfaces: List[String],
                    fields: List[DField], methods: List[DMethod]) extends Printable

  case class DField(owner: String, name: String, desc: String) extends Printable

  case class DMethod(owner: String, name: String, desc: String, refs: List[DRef]) extends Printable

  case class DRef(flag: String, reference: String) extends Printable

}