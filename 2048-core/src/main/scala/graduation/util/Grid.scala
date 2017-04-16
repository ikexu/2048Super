package graduation.util

/**
  * Created by KeXu on 2017/4/13.
  */
class Grid(uuid:String) extends Serializable{

  val data:Array[Array[Int]]=Array.ofDim[Int](2,4)

  def getRow(rowIndex:Int):(Int,Int,Int,Int)={

    val row=data(rowIndex)
    (row(0),row(1),row(2),row(3))
  }

  def getCol(colIndex:Int):(Int,Int,Int,Int)={
    (data(0)(colIndex),data(1)(colIndex),data(2)(colIndex),data(3)(colIndex))
  }

}

object GridDirect extends Enumeration{

  type GridDirect=Value
  val Up=Value(0)
  val Down=Value(1)
  val Left=Value(2)
  val Right=Value(3)

}
