package de.vkoop.imageconverter

import org.imgscalr.Scalr
import org.imgscalr.Scalr.{Method, Mode}

import java.nio.file.{FileSystems, Files, Path, Paths}
import javax.imageio.ImageIO
import scala.collection.convert.ImplicitConversions.`iterable AsScalaIterable`
import scala.collection.parallel.CollectionConverters.*
import scala.language.implicitConversions

object MyApp {

  implicit def string2Path(pathString: String): Path = Paths.get(pathString)

  implicit def path2String(path: Path): String = path.toString

  def convertFile(filePath: Path, outPath: Path): Unit = {
    val inStream = Files.newInputStream(filePath)
    val outStream = Files.newOutputStream(outPath)
    val img = ImageIO.read(inStream)
    val out = Scalr.resize(img, Method.BALANCED, Mode.AUTOMATIC, 1500, 1500);
    ImageIO.write(out, "jpg", outStream)

    inStream.close()
    outStream.close()
  }

  def getRecursiveFileIterator(path: Path) = Files.newDirectoryStream(path).flatMap {
    case x if Files.isDirectory(x) => Files.newDirectoryStream(x)
    case a@_ => a
  }

  def main(args: Array[String]): Unit ={
    if(args.length < 2){
      System.exit(1);
    }

    val path = args(0)
    val targetString = args(1)

    val matcher = FileSystems.getDefault.getPathMatcher("glob:**/*.{jpg,JPG}")

    getRecursiveFileIterator(path).par
      .filter(matcher.matches)
      .map(old => (old, old.replace(path, targetString)))
      .foreach {
        case (old, newPath) =>
          Files.createDirectories(newPath.getParent)

          if (!Files.exists(newPath)) {
            println("Creating file: " + newPath)
            convertFile(old, newPath)
          } else {
            println("Skipping already exists: " + newPath)
          }
      }
  }


}
