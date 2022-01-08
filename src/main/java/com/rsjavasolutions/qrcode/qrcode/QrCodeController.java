package com.rsjavasolutions.qrcode.qrcode;

import com.rsjavasolutions.qrcode.qrcode.request.Content;
import com.rsjavasolutions.qrcode.qrcode.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("codes")
@RequiredArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;

    @PostMapping("generate")
    public ResponseEntity<byte[]> generateQRCode(@RequestBody Content content, HttpServletResponse response) {

        return qrCodeService.generateQRCodeImage(content.getContent(), response);
    }

    @PostMapping("read")
    public Content readQRCode(@RequestParam("code") MultipartFile code) {

        return qrCodeService.readQRCode(code);
    }
}
