package graduation.models


import graduation.models.Grid.Direct
import argonaut._
import Argonaut._

/**
  * Created by wendell on 17/4/2017.
  */
class Result (val k:String,val s:Int, val d:Direct){
  var key=k
  var step=s
  var direct=d
}

object Result{

  def apply(k: String,s: Int,d: Grid.Direct) = new Result(k,s,d)
  implicit def ResultEncodeJson: EncodeJson[Result] =
  EncodeJson((r: Result) =>
    ("key" := r.key) ->:
      ("step" := r.step) ->:
      ("direct" := r.direct.toString) ->:
      jEmptyObject)
}
