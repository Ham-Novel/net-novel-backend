package com.ham.netnovel.s3;


import com.ham.netnovel.common.exception.ServiceMethodException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Service
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.buket}")
    private String BUKET_NAME;//S3 버킷이름

    @Value("${aws.cloudfront.domain}")
    private String DOMAIN_NAME;//cloud front 가상 도메인 이름

    public S3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    private final String FOLDER_NAME = "thumbnail";//S3 버킷에서 접근할 폴더 이름


    @Override
    public String uploadFileToS3(MultipartFile file) {
        //현재시간과 파일이름으 조합으로 고유한 파일명 생성
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try {
            //S3에 Put 요청을 하기위한 Request 객체 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUKET_NAME)//S3에서 사용될 버킷 이름
                    .key(FOLDER_NAME + "/" + fileName)//버킷 안에 폴더 이름과 파일 명으로 엔드포인트 설정
                    .build();

            //업로드할 파일을 바이트로 변환하여 S3 버킷에 저장후, 반환된 응답 객체에 저장
            PutObjectResponse response = s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(file.getBytes()));

            //업로드 결과 출력
            log.info("S3 파일 업로드 결과 ={}",response.toString());

            return fileName;
        } catch (Exception ex) {
            throw new ServiceMethodException("uploadFileToS3 메서드 에러, S3 파일 업로드 실패"+ex+ex.getMessage());
        }
    }

    @Override
    public String generateCloudFrontUrl(String fileName) {
        try {
            //cloudfront 도메인으로 S3 이미지 접근, URL은 https://{cloudfront도메인이름}/{폴더이름}/{파일이름}
            return String.format("https://%s/%s/%s", DOMAIN_NAME, FOLDER_NAME, fileName); //생성된 URL 반환
        } catch (Exception ex) {
            throw new ServiceMethodException("generateCloudFrontUrl 메서드 에러 cloudfront URL 생성실패"+ex+ex.getMessage());
        }
    }
}





