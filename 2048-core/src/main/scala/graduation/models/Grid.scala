package graduation.models

import argonaut._
import Argonaut._
import org.apache.avro.SchemaBuilder.ArrayBuilder

import scala.collection.mutable.ArrayBuffer


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

  /**
    * 玩家切换
    * @return
    */
  def playerCutover():Boolean={
    playerTurn = !playerTurn
    playerTurn
  }

  // 朝某个方向移动后的局面,如果不能移动返回false
  def move(direct:Grid.Direct):Boolean = {
      direct match {
        case Grid.UP => println("上")


        case Grid.DOWN => println("下")

        case Grid.LEFT => println("左右")

        case Grid.RIGHT => println("右")
      }
    true

  }

  // 克隆该Grid对象
  override def clone(): Grid = {
    val copydata=data.clone()
    new Grid(key,playerTurn,step,copydata)
  }

  // 评价该局面的评分
  def eval(): Double = ???

  // 返回空格的索引数组
  def availableCells():Array[(Int,Int)] = {
    val noneArray = new ArrayBuffer[(Int,Int)]()
      for (row <- 0 to 3) {
        for (col <- 0 to 3) {
        if(data(row)(col).equals(0)){
         noneArray += ((row,col))
        }
      }
    }
    noneArray.toArray
  }

  // 添加格子，设置value为0即为删除格子
  def setCell(index:(Int,Int) , value: Int):Grid = {
    data(index._1)(index._2)=value
    this
  }

  // 计算局面的平滑性
  def smoothness():Double = ???

  // 计算局面孤立点的数目
  def islands():Double = {
    var dataMark=(for(x <- 0 to 3; y <- 0 to 3) yield ((x,y) -> false)) toMap
    def mark(x:Int,y:Int,value:Int):Unit={
      if(x>=0 && x<=3 && y>=0 && y<=3 && data(x)(y)!=0 && data(x)(y)==value && !dataMark((x,y))){
        dataMark += ((x,y) -> true)
        Grid.directs.foreach(d=>{
          val vec=Grid.vectors(d)
          mark(x+vec._1,y+vec._2,value)
        })
      }
    }
    var isLands=0;
    for(x <- 0 to 3; y<- 0 to 3){
      if(data(x)(y)!=0 && !dataMark((x,y))){
        isLands+=1
        mark(x,y,data(x)(y))
      }
    }

    isLands
  }


  // 计算局面的单调性
  def monotonicity():Double= ???

  // 局面中最大的值
  def maxValue():Int= {
    val mValue=data.maxBy(_.max).max
    mValue
  }

}


object Grid extends Enumeration{

  type Direct = Value
  val UP = Value("up")
  val LEFT = Value("left")
  val DOWN = Value("down")
  val RIGHT = Value("right")
  val NONE = Value("none")
  val directs=List(UP,LEFT,DOWN,RIGHT)

  val vectors=Map[Direct,(Int,Int)](
    UP->(0,-1),
    RIGHT->(1,0),
    DOWN->(0,1),
    LEFT->(-1,0)
  )

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
