package graduation.util

import breeze.linalg._
import breeze.numerics._
/**
  * Created by KeXu on 2017/4/20.
  */
object MatrixUtil {

  /**
    * 逆矩阵计算
    * @param matrix
    * @return
    */
  def invMatrix(matrix:DenseMatrix[Double]):DenseMatrix[Double]={

  inv(matrix)
  }

  /**
    * 矩阵乘法计算
    * @param matrix1
    * @param matrix2
    * @return
    */
  def *(matrix1:DenseMatrix[Double],matrix2:DenseMatrix[Double]):DenseMatrix[Double]={

    matrix1 :* matrix2
  }

  /**
    * 变换矩阵计算
    * @param matrix1
    * @param matrix2
    * @return
    */
  def convertMatrix(matrix1:DenseMatrix[Double],matrix2:DenseMatrix[Double]):DenseMatrix[Double]={
   val invMatrix=inv(matrix1)
    *(invMatrix,matrix2)
  }

  /**
    * 测试
    * @param args
    */
  def main(args: Array[String]): Unit = {

    val a = DenseMatrix((1.0,2.0,3.0), (4.0,5.0,6.0))
    println(invMatrix(a))
    val b = DenseMatrix((9.0,8.0,7.0), (4.0,5.0,6.0))
    println(convertMatrix(a,b))
  }
}
