package graduation.streaming

import graduation.models.Grid
import org.apache.spark.streaming.dstream.InputDStream

/**
  * Created by KeXu on 2017/4/12.
  */
trait Analyze {

  def analyzeStream(stream: InputDStream[(String, String)])

  def analyze(kv: (String, Grid))

}
