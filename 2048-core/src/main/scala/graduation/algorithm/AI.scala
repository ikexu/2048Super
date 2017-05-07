package graduation.algorithm

import java.util.Date

import graduation.models.Grid
import org.apache.spark.mllib.classification.LogisticRegressionModel

import scala.collection.mutable.ListBuffer

/**
  * Created by wendell on 21/4/2017.
  */
class AI(var d: Grid) {
  var grid = d;

  // 评价该局面的评分
  def eval(): Double = {
    // 权重

    val (smoothWeight, monoWeight, emptyWeight, maxWeight) = (AI.smoothWeight, AI.monoWeight, AI.emptyWeight, AI.maxWeight)

    grid.smoothness() * smoothWeight + grid.monotonicity() * monoWeight + grid.availableCells().length * emptyWeight + grid.maxValue() * maxWeight
  }

  def search(dept: Int, alpha: Double, beta: Double): (Grid.Direct, Double) = {
    var bestScore: Double = 0
    var bestMove: Grid.Direct = Grid.NONE
    var result: (Grid.Direct, Double) = null

    if (grid.playerTurn) {
      // 该玩家选择方向
      bestScore = alpha
      for (direct <- List(Grid.UP, Grid.LEFT, Grid.DOWN, Grid.RIGHT)) {
        val newGrid = grid.clone()
        if (newGrid.move(direct)) {
          newGrid.playerTurn = false
          val newAI = AI(newGrid)
          // 深度为0，返回此时最好的中间局
          if (dept == 0) {
            result = (bestMove, this.eval())
          } else {
            result = search(dept - 1, bestScore, beta)
          }
          if (result._2 > bestScore) {
            bestScore = result._2
            bestMove = direct
          }
          if (bestScore > beta) {
            return (bestMove, beta)
          }
        }
      }
    } else {
      // 该电脑选择放入数字
      bestScore = beta
      var cells = grid.availableCells()
      var badScore = 100.0;
      var badCells = ListBuffer[((Int, Int), Int)]()
      List(2, 4).foreach(value => {
        cells.foreach(cell => {
          grid.setCell(cell, value)
          val score = grid.smoothness() + grid.islands()
          if (score < badScore) {
            badScore = score
            badCells = ListBuffer((cell, value))
          } else if (score == badScore) {
            badCells += ((cell, value))
          }
          grid.setCell(cell, 0)
        })
      })
      badCells.foreach(badValue => {
        var newGrid = grid.clone()
        newGrid.playerTurn = true
        result = AI(newGrid.setCell(badValue._1, badValue._2)).search(dept, alpha, bestScore)
        if (result._2 < bestScore) {
          bestScore = result._2
        }
        if (bestScore < alpha) {
          return (Grid.NONE, alpha)
        }
      })
    }
    (bestMove, bestScore)
  }

  def getBest(): (Grid.Direct, Double) = {
    iterativeDeep()
  }

  def iterativeDeep(): (Grid.Direct, Double) = {
    val start = new Date().getTime
    var dept = 0
    var best: (Grid.Direct, Double) = null
    import scala.util.control.Breaks
    val loop = new Breaks

    loop.breakable {
      do {
        var newBest = search(dept, -10000, 10000)
        if (newBest._1 == Grid.NONE) {
          loop.break
        }
        best = newBest
        dept += 1
      } while (System.currentTimeMillis() - start < AI.searchTimeOut)
      //} while (dept<=2)
    }
    best
  }

}

object AI {
  var searchTimeOut: Int = _
  var smoothWeight: Double = _
  var monoWeight: Double = _
  var emptyWeight: Double = _
  var maxWeight: Double = _
  var model: LogisticRegressionModel = _

  def apply(g: Grid) = new AI(g)
}

