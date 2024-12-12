# Step1: 기본 이미지 설정(Java 17)
FROM openjdk:17-jdk-alpine
# Step2: 작업 디렉토리 설정, 도커파일 기준 경로
WORKDIR /netnovel
# Step3: 애플리케이션 파일(jar) 복사
COPY ./build/libs/netnovel-latest.jar app.jar
# Step4: 포트공개
EXPOSE 8081
# Step5: 애플리케이션 실생
ENTRYPOINT ["java", "-jar", "app.jar"]

