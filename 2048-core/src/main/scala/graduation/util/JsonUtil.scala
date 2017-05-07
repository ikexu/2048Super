package graduation.util

import argonaut.Argonaut._
import argonaut._
import graduation.models.Grid
import graduation.models.Grid._


/**
  * Created by KeXu on 2017/4/13.
  */
object JsonUtil {

  def main(args: Array[String]): Unit = {
    val jsonStr =
      """
        |{"data":
        |[
        |[2,2,4,4],
        |[0,0,0,4],
        |[8,2,64,8],
        |[4,0,4,0]
        |],
        |"step":10,
        |"playerTurn":true,
        |"key":"test"
        |}""".stripMargin
    val grid = jsonStr.decodeOption[Grid].get
    /*println(grid.key,grid.playerTurn,grid.step,grid.data.deep.mkString(" "))

    val r=Result("test2",10,Grid.UP)
    println(r.asJson.toString())*/
    //grid.availableCells().foreach(println(_))
    // println(grid.maxValue())
    //grid.setCell((1,1),1024).data.foreach(i=>println(i.mkString(" ")))
    /*println(grid.data+" "+grid)
    val newGrid=grid.clone()
    println(newGrid.data+" "+newGrid)*/

    println(grid.move(Grid.RIGHT))
    grid.data.foreach(i => println(i.mkString(" ")))


  }

}
