package de.pho.dsapdfreader;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageCompressorMain {


  public static void main(String[] args) {
    // Source and destination directories
    String sourceDirectoryPath = "path/to/source/directory";
    String destinationDirectoryPath = "path/to/destination/directory";

    // Target width for scaling
    int targetWidth = 2048;

    // Get a list of files ending with ".png" from the source directory
    File sourceDirectory = new File(sourceDirectoryPath);
    File[] files = sourceDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

    // Process each file
    if (files != null) {
      for (File file : files) {
        try {
          // Read the image
          BufferedImage originalImage = ImageIO.read(file);

          // Scale the image while maintaining aspect ratio
          int originalWidth = originalImage.getWidth();
          int originalHeight = originalImage.getHeight();
          int targetHeight = (int) ((double) originalHeight / originalWidth * targetWidth);
          BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
          Graphics2D graphics2D = scaledImage.createGraphics();
          graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
          graphics2D.dispose();

          // Compress the image
          File outputFile = new File(destinationDirectoryPath, file.getName());
          ImageIO.write(scaledImage, "png", outputFile);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
