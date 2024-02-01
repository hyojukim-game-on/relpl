//package com.ssafy.relpl.service;
//
//import java.util.ArrayList;
//import java.util.UUID;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//
//@Service
//public class SampleMusicImpl{
//
//
//    public int uploadFile(String userId, String genre, MultipartFile multipartFile){
//
//        String fileName = multipartFile.getOriginalFilename();
//        String s3FileName = UUID.randomUUID() + fileName;
//
//        try {
//
//            String musicUrl = uploadFileToS3(userId, genre, s3FileName, multipartFile);
//
//            if (musicUrl == null) {
//                log.info("upload S3 filed");
//                return 0;
//            }
//
//            int res = uploadUrlToEC2DB(userId, fileName, s3FileName, genre, musicUrl);
//
//            if (res == 0) log.info("upload DB filed");
//            else log.info("upload Success!");
//
//            fService.broadCastMessage(genre, multipartFile.getOriginalFilename() + "가 업로드됬습니다.", genre, "upload", userId);
//            // genre genre 수정 필요
//            return res;
//
//        } catch(Exception e) {
//            e.printStackTrace();
//            log.info(" upload filed");
//        }
//        return 0;
//    }
//
//    @Override
//    public String uploadFileToS3(String userId, String genre, String s3fileName, MultipartFile multipartFile){
//
//        try {
//            AmazonS3 amazonS3 = awsConfig.amazonS3();
//            ObjectMetadata objMeta = new ObjectMetadata();
//            objMeta.setContentLength(multipartFile.getInputStream().available());
//            amazonS3.putObject(bucket, s3fileName, multipartFile.getInputStream(), objMeta);
//            return amazonS3.getUrl(bucket, s3fileName).toString();
//        }  catch(Exception e) {
//            e.printStackTrace();
//            log.info("uploadFileToS3 fail");
//        }
//        return null;
//
//    }
//
//    @Override
//    public int uploadUrlToEC2DB(String userId, String fileName, String s3FileName, String genre, String musicUrl) {
//        MusicDTO dto = new MusicDTO(userId, fileName, s3FileName, genre, musicUrl);
//        log.info("{}", dto);
//        return repo.insert(dto);
//    }
//
//    @Override
//    public MusicDTO getMusicByID(String id) {
//        return repo.selectById(id);
//    }
//
//    @Override
//    public ArrayList<MusicDTO> getMusicByGenre(String genre) {
//        return repo.selectByGenre(genre);
//    }
//
//    @Override
//    public ArrayList<MusicDTO> getMusicByUser(String user) {
//        return repo.selectUserUploadMusic(user);
//    }
//
//    @Override
//    public int deleteMusic(String id) {
//        try {
//
//            log.info("{}", id);
//
//            MusicDTO dto = repo.selectById(id);
//
//            if (dto == null) {
//                log.info("delete faild : id not exist in ec2");
//                return 0;
//            } else {
//                repo.delete(id);
//                log.info("delete ec2 db success");
//            }
//
//            AmazonS3 amazonS3 = awsConfig.amazonS3();
//            String[] split = dto.getMusicUrl().split("/");
//            String key = split[split.length - 1];
//            log.info("request key = {}", key);
//            amazonS3.deleteObject(bucket, key);
//
//            fService.broadCastMessage(dto.getMusicName() + "가 삭제됬습니다.", dto.getMusicGenre(), dto.getMusicGenre(), "delete", dto.getUploadUser());
//
//            return 1;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info(" delete filed");
//        }
//
//        return 0;
//    }
//
//    @Override
//    public int updateFile(MusicDTO dto) {
//        int res = repo.update(dto);
//        try {
//            fService.broadCastMessage(dto.getMusicName() + "가 갱신.", dto.getMusicGenre(), dto.getMusicGenre(), "update", dto.getUploadUser());
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info(" update filed");
//        }
//
//        return res;
//    }
//
//}
//
//
