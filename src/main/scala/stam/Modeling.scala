package stam

object Modeling extends App {

  sealed trait GueseColor
  object GueseColor {
    case object Red extends GueseColor
    case object Green extends GueseColor
    case object Blue extends GueseColor
    case class CustomColor (red: Int, green: Int, blue: Int) extends GueseColor
  }

  sealed trait MineColor
  object MineColor {
    abstract class InnerCustomColor private[MineColor](val red: Int, val green: Int, val blue: Int) extends MineColor
    case class Red() extends InnerCustomColor(red = 1, green = 0, blue = 0)
    case class Green() extends InnerCustomColor(red = 0, green = 1, blue = 0)
    case class Blue() extends InnerCustomColor(red = 0, green = 0, blue = 1)
    case class CustomColor (override val red: Int, override val green: Int, override val blue: Int) extends InnerCustomColor(red, green, blue)
  }

  sealed trait GueseAgeBracket
  object GueseAgeBracket {

  }
  sealed trait MineAgeBracket

  object MineAgeBracket {
    abstract class AgeRange private[MineAgeBracket](val from: Int, val toExclusive: Int) extends MineAgeBracket
    case class Baby() extends AgeRange(from = 0, toExclusive = 13)
    case class Child() extends AgeRange(from = 13, toExclusive = 18)
    case class YoungAdult() extends AgeRange(from = 18, toExclusive = 21)
    case class Adult() extends AgeRange(from = 21, toExclusive = 35)
    case class MatureAdult() extends AgeRange(from = 35, toExclusive = 55)
    case class SeniorAdult() extends AgeRange(from = 55, toExclusive = 120)
  }



}
