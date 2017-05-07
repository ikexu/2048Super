package graduation.models


import argonaut.Argonaut._
import argonaut._
import graduation.models.Grid.Direct

/**
  * Created by wendell on 17/4/2017.
  */
case class Result(key: String, direct: Direct)

object Result {

  implicit def ResultEncodeJson: EncodeJson[Result] =
    EncodeJson((r: Result) =>
      ("key" := r.key) ->:
        ("direct" := r.direct.id) ->:
        jEmptyObject)
}
