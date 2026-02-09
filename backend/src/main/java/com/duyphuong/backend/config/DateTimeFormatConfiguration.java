package com.duyphuong.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// cấu hình định dạng Date/Time
public class DateTimeFormatConfiguration implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        // Cấu hình sử dụng chuẩn ISO-8601 cho ngày giờ
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);
    }
}
