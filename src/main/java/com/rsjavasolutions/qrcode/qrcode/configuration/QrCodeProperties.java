package com.rsjavasolutions.qrcode.qrcode.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "qr-code")
public class QrCodeProperties {

    private String fileName;
    private int width;
    private int height;
}
