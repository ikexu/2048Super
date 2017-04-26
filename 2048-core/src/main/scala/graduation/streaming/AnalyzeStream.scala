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

    val processedStream = transform(stream)
    processedStream.foreachRDD { rdd =>
      rdd.foreach { kv =>
        analyze(kv)
      }
    }

  }

  override def analyze(kv: (String, Grid)): Unit = {

  }




  private def transform(stream: InputDStream[(String,String)]):DStream[(String, Grid)]= {

    stream.transform{rdd =>
      rdd.map{ message =>
        val key=message._1
        val value=message._2
        val grid=value.decodeOption[Grid].get
        (key,grid)
      }
    }
  }
}
