package graduation.streaming

import graduation.models.Grid
import graduation.models.Grid._
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import argonaut._, Argonaut._
import scala.reflect.ClassTag

/**
  * Created by KeXu on 2017/4/12.
  */
object AnalyzeStream extends Analyze {

  override def analyzeStream(stream: InputDStream[(String, String)]): Unit = {
    transform(stream).foreachRDD { rdd =>
      rdd.foreachPartition(p=>{
        p.foreach(record=>{
          analyze(record)
        })
      })
    }
  }

  override def analyze(kv: (String, Grid)): Unit = {
    println(kv._1,kv._2)

  }




  private def transform(stream: InputDStream[(String,String)]):DStream[(String, Grid)]= {
    stream.transform{rdd =>
      rdd.map{ message =>
//        val key=message._1
//        val value=message._2
//        val grid=value.decodeOption[Grid].get
        (message._1,message._2.decodeOption[Grid].get)
      }
    }
  }
}
