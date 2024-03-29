import util.Pixel
import util.Util.{getNeighbors, toGrayScale}

import java.lang.Math.round
import scala.annotation.tailrec

// Online viewer: https://0xc0de.fr/webppm/
object Solution {
  type Image = List[List[Pixel]]
  type GrayscaleImage = List[List[Double]]

  // define constants
  private val BLACK: Pixel = Pixel(0, 0, 0)
  private val WHITE: Pixel = Pixel(255, 255, 255)
  val PPMFormat = "P3"
  val maximColor = "255"


  // prerequisites
  def fromStringPPM(image: List[Char]): Image = {
    val lines = image.mkString.split("\n").toList
    val width = lines(1).split(" ").head.toInt
    val height = lines(1).split(" ").last.toInt
    val remainingLines = lines.drop(3)

    val pixels = remainingLines.flatMap(line => line.split(" ").toList).map(_.toInt)
    val pixelGroups = pixels.grouped(3).toList
    val pixelRows = pixelGroups.grouped(width).toList

    pixelRows.map(row => row.map(pixel => Pixel(pixel.head, pixel(1), pixel.last)))
  }


  //from ppm image to string
  def toStringPPM(image: Image): List[Char] = {
    // get the dimensions of the image
    val width = image.head.length
    val height = image.length

    // create the PPM header
    val header = PPMFormat
      + "\n"
      + width.toString + " " + height.toString
      + "\n"
      + maximColor
      + "\n"

    // create a string representation of each pixel and join them with newline characters
    val pixelStrings = image.flatten.map {
      pixel => s"${pixel.red} ${pixel.green} ${pixel.blue}"
    }
    val stringPPM = header + pixelStrings.mkString("\n") + "\n"

    stringPPM.toList
  }

  // ex 1
  def verticalConcat(image1: Image, image2: Image): Image = {
    image1 ++ image2
  }

  // ex 2
  def horizontalConcat(image1: Image, image2: Image): Image = {
    image1.zip(image2).map {
      case (line1, line2) => line1 ++ line2
    }
  }

  // generic transpose
  private def transpose[T](image: List[List[T]]): List[List[T]] = {
    // use match
    image match {
      case Nil :: _ => Nil
      case _ => image.map(_.head) :: transpose(image.map(_.tail))
    }
  }


  // ex 3
  def rotate(image: Image, degrees: Integer): Image = {
    degrees % 360 match {
      case 0 => image
      case 90 => transpose(image).reverse
      case 180 => image.reverse.map(_.reverse)
      case 270 => transpose(image.reverse)
      case _ => image
    }
  }

  // ex 4
  private val gaussianBlurKernel: GrayscaleImage = List[List[Double]](
    List(1, 4, 7, 4, 1),
    List(4, 16, 26, 16, 4),
    List(7, 26, 41, 26, 7),
    List(4, 16, 26, 16, 4),
    List(1, 4, 7, 4, 1)
  ).map(_.map(_ / 273))

  private val Gx: GrayscaleImage = List(
    List(-1, 0, 1),
    List(-2, 0, 2),
    List(-1, 0, 1)
  )

  private val Gy: GrayscaleImage = List(
    List(1, 2, 1),
    List(0, 0, 0),
    List(-1, -2, -1)
  )

  def edgeDetection(image: Image, threshold: Double): Image = {
    val grayImage = image.map(pixelRow => pixelRow.map(toGrayScale))
    val blurredImage = applyConvolution(grayImage, gaussianBlurKernel)
    val Mx = applyConvolution(blurredImage, Gx)
    val My = applyConvolution(blurredImage, Gy)
    val combinedImage = combineImagesWithOp((x, y) => Math.abs(x) + Math.abs(y))(Mx, My)

    combinedImage.map(_.map(pixel => {
      if (pixel < threshold) BLACK
      else WHITE
    }))
  }


  private def combineImagesWithOp(op: (Double, Double) => Double)(Mx: GrayscaleImage, My: GrayscaleImage) = {
    val combinedImage = Mx.zip(My).map {
      case (row1, row2) =>
        row1.zip(row2).map {
          case (pixel1, pixel2) =>
            op(pixel1, pixel2)
        }
    }
    combinedImage
  }

  private def applyConvolution(image: GrayscaleImage, kernel: GrayscaleImage): GrayscaleImage = {
    val radius = kernel.length / 2
    val neighbors = getNeighbors(image, radius)
    neighbors.map(row =>
      row.map(window =>
        combineImagesWithOp(_ * _)(window, kernel).flatten.sum
      )
    )
  }


  private def replaceAtIndex[T](list: List[T], index: Int, value: T): List[T] = {
    @tailrec
    def helper(currList: List[T], currIndex: Int, result: List[T]): List[T] = {
      currList match {
        case Nil => result.reverse
        case _ :: tail if currIndex == index => helper(tail, currIndex + 1, value :: result)
        case head :: tail => helper(tail, currIndex + 1, head :: result)
      }
    }

    helper(list, 0, Nil)
  }

  // ex 5
  def moduloPascal(m: Integer, funct: Integer => Pixel, size: Integer): Image = {
    // Initialize the image matrix with all black pixels
    val imageMatrix = List.fill(size, size)(BLACK)

    // Create a map to store the computed values of the Pascal triangle [(i,j) -> value]
    def computeValue(i: Int, j: Int, computedValues: Map[(Int, Int), Int]): (Map[(Int, Int), Int], Int) =
      computedValues.get((i, j)) match {
        case Some(value) => (computedValues, value)
        case None =>
          if (j == 0 || j == i) (computedValues + ((i, j) -> 1), 1)
          else {
            val (map1, value1) = computeValue(i - 1, j - 1, computedValues)
            val (map2, value2) = computeValue(i - 1, j, map1)
            val result = (value1 + value2) % m
            (map2 + ((i, j) -> result), result)
          }
      }

    // Compute the updated image matrix by applying 'funct' to each computed value
    val updatedImageMatrix = imageMatrix.zipWithIndex.map {
      case (row, rowIndex) =>
        (0 to rowIndex).foldLeft(row) { (acc, j) =>
          val (_, value) = computeValue(rowIndex, j, Map.empty[(Int, Int), Int])
          replaceAtIndex(acc, j, funct(value))
        }
    }

    updatedImageMatrix
  }


}