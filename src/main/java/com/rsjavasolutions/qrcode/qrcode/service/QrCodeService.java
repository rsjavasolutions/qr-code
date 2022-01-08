package com.rsjavasolutions.qrcode.qrcode.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.rsjavasolutions.qrcode.qrcode.configuration.QrCodeProperties;
import com.rsjavasolutions.qrcode.qrcode.enums.FileExtension;
import com.rsjavasolutions.qrcode.qrcode.request.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrCodeService {

    private static final FileExtension FILE_EXTENSION = FileExtension.PNG;
    private final QrCodeProperties qrCodeProperties;

    public ResponseEntity<byte[]> generateQRCodeImage(String text, HttpServletResponse response) {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            bitMatrix = barcodeWriter.encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    qrCodeProperties.getWidth(),
                    qrCodeProperties.getHeight());

            MatrixToImageWriter.writeToStream(bitMatrix, FILE_EXTENSION.toString(), outputStream);

        } catch (IOException | WriterException e) {
            throw new RuntimeException("An error was encountered by converting data. Can't generate qr code");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.setHeader("Content-disposition", "attachment; filename=" + qrCodeProperties.getFileName() + "." + FILE_EXTENSION);

        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }

    public Content readQRCode(MultipartFile code) {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(code.getOriginalFilename())).toUpperCase();

        validateInputFile(code, fileName);

        Result result = null;

        try {

            BufferedImage image = ImageIO.read(code.getInputStream());
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader reader = new QRCodeReader();
            result = reader.decode(bitmap);

        } catch (ReaderException | IOException e) {
            throw new RuntimeException("An error was encountered by converting data. Can't read qr code from file");

        }
        assert result != null;
        return new Content(result.getText());
    }

    private void validateInputFile(MultipartFile code, String fileName) {
        if (code.isEmpty()) {
            throw new RuntimeException("Failed to convert empty file " + code);
        }
        if (fileName.contains("..")) {
            throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
        }
        if (!isCorrectExtension(fileName)) {
            throw new RuntimeException("Invalid extension " + fileName);
        }
    }

    private boolean isCorrectExtension(String filename) {
        boolean isCorrect = false;

        for (FileExtension value : FileExtension.values()) {
            if (filename.endsWith(value.toString())) {
                isCorrect = true;
                break;
            }
        }
        return isCorrect;
    }
}