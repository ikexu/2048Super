package graduation.util

import graduation.models.Grid._
import graduation.models.Grid
import graduation.models.Result
import argonaut._, Argonaut._


/**
  * Created by KeXu on 2017/4/13.
  */
object JsonUtil {

  def main(args: Array[String]): Unit = {
    val jsonStr=
      """
        |{"data":
        |[
        |[1,2,3,4],
        |[0,0,0,0],
        |[0,0,64,0],
        |[0,0,0,0]
        |],
        |"step":10,
        |"playerTurn":true,
        |"key":"test"
        |}""".stripMargin
    val grid=jsonStr.decodeOption[Grid].get
    /*println(grid.key,grid.playerTurn,grid.step,grid.data.deep.mkString(" "))

    val r=Result("test2",10,Grid.UP)
    println(r.asJson.toString())*/
    //grid.availableCells().foreach(println(_))
   // println(grid.maxValue())
    //grid.setCell((1,1),1024).data.foreach(i=>println(i.mkString(" ")))
    /*println(grid.data+" "+grid)
    val newGrid=grid.clone()
    println(newGrid.data+" "+newGrid)*/
    grid.move(Grid.LEFT)



  }

}
