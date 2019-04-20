package br.com.ifitness.commons.helper;

import br.com.ifitness.commons.dto.UploadFileContent;
import br.com.ifitness.commons.enumeration.ImageTypeEnumeration;
import br.com.ifitness.commons.enumeration.S3BucketFolderEnumeration;
import br.com.ifitness.commons.enumeration.VideoTypeEnumeration;
import br.com.ifitness.commons.exception.IFitnessException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class AwsS3FileManager {
    @Value("${aws.bucket.url}")
    private String awsBucketUrl;

    @Value("${aws.bucket.name}")
    private String awsBucketName;

    private static final List<String> IMAGE_TYPE_ARRAY = new ArrayList<>();
    private static final List<String> VIDEO_TYPE_ARRAY = new ArrayList<>();

    static {
        IMAGE_TYPE_ARRAY.add(ImageTypeEnumeration.JPG.getContentType());
        IMAGE_TYPE_ARRAY.add(ImageTypeEnumeration.JPEG.getContentType());
        IMAGE_TYPE_ARRAY.add(ImageTypeEnumeration.PNG.getContentType());

        VIDEO_TYPE_ARRAY.add(VideoTypeEnumeration.MP4.getContentType());
    }

    private AmazonS3 awsSdkProvider;

    @Autowired
    public AwsS3FileManager(AmazonS3 awsSdkProvider) {
        this.awsSdkProvider = awsSdkProvider;
    }

    private UploadFileContent<ImageTypeEnumeration> validateBase64(final String base64) {
        String[] base64Components = base64.split(",");
        if (base64Components.length != 2) {
            throw new IFitnessException(MessageFormat.format("Invalid base64 data: {0}", base64),
                    2, HttpStatus.BAD_REQUEST);
        }

        String firstComponent = base64Components[0];
        final String fileType = firstComponent.substring(firstComponent.indexOf(':') + 1,
                firstComponent.indexOf(';'));

        if (!IMAGE_TYPE_ARRAY.contains(fileType)) {
            throw new IFitnessException(MessageFormat.format("Invalid file type: {0}", fileType),
                    3, HttpStatus.BAD_REQUEST);
        }

        return UploadFileContent.<ImageTypeEnumeration>builder()
                .bytes(Base64.decodeBase64(base64Components[1]))
                .enumType(Optional.ofNullable(ImageTypeEnumeration.contains(fileType))
                        .orElseThrow(() -> new IFitnessException(MessageFormat
                                .format("Invalid file type: {0}", fileType), 4,
                                HttpStatus.BAD_REQUEST)))
                .build();
    }

    private UploadFileContent<VideoTypeEnumeration> validateVideo(
            final MultipartFile multipartFile) throws IOException {
        final String fileType = multipartFile.getContentType();

        if (!VIDEO_TYPE_ARRAY.contains(fileType)) {
            throw new IFitnessException(MessageFormat.format("Invalid file type: {0}",
                    fileType), 3, HttpStatus.BAD_REQUEST);
        }

        return UploadFileContent.<VideoTypeEnumeration>builder()
            .bytes(multipartFile.getBytes())
            .enumType(Optional.ofNullable(VideoTypeEnumeration.contains(fileType))
                .orElseThrow(() -> new IFitnessException(MessageFormat
                    .format("Invalid file type: {0}", fileType), 4, HttpStatus.BAD_REQUEST)))
            .build();
    }

    public String uploadImage(final S3BucketFolderEnumeration s3BucketFolderEnumeration,
                              final String imageBase64, final String fileName) {
        UploadFileContent<ImageTypeEnumeration> uploadFileContent = validateBase64(imageBase64);

        InputStream fis = new ByteArrayInputStream(uploadFileContent.getBytes());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(uploadFileContent.getBytes().length);
        metadata.setContentType(uploadFileContent.getEnumType().getContentType());
        metadata.setCacheControl("public, max-age=31536000");

        String destinationFile = MessageFormat.format("{0}{1}.{2}",
                s3BucketFolderEnumeration.getValue(), fileName,
                uploadFileContent.getEnumType().getValue());

        PutObjectResult result = awsSdkProvider.putObject(awsBucketName,
                destinationFile, fis, metadata);

        if (result == null) {
            throw new IFitnessException("Upload fail!", 5, HttpStatus.BAD_REQUEST);
        }

        awsSdkProvider.setObjectAcl(awsBucketName, destinationFile,
                CannedAccessControlList.PublicRead);

        return MessageFormat.format("{0}{1}", awsBucketUrl, destinationFile);
    }

    public void removeObject(final S3BucketFolderEnumeration s3BucketFolderEnumeration,
                             final String fileName) {
        String object = MessageFormat.format("{0}{1}",
                s3BucketFolderEnumeration.getValue(), fileName);

        if (awsSdkProvider.doesObjectExist(awsBucketName, object)) {
            awsSdkProvider.deleteObject(awsBucketName, object);
        }
    }

    public String uploadVideo(final S3BucketFolderEnumeration s3BucketFolderEnumeration,
                              final MultipartFile multipartFile, final String fileName)
            throws IOException {
        UploadFileContent<VideoTypeEnumeration> uploadFileContent = validateVideo(multipartFile);

        InputStream fis = new ByteArrayInputStream(uploadFileContent.getBytes());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getBytes().length);
        metadata.setContentType(uploadFileContent.getEnumType().getValue());
        metadata.setCacheControl("public, max-age=31536000");

        String destinationFile = MessageFormat.format("{0}{1}.{2}",
                s3BucketFolderEnumeration.getValue(), fileName, "mp4");

        PutObjectResult result = awsSdkProvider.putObject(awsBucketName, destinationFile, fis,
                metadata);
        awsSdkProvider.setObjectAcl(awsBucketName, destinationFile,
                CannedAccessControlList.PublicRead);

        return MessageFormat.format("{0}{1}", awsBucketUrl, destinationFile);
    }
}
