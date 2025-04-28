package esprit.tn.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class QRAndPDFUtil {
    public static void generatePDFInvitation(String nom, String prenom, String eventName, String date, String outputPath) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
        contentStream.beginText();
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText("Invitation à l'événement");
        contentStream.endText();
        contentStream.setFont(PDType1Font.HELVETICA, 14);
        contentStream.beginText();
        contentStream.newLineAtOffset(100, 650);
        contentStream.showText("Nom: " + nom);
        contentStream.endText();
        contentStream.beginText();
        contentStream.newLineAtOffset(100, 630);
        contentStream.showText("Prénom: " + prenom);
        contentStream.endText();
        contentStream.beginText();
        contentStream.newLineAtOffset(100, 610);
        contentStream.showText("Événement: " + eventName);
        contentStream.endText();
        contentStream.beginText();
        contentStream.newLineAtOffset(100, 590);
        contentStream.showText("Date: " + date);
        contentStream.endText();
        contentStream.close();
        document.save(outputPath);
        document.close();
    }

    public static String generateQRCode(String text, String filePath, int width, int height) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        return filePath;
    }
}
