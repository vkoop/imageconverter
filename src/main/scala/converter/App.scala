package converter

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import scala.collection.JavaConversions.iterableAsScalaIterable

import org.imgscalr.Scalr
import org.imgscalr.Scalr.Method
import org.imgscalr.Scalr.Mode

import javax.imageio.ImageIO

object App extends App {

  implicit def string2Path(pathString: String) = Paths.get(pathString)
  implicit def path2String(path: Path) = path.toString()

  def convertFile(filePath: Path, outPath: Path) {
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
    case a @ _                     => a
  }

  val path = args(0)
  val targetString = args(1)

  val matcher = FileSystems.getDefault.getPathMatcher("glob:**/*.{jpg,JPG}")

  getRecursiveFileIterator(path).par
    .filter(matcher.matches)
    .map(old => (old, old.replace(path, targetString)))
    .foreach {
      case (old, newPath) => {
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
