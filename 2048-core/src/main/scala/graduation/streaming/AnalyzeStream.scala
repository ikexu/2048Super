package graduation.streaming

import org.apache.spark.streaming.dstream.InputDStream

import scala.reflect.ClassTag

/**
  * Created by KeXu on 2017/4/12.
  */
object AnalyzeStream extends Analyze {

  override def analyzeStream[K: ClassTag, V: ClassTag](stream: InputDStream[(K, V)]): Unit = {

    val processedStream = transform(stream)
    processedStream.foreachRDD { rdd =>
      rdd.foreach { kv =>
        analyze(kv)
      }
    }

  }

  override def analyze[K: ClassTag, V: ClassTag](kv: (K, V)): Unit = {

  }

  private def transform[K: ClassTag, V: ClassTag](stream: InputDStream[(K, V)]): InputDStream[(K, V)] = {

    stream
  }
}
