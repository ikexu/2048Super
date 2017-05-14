package graduation.algorithm

import argonaut.Argonaut._
import argonaut._
import breeze.linalg.DenseMatrix
import graduation.models.{Grid, Result}
import graduation.models.Grid._
import graduation.util.MatrixUtil
import org.apache.spark.mllib.linalg.Vectors
/**
  * Created by KeXu on 2017/5/7.
  */
object debug {


  def main(args: Array[String]): Unit = {

    //4096 2048 8,128 256 32 16,32 64 128 2,2 4 32 2
   val jsonStr1 =
      """
        |{"data":
        |[
        |[2,4096,2048,8],
        |[128,256,32,16],
        |[32,64,128,2],
        |[2,4,32,2]
        |],
        |"step":10,
        |"playerTurn":true,
        |"key":"test"
        |}""".stripMargin
    val grid1 = jsonStr1.decodeOption[Grid].get
    val grid2=grid1.clone()
    grid2.move(Grid.DOWN)
    val headMatrix=DenseMatrix(grid1.data.map(i =>
      Tuple4(i(0).toDouble, i(1).toDouble, i(2).toDouble, i(3).toDouble)):_*)
    val lastMatrix=DenseMatrix(grid2.data.map(i =>
      Tuple4(i(0).toDouble, i(1).toDouble, i(2).toDouble, i(3).toDouble)):_*)

    val convert = MatrixUtil.convertMatrix(headMatrix, lastMatrix)
    val features = Vectors.dense(convert.data.mkString(" ").trim().split(' ').map(java.lang.Double.parseDouble))
    println(features)
    //moveTest(grid1)
   // AITest(grid1)

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
