package de.pho.dersmarteheld.qrgenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

public class QrMain {

    public static void generateStyledQRCode(String url, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300;
        int height = 300;
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);

        // Customize colors: foreground (black) and background (white)
        int foregroundColor = 0xFF0000FF; // Blue
        int backgroundColor = 0xFFFFFFFF; // White
        MatrixToImageConfig config = new MatrixToImageConfig(foregroundColor, backgroundColor);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path, config);
    }

    public static void generateQRCodeWithLogo(String url, String filePath, String logoPath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        int width = 1000;
        int height = 1000;
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);

        // Write QR code to a file
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        // Read QR code and logo images
        BufferedImage qrImage = ImageIO.read(new File(filePath));
        BufferedImage logoImage = ImageIO.read(new File(logoPath));

        // Calculate logo position and overlay it
        int deltaHeight = qrImage.getHeight() - logoImage.getHeight();
        int deltaWidth = qrImage.getWidth() - logoImage.getWidth();

        BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) combined.getGraphics();
        g.drawImage(qrImage, 0, 0, null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g.drawImage(logoImage, Math.round(deltaWidth / 2), Math.round(deltaHeight / 2), null);

        // Write combined image to a file
        ImageIO.write(combined, "PNG", new File(filePath));
    }

    public static void main(String[] args) {
        String data = "https://dersmarteheld.de";
        boolean printLogo = Boolean.TRUE;
        int size = 1000; // Größe des QR-Codes
        int freeAreaSize = 300; // Größe der freien Fläche
        String logoPath = "D:\\Daten\\OneDrive\\Dokumente\\DSH-logo-only.png"; // Pfad zum Logo

        try {
            String fileName = "qr_code_without_logo.png";
            BufferedImage qrCode = generateQRCodeImage(data, size);
            BufferedImage logo = ImageIO.read(new File(logoPath));
            if(printLogo) {
                fileName = "qr_code_with_logo.png";
                addLogoToQRCode(qrCode, logo, freeAreaSize);
            }
            ImageIO.write(qrCode, "png", new File(fileName));
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage generateQRCodeImage(String text, int size) throws WriterException {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // Höchste Fehlerkorrekturstufe

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size, hints);

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return image;
    }

    public static BufferedImage addLogoToQRCode(BufferedImage qrCode, BufferedImage logo, int freeAreaSize) {
        int centerX = qrCode.getWidth() / 2;
        int centerY = qrCode.getHeight() / 2;
        int rectWidth = freeAreaSize;
        int rectHeight = freeAreaSize;

        Graphics2D g2d = qrCode.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(centerX - rectWidth / 2, centerY - rectHeight / 2, rectWidth, rectHeight);

        // Logo skalieren
        int logoWidth = rectWidth;
        int logoHeight = rectHeight;
        Image scaledLogo = logo.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);

        // Logo in der Mitte platzieren
        g2d.drawImage(scaledLogo, centerX - logoWidth / 2, centerY - logoHeight / 2, null);
        g2d.dispose();

        return qrCode;
    }
}
