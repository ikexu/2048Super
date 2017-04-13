package graduation.util

import scala.util.parsing.json.JSON
/**
  * Created by KeXu on 2017/4/13.
  */
object JsonUtil {


  def parseFull(json:String):Map[String, String]={

    val parsed=JSON.parseFull(json)
    parsed match {
      case Some(data:Map[String, String]) => data
      case None =>  null
    }
  }

  def main(args: Array[String]): Unit = {
    val json=
      s"""
         |{
         |  "grid":[[0,2,0,4],[0,0,8,0],[4,0,0,0],[2,0,0,0]],
         |  "uuid":"Cekw67uyMpBGZLRP2HFVbe"
         |}
       """.stripMargin
    parseFull(json).foreach(println(_))
  }

}
