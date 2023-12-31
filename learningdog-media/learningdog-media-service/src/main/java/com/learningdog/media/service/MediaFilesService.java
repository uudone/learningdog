package com.learningdog.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.base.model.PageParams;
import com.learningdog.base.model.PageResult;
import com.learningdog.base.model.RestResponse;
import com.learningdog.media.model.dto.QueryMediaParamsDto;
import com.learningdog.media.model.dto.UploadFileParamsDto;
import com.learningdog.media.model.dto.UploadFileResultDto;
import com.learningdog.media.po.MediaFiles;

import java.io.File;

/**
 * <p>
 * 媒资信息 服务类
 * </p>
 *
 * @author getjiajia
 * @since 2023-05-27
 */
public interface MediaFilesService extends IService<MediaFiles> {

    /**
     * @param companyId:
     * @param params:
     * @param queryMediaParamsDto:
     * @return PageResult<MediaFiles>
     * @author getjiajia
     * @description 分页查询符合条件的媒资文件
     */
    PageResult<MediaFiles> list(Long companyId, PageParams params, QueryMediaParamsDto queryMediaParamsDto);


    /**
     * @param companyId:
     * @param paramsDto:
     * @param localFilePath:
     * @return UploadFileResultDto
     * @author getjiajia
     * @description 上传文件
     */
    UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto paramsDto,String localFilePath,String objectName);


    /**
     * @param localFilePath:
     * @param bucket:
     * @param mimeType:
     * @param objectName:
     * @return boolean
     * @author getjiajia
     * @description 上传文件到minio
     */
    public boolean addMediaFileToMinio(String localFilePath,String bucket,String mimeType,String objectName);

    /**
     * @param companyId:
     * @param md5:
     * @param paramsDto:
     * @param bucket:
     * @param objectName:
     * @return boolean
     * @author getjiajia
     * @description 将文件信息保存到数据库
     */
    MediaFiles addMediaFileToDb(Long companyId,String md5,UploadFileParamsDto paramsDto,String bucket,String objectName);


    /**
     * @param bucket:
     * @param objectName:
     * @return File
     * @author getjiajia
     * @description 从minio中下载文件
     */
    public File downloadFileFromMinio(String bucket, String objectName);

    /**
     * @param fileMd5:
     * @return RestResponse<Boolean>
     * @author getjiajia
     * @description 检查文件是否存在
     */
    RestResponse<Boolean> checkfile(String fileMd5);

    /**
     * @param fileMd5:
     * @param chunkIndex:
     * @return RestResponse<Boolean>
     * @author getjiajia
     * @description 检查文件分块是否存在
     */
    RestResponse<Boolean> checkchunk(String fileMd5,Integer chunkIndex);

    /**
     * @param fileMd5:
     * @param chunkIndex:
     * @param localFilePath:
     * @return RestResponse
     * @author getjiajia
     * @description 上传分块到minio
     */
    RestResponse uploadchunk(String fileMd5,int chunkIndex,String localFilePath);

    /**
     * @param companyId:
     * @param fileMd5:
     * @param chunkTotal:
     * @param paramsDto:
     * @return RestResponse
     * @author getjiajia
     * @description 合并文件分块
     */
    RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto paramsDto);


    /**
     * @param fileId:
     * @param status:
     * @return int
     * @author getjiajia
     * @description 设置文件状态
     */
    int setFileStatus(String fileId,String status);

    /**
     * @param companyId:
     * @param mediaFilesId:
     * @return MediaFiles
     * @author getjiajia
     * @description 获取媒资文件信息
     */
    MediaFiles getMediaFiles(Long companyId, String mediaFilesId);

    /**
     * @param mediaId:
     * @return MediaFiles
     * @author getjiajia
     * @description 获取媒资文件信息
     */
    MediaFiles getMediaFiles(String mediaId);

    /**
     * @param companyId:
     * @param fileMd5:
     * @return void
     * @author getjiajia
     * @description 删除媒资文件信息
     */
    void deleteMediaFile(Long companyId, String fileMd5);

    /**
     * @param companyId:
     * @param filePath:
     * @return void
     * @author getjiajia
     * @description 删除课程静态文件信息
     */
    void deleteCourseHtml(Long companyId, String filePath);
}
