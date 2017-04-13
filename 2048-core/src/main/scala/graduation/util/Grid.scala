package graduation.util

/**
  * Created by KeXu on 2017/4/13.
  */
class Grid(uuid:String) extends Serializable{

  val id:String=uuid
  var col1:(Int,Int,Int,Int)= _
  var col2:(Int,Int,Int,Int)= _
  var col3:(Int,Int,Int,Int)= _
  var col4:(Int,Int,Int,Int)= _

  def setCol1(col:(Int,Int,Int,Int)):Unit={
    col1=col
  }
  def setCol2(col:(Int,Int,Int,Int)):Unit={
    col2=col
  }
  def setCol3(col:(Int,Int,Int,Int)):Unit={
    col3=col
  }
  def setCol4(col:(Int,Int,Int,Int)):Unit={
    col4=col
  }

}
