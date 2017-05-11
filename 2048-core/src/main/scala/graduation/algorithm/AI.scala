package graduation.algorithm

import java.util.Date

import graduation.models.Grid
import org.apache.spark.mllib.classification.LogisticRegressionModel

import scala.collection.immutable.TreeMap
import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * Created by wendell on 21/4/2017.
  */
class AI(var d: Grid) {
  var grid = d;

  // 评价该局面的评分
  def eval(): Double = {
    // 权重

    val (smoothWeight, monoWeight, emptyWeight, maxWeight) = (AI.smoothWeight, AI.monoWeight, AI.emptyWeight, AI.maxWeight)

    val smoothValue = grid.smoothness() * smoothWeight
    val monoValue = grid.monotonicity() * monoWeight
    val emptyValue = math.log(grid.availableCells().length) * emptyWeight
    val maxValue = grid.maxValue() * maxWeight
    smoothValue+monoValue+emptyValue+maxValue
  }


  def expectiEval():Double={
    grid.modelScore()
  }

  def expectimaxSearchBest(dept:Int): (List[(Grid.Direct,Double)]) ={
//    var score = Double.MinValue;
//    var bestMove = Grid.NONE;
    var scoreList=ListBuffer[(Grid.Direct,Double)]()

    for (d <- Grid.directs) {
      var newGrid = grid.clone();
      newGrid.playerTurn=false
      if(newGrid.move(d)){
        val newScore = AI(newGrid).expectimaxSearch(dept - 1)
        scoreList+=((d,newScore))
//        if (newScore >= score) {
//          bestMove = d
//          score = newScore
//        }
      }
    }
    scoreList.toList.sortBy(-_._2)
    //(bestMove,score)
  }

  def expectimaxSearch(dept:Int):Double={

    if(dept==0){
      return expectiEval()
    }else if (grid.playerTurn) {
      var score=Double.MinValue
      // Player node
      for (direct <- Grid.directs) {
        val newGrid = grid.clone()
        if (newGrid.move(direct)) {
          newGrid.playerTurn=false
          val newAI = AI(newGrid)
          val newScore=newAI.expectimaxSearch(dept - 1)
          if(newScore>score){
            score=newScore
          }
        }
      }
      return score
    }else{
      // Chance node
      var score:Double=0
      val cells=grid.availableCells()
      cells.foreach(cell => {
        Map(2 -> 0.9,4->0.1).foreach{case (value,p)=>{
          val newGrid=grid.clone()
          newGrid.playerTurn=true
          newGrid.setCell(cell,value)
          val newScore=AI(newGrid).expectimaxSearch(dept-1)
          score+=(newScore*p)
        }}
      })
      score/=cells.length
      return score
    }
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
            result = (bestMove, newAI.grid.modelScore())
          } else {
            result = newAI.search(dept - 1, bestScore, beta)
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
      var badScore = 100000.0
      var badCells = ListBuffer[((Int, Int), Int)]()
      List(2, 4).foreach(value => {
        grid.availableCells().foreach(cell => {
          grid.setCell(cell, value)
//          val score = grid.smoothness() + grid.islands()
          val score = 0-grid.modelScore()
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
        val newGrid = grid.clone()
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
   //iterativeDeep()
   val scoreList=expectimaxSearchBest(6)
    scoreList(0)

  }

  def iterativeDeep(): (Grid.Direct, Double) = {
    val start = System.currentTimeMillis()
    var dept = 0
    var best: (Grid.Direct, Double) = null
    import scala.util.control.Breaks
    val loop = new Breaks

    loop.breakable {
      do {
        val newBest = search(dept, -10000, 10000)
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

