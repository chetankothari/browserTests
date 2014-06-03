/**
 * creats a html equivalent of the given image by mapping each pixel of the image to a 
 * div in html. the divs are postioned relative to eachother
 */

import java.awt.Color
import java.io._
import javax.imageio._
import scala.language.postfixOps
import scala.xml.PrettyPrinter

for(file <- args) { 
    /**
     * renames the file to a .html file
     */
    val filePathSplit = file.split("/")
    val newFileName = filePathSplit.last.split("[.]").head + "Relative.html"
    val newFilePath = filePathSplit.dropRight(1).mkString("","/", "/") + newFileName
    val srcImg = new File(file)
    val img = ImageIO.read(srcImg)

    val w = img.getWidth
    val h = img.getHeight
    
    /**
     * defines the 
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
        display:inline; 
        float:left; 
        margin:0px; 
        padding:0px; 
        border:0px none;
      }"""

    val head = <head>
        <style> 
        {s"$bodyCSS\n      $imageContainerCSS\n    $imageRowCSS\n    $pixelCSS"}
        </style>
    </head>


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
            <div class="pixel" style={s"background-color:rgb($r, $g, $b);"}></div> 
        } toSeq 
        val row = <div class="imageRow" style={s"width: ${w}px;"}>{pixels}</div>
        row
    } toSeq

    /**
     * the rows are put into a container
     */
    val imageContainer = <div class="imageContainer" style={s"width: ${w}px; height: ${h}px;"}>{rows}</div>

    val html = <html>{head}<body>{imageContainer}</body></html>

        /**
         * wriites the html to a file in the same directory as the original image
         */
    val xmlFormatter = new PrettyPrinter(120, 2)
    val fileWriter = new PrintWriter(new File(newFilePath))
    fileWriter.write(xmlFormatter.format(html))
    fileWriter.close
}
