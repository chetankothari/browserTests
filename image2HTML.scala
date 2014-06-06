/**
 * creats a html equivalents of the given image by mapping each pixel of the image to a 
 * div in html.
 */

import java.awt.Color
import java.io._
import javax.imageio._
import scala.language.postfixOps
import scala.xml.PrettyPrinter

abstract class Positioning
case class Relative(classes: List[String]) extends Positioning
case class Absolute(classes: List[String]) extends Positioning

def toDiv(img: java.awt.image.BufferedImage, positioningType: Positioning) = {
    val w = img.getWidth
    val h = img.getHeight

    /**
     * creates rows of pixels
     */
    val rows: scala.xml.NodeSeq = 0 until h map { y =>
        /**
         * creats a div for each pixel
         */
        val pixels: scala.xml.NodeSeq = 0 until w map { x =>
            val pixel = new Color(img.getRGB(x, y))
            val r = pixel.getRed
            val g = pixel.getGreen
            val b = pixel.getBlue
            positioningType match {
                case Relative(classes) => <div class={classes.mkString(" ")} style={s"background-color:rgb($r, $g, $b); top: ${y}px; left: ${x}px;"}></div>
                case Absolute(classes) => <div class={classes.mkString(" ")} style={s"background-color:rgb($r, $g, $b); top: ${y}px; left: ${x}px;"}></div>
            }
        } toSeq
        /**
         * a row is created
         */
        val row = <div class="imageRow" style={s"width: ${w}px;"}>{pixels}</div>
        row
    } toSeq

    /**
     * the rows are put into a container
     */
    <div class="imageContainer" style={s"width: ${w}px; height: ${h}px;"}>{rows}</div>
}

for(file <- args) {
    /**
     * renames the file to a .html file
     */
    val filePathSplit = file.split("/")
    val basePath = filePathSplit.dropRight(1).mkString("","/", "/")
    val sourceFileName = filePathSplit.last.split("[.]").head
    val baseFileName = basePath + sourceFileName
    val img = ImageIO.read(new File(file))

    /**
     * defines the css
     */
    val bodyCSS = """body {
        margin:0px;
        padding:0px;
        border:0px none;
      }"""

    val imageContainerCSS = """.imageContainer {
        display:block
      }"""

    val imageRowCSS = """.imageRow {
        display:block;
        height:1px; margin:0px;
        padding:0px;
        border:0px none;
      }"""

    val pixelCSS = """.pixel {
        width:1px;
        height:1px;
        margin:0px;
        padding:0px;
        border:0px none;
      }"""

    val absolutePositionCSS = """.absolutePosition {
        position:absolute;
      }"""

    val relativePositionCSS = """.relativePosition {
        display:inline;
        float:left;
      }"""

    val head = <head>
        <style>
        {s"$bodyCSS\n      $imageContainerCSS\n    $imageRowCSS\n    $pixelCSS\n    $absolutePositionCSS\n    $relativePositionCSS"}
        </style>
    </head>

    val positions = List(Relative(List("relativePosition", "pixel")), Absolute(List("absolutePosition", "pixel")))
    val varients = positions map { position =>
        <html>{head}<body>{toDiv(img, position)}</body></html>
    }

    /**
     * wriites the html to a file in the same directory as the original image
     */
    val xmlFormatter = new PrettyPrinter(120, 2)
    varients.zipWithIndex foreach { case(html, varientNo) =>
        val fileWriter = new PrintWriter(new File(s"${baseFileName}Varient${varientNo}.html"))
        fileWriter.write(xmlFormatter.format(html))
        fileWriter.close
    }
}
