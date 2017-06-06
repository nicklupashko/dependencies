object member {

  case class DClass(name: String, superName: String, interfaces: List[String],
                    fields: List[DField], methods: List[DMethod])

  case class DField(owner: String, name: String, desc: String)

  case class DMethod(owner: String, name: String, desc: String, refs: List[DRef])

  case class DRef(flag: String, reference: String)

}