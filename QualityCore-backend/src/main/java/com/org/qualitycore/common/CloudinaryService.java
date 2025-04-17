package com.org.qualitycore.common;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.org.qualitycore.config.CloudinaryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(CloudinaryConfig cloudinaryConfig) {
        this.cloudinary = new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", cloudinaryConfig.getCloudName(),
                        "api_key", cloudinaryConfig.getApiKey(),
                        "api_secret", cloudinaryConfig.getApiSecret()
                )
        );
    }

    // 이미지 업로드
    public String uploadImage(MultipartFile file) throws IOException {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("url");

            if (imageUrl == null) {
                throw new IOException("Image URL is null after upload");
            }

            return imageUrl;
        } catch (IOException e) {

            throw new IOException("Cloudinary image upload failed: " + e.getMessage(), e);
        }
    }

    // 파일 업로드
    public String uploadFile(MultipartFile file) throws IOException {
        Map<String, Object> uploadOptions = ObjectUtils.asMap(
                "resource_type", "raw"
        );

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
        String fileUrl = (String) uploadResult.get("secure_url");

        if (fileUrl == null) {
            throw new IOException("Failed to upload file to Cloudinary");
        }
        return fileUrl;
    }

}
