package com.chattingapplication.springbootserver.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary getCloudinary(){
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dxjkpbzmo",
            "api_key", "728767975167981",
            "api_secret", "XG9MHhjvkixgZcRfriKwyXSEqjM",
            "secure", true
        ));
        return cloudinary;
    }
}
