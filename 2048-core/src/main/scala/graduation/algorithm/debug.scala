package graduation.algorithm

import argonaut.Argonaut._
import argonaut._
import graduation.models.{Grid, Result}
import graduation.models.Grid._
/**
  * Created by KeXu on 2017/5/7.
  */
object debug {


  def main(args: Array[String]): Unit = {
   val jsonStr1 =
      """
        |{"data":
        |[
        |[128,2,2,2],
        |[32,16,32,8],
        |[16,4,4,4],
        |[4,2,2,2]
        |],
        |"step":10,
        |"playerTurn":true,
        |"key":"test"
        |}""".stripMargin
    val grid1 = jsonStr1.decodeOption[Grid].get
    AITest(grid1)

   /* val jsonStr2 =
      """
        |{"data":
        |[
        |[128,2,2,2],
        |[32,16,32,8],
        |[16,4,4,4],
        |[4,2,2,2]
        |],
        |"step":10,
        |"playerTurn":true,
        |"key":"test"
        |}""".stripMargin
    val grid2 = jsonStr2.decodeOption[Grid].get
    //moveTest(grid2)
    //maxValueTest(grid2)
    AITest(grid2)*/

  }

  def moveTest(grid:Grid):Unit={
    for(direct <- List(Grid.UP, Grid.LEFT, Grid.DOWN, Grid.RIGHT)){
      val newGird=grid.clone()
      if(newGird.move(direct)){
        println(s"${direct} can move ,"+newGird.toString)
      }
      else {
        println(s"${direct} can not move")
      }
    }
  }

  def AITest(grid:Grid):Unit={
    val bestDiret = new AI(grid).getBest()
    val result = Result(grid.k, bestDiret._1).asJson.toString()
    println(result)
  }

  def maxValueTest(grid: Grid):Unit={
    println(s"Grid[MaxValue]->${grid.maxValue()}")
  }
}
