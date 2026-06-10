package cn.net.mall.basic.service.upload;

import cn.net.mall.basic.config.MinioConfig;
import cn.net.mall.basic.dto.FileDTO;
import cn.net.mall.basic.enums.OssTypeEnum;
import cn.net.mall.basic.util.MinioUtil;
import cn.net.mall.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


/**
 * 文件上传service
 *
 * @date 2024/5/5 下午5:16
 */
@Service
@Slf4j
public class UploadService {
    public static final String IMAGE = "image";
    public static final String FILE = "file";

    @Value("${mall.mgt.ossType:1}")
    private int ossType;

    private final MinioConfig minioConfig;
    private final MinioUtil minioUtil;

    public UploadService(MinioConfig minioConfig, MinioUtil minioUtil) {
        this.minioConfig = minioConfig;
        this.minioUtil = minioUtil;
    }

    /**
     * 批量上传图片
     *
     * @param files 图片列表
     * @return
     */
    public FileDTO batchUpload(MultipartFile[] files) throws Exception {
        StringBuilder urlBuilder = new StringBuilder();
        for (MultipartFile file : files) {
            String[] originalFilename = file.getOriginalFilename().split("\\.");
            FileDTO upload = upload(file, IMAGE, originalFilename[originalFilename.length - 1]);
            if ("".equals(urlBuilder.toString())) {
                urlBuilder = urlBuilder.append(upload.getDownloadUrl());
            } else {
                urlBuilder = urlBuilder.append(",").append(upload.getDownloadUrl());
            }
        }
        FileDTO fileDTO = new FileDTO();
        fileDTO.setDownloadUrl(urlBuilder.toString());
        return fileDTO;
    }

    /**
     * 上传文件
     *
     * @param file     文件
     * @param fileType 文件类型
     * @return 文件实体
     * @throws Exception
     */
    public FileDTO upload(MultipartFile file, String fileType, String fileContextType) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            log.error("传入的文件名不能为空");
            throw new BusinessException("传入的文件名不能为空");
        }
//        if (!this.validateFileName(fileName)) {
//            log.error("文件名应仅包含汉字、字母、数字、下划线和点号");
//            throw new BusinessException("文件名应仅包含汉字、字母、数字、下划线和点号");
//        }

        if (OssTypeEnum.MINIO.getValue().equals(ossType)) {
            return uploadToMinio(file, fileName, fileType, fileContextType);
        }
        throw new BusinessException("OSS类型错误");
    }


    private FileDTO uploadToMinio(MultipartFile file, String fileName, String fileType, String fileContextType) throws Exception {
        String bucketName = minioConfig.getBucketName();
        minioUtil.putObject(bucketName, file, fileName);
        String fileUrl = minioUtil.getObjectUrl(bucketName, fileName);
        //String fileUrl = minioUtil.getPresignedObjectUrl(bucketName, fileName, 24 * 60 * 60);
        FileDTO fileVO = new FileDTO();
        fileVO.setFileName(fileName);
        fileVO.setDownloadUrl(fileUrl);
        return fileVO;
    }


    /**
     * 验证文件名称：仅包含 汉字、字母、数字、下划线和点号
     *
     * @param fileName 文件名称
     * @return 返回true表示符合要求
     */
    private boolean validateFileName(String fileName) {
        String regex = "^[a-zA-Z0-9_\\u4e00-\\u9fa5_\\._\\-]+$";
        return fileName.matches(regex);
    }
}
