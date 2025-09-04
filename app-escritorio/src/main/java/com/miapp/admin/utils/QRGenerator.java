package com.miapp.admin.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRGenerator {

    /**
     * Genera un QR con el texto dado y lo guarda en la ruta indicada (PNG).
     *
     * @param texto       Texto que contendrá el QR
     * @param rutaArchivo Ruta completa del archivo PNG donde se guardará el QR
     * @throws WriterException
     * @throws IOException
     */
    public static void generarQR(String texto, String rutaArchivo) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, 200, 200);

        Path path = FileSystems.getDefault().getPath(rutaArchivo);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

}
