package graduation.models

import argonaut._
import Argonaut._
import org.apache.avro.SchemaBuilder.ArrayBuilder

import scala.collection.mutable.ArrayBuffer
import util.control.Breaks._

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
        //move up
        case Grid.UP =>
          val baseArray=data.map(_.clone()).clone()
        for(y <- 0 to 3){
          var x=0
          while (x<4){
            breakable{
              for(x1 <- (x+1) to 3){
                if(data(x1)(y) > 0){
                  if (data(x)(y) <= 0) {
                    setCell((x,y),data(x1)(y))
                    setCell((x1,y),0)
                    x=x-1
                  } else if (data(x)(y) == (data(x1)(y))) {
                    setCell((x,y),data(x1)(y) * 2)
                    setCell((x1,y),0)
                  }
                  break()
                }
              }
            }
            x=x+1
          }
        }
          !equalData(baseArray,data)
        //move down
        case Grid.DOWN =>
          val baseArray=data.map(_.clone()).clone()
          for(y <- 0 to 3){
            var x=3
            while (x>=0){
              breakable {
                var x1=x-1
              while(x1>=0){
                if (data(x1)(y) > 0) {
                  if (data(x)(y) <= 0) {
                    setCell((x,y),data(x1)(y))
                    setCell((x1,y),0)
                    x=x+1
                  } else if (data(x)(y) == data(x1)(y)) {
                    setCell((x,y),data(x1)(y) * 2)
                    setCell((x1,y),0)
                  }
                  break()
                }
                x1=x1-1
              }
              }
              x=x-1
            }
          }
         !equalData(baseArray,data)
        //move left
        case Grid.LEFT =>
          val baseArray=data.map(_.clone()).clone()
          for(x <- 0 to 3){
            var y=0
            while (y<4){
              breakable {
                for(y1 <- (y+1) to 3){
                  if(data(x)(y1) > 0){
                    if (data(x)(y) <= 0) {
                      setCell((x,y),data(x)(y1))
                      setCell((x,y1),0)
                      y=y-1
                    } else if (data(x)(y) == (data(x)(y1))) {
                      setCell((x,y),data(x)(y1) * 2)
                      setCell((x,y1),0)
                    }
                    break()
                  }
                }
              }
            y=y+1
            }
          }
          !equalData(baseArray,data)
          //move right
        case Grid.RIGHT =>
          val baseArray=data.map(_.clone()).clone()
          for(x <- 0 to 3){
            var y=3
            while (y>=0){
              breakable {
                var y1=y-1
                while(y1>=0){
                  if (data(x)(y1) > 0) {
                    if (data(x)(y) <= 0) {
                      setCell((x,y),data(x)(y1))
                      setCell((x,y1),0)
                      y=y+1
                    } else if (data(x)(y) == data(x)(y1)) {
                      setCell((x,y),data(x)(y1) * 2)
                      setCell((x,y1),0)
                    }
                    break;
                  }
                  y1=y1-1
                }
              }
              y=y-1
            }
          }
          !equalData(baseArray,data)
      }

  }

  // 克隆该Grid对象
  override def clone(): Grid = {
    val copydata=data.map(_.clone()).clone()
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

  def equalData(data1:Array[Array[Int]],data2:Array[Array[Int]]):Boolean={
    for(row <- 0 to 3;col <- 0 to 3)
      {
          if(data1(row)(col) != data2(row)(col)){
           return false
          }
      }
    true
  }

  // 计算局面的平滑性
  def smoothness():Double = ???

  // 计算局面孤立点的数目
  def islands() :Double= ???

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
