package graduation.models

import argonaut._
import Argonaut._


/**
  * Created by KeXu on 2017/4/13.
  */
class Grid(val k:String, val p:Boolean,val s:Int,var d:Array[Array[Int]]) extends Serializable{

  var key:String=k
  var playerTurn:Boolean=p
  var data=d
  var step=s

  def getRow(rowIndex:Int):(Int,Int,Int,Int)={

    val row=data(rowIndex)
    (row(0),row(1),row(2),row(3))
  }

  def getCol(colIndex:Int):(Int,Int,Int,Int)={
    (data(0)(colIndex),data(1)(colIndex),data(2)(colIndex),data(3)(colIndex))
  }

}


object Grid extends Enumeration{

  type Direct = Value
  val UP = Value("up")
  val LEFT = Value("left")
  val DOWN = Value("down")
  val RIGHT = Value("right")

  def apply(k: String,p: Boolean,s: Int,d: Array[Array[Int]]) = new Grid(k,p,s,d)

  implicit def GridEncodeJson: EncodeJson[Grid] =
    EncodeJson((g: Grid) =>
      ("key" := g.key) ->:
        ("playerTurn" := g.playerTurn) ->:
        ("data" := g.data) ->:
        jEmptyObject)

  implicit def GridDecodeJson: DecodeJson[Grid] =
    DecodeJson(g => for {
      key <- (g --\ "key").as[String]
      playerTurn <- (g --\ "playerTurn").as[Boolean]
      step <- (g --\ "step").as[Int]
      data <- (g --\ "data").as[Array[Array[Int]]]
    } yield new Grid(key,playerTurn,step,data))
}
