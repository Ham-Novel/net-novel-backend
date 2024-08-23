package com.ham.netnovel.s3;


import com.ham.netnovel.common.exception.ServiceMethodException;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    /**
     * 파일을 Amazon S3 버킷에 업로드합니다.
     *
     * <p>이 메서드는 {@link MultipartFile} 객체를 받아, 현재 타임스탬프를 이용해 고유한 파일 이름을 생성하고,
     * 해당 파일을 지정된 S3 버킷과 폴더에 업로드합니다. 업로드가 성공하면 메서드는 파일 이름을 반환합니다.</p>
     *
     * <p>application properties 에서 파일 용량 제한이 필요합니다. </p>
     *
     * <p>업로드 과정에서 오류가 발생하면 {@link RuntimeException}이 발생합니다.</p>
     *
     * @param file S3에 업로드할 {@link MultipartFile} 객체
     * @return S3에 업로드된 후의 파일 이름
     * @throws ServiceMethodException 파일 업로드 중 오류가 발생한 경우
     */
    String uploadFileToS3(MultipartFile file);



    /**
     * 주어진 파일 이름을 사용하여 CloudFront URL을 생성합니다.
     *
     * <p>이 메서드는 파일 이름을 받아서 CloudFront 도메인, 폴더 이름, 파일 이름을 조합하여
     * CloudFront URL을 생성하고 반환합니다. URL 생성 중 오류가 발생하면
     * {@link RuntimeException}이 발생합니다.</p>
     *
     * @param fileName CloudFront URL을 생성할 파일의 이름
     * @param thumbnailType 미니 사이즈로 리사이징 된 이미지 사용시, mini 대입(랭킹 등 대량의 섬네일이 필요한 경우)
     * @return 생성된 CloudFront URL
     * @throws RuntimeException URL 생성 중 오류가 발생한 경우
     */
   String generateCloudFrontUrl(String fileName,String thumbnailType);

}
