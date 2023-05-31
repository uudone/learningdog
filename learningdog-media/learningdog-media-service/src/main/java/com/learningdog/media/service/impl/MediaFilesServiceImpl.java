package com.learningdog.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.learningdog.base.code.ObjectAuditStatus;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.base.model.PageParams;
import com.learningdog.base.model.PageResult;
import com.learningdog.base.model.RestResponse;
import com.learningdog.media.mapper.MediaFilesMapper;
import com.learningdog.media.mapper.MediaProcessMapper;
import com.learningdog.media.model.dto.QueryMediaParamsDto;
import com.learningdog.media.model.dto.UploadFileParamsDto;
import com.learningdog.media.model.dto.UploadFileResultDto;
import com.learningdog.media.model.po.MediaFiles;
import com.learningdog.media.model.po.MediaProcess;
import com.learningdog.media.service.MediaFilesService;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 媒资信息 服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class MediaFilesServiceImpl extends ServiceImpl<MediaFilesMapper, MediaFiles> implements MediaFilesService {

    @Resource
    MediaFilesService mediaFilesService;
    @Resource
    MediaFilesMapper mediaFilesMapper;
    @Resource
    MediaProcessMapper mediaProcessMapper;

    @Value("${minio.bucket.files}")
    private String bucket_files;

    @Value("${minio.bucket.videofiles}")
    private String bucket_video;

    @Resource
    MinioClient minioClient;

    @Override
    public PageResult<MediaFiles> list(Long companyId, PageParams params, QueryMediaParamsDto queryMediaParamsDto) {
        String filename=queryMediaParamsDto.getFilename();
        String fileType=queryMediaParamsDto.getFileType();
        String auditStatus=queryMediaParamsDto.getAuditStatus();
        //构建查询条件
        LambdaQueryWrapper<MediaFiles> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(MediaFiles::getCompanyId,companyId);
        queryWrapper.like(StringUtils.isNotEmpty(filename),MediaFiles::getFilename,filename);
        queryWrapper.eq(StringUtils.isNotEmpty(fileType),MediaFiles::getFileType,fileType);
        queryWrapper.eq(StringUtils.isNotEmpty(auditStatus),MediaFiles::getAuditStatus,auditStatus);
        //创建分页参数
        Page<MediaFiles> page=new Page<>(params.getPageNo(),params.getPageSize());
        Page<MediaFiles> result = mediaFilesMapper.selectPage(page, queryWrapper);
        //封装查询结果
        PageResult<MediaFiles> pageResult=new PageResult<>(result.getRecords(),
                                                        result.getTotal(),
                                                        params.getPageNo(),
                                                        params.getPageSize());
        return pageResult;
    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto paramsDto, String localFilePath) {
        File file=new File(localFilePath);
        if (!file.exists()){
            LearningdogException.cast("文件不存在");
        }
        //获取文件名
        String fileName = paramsDto.getFilename();
        //获取文件扩展名
        String extension=fileName.substring(fileName.lastIndexOf("."));
        //获取文件mimeType
        String mimeType = getFileMimeType(extension);
        //获取文件md5值
        String md5=getFileMd5(file);
        //获取文件在minio中的目录
        String defaultFolderPath = getDefaultFolderPath();
        //获取文件在minio中的对象名
        String objectName=defaultFolderPath+"/"+md5+extension;
        //将文件上传到minio
        boolean flag=addMediaFileToMinio(localFilePath,bucket_files,mimeType,objectName);
        if (!flag){
            LearningdogException.cast("文件上传失败");
        }
        //将文件信息保存到数据库中
        paramsDto.setFileSize(file.length());
        MediaFiles mediaFiles=mediaFilesService.addMediaFileToDb(companyId,md5,paramsDto,bucket_files,objectName);
        //封装返回数据
        UploadFileResultDto resultDto=new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles,resultDto);
        return resultDto;
    }


    /**
     * @param :
     * @return String
     * @author getjiajia
     * @description 获取文件存储的文件夹
     */
    private String getDefaultFolderPath(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String path=format.format(new Date()).replace("-","/")+"/";
        return path;
    }

    /**
     * @param file:
     * @return String
     * @author getjiajia
     * @description 获取文件md5值
     */
    private String getFileMd5(File file){
        try(FileInputStream inputStream=new FileInputStream(file)){
            String md5= DigestUtils.md5Hex(inputStream);
            return md5;
        } catch (Exception e) {
            LearningdogException.cast("文件存储失败");
        }
        return null;
    }

    /**
     * @param extension:
     * @return String
     * @author getjiajia
     * @description 根据文件扩展名获取文件mimeType
     */
    private String getFileMimeType(String extension){
        //判断扩展名是否为空
        if (extension==null){
            extension="";
        }
        //根据文件扩展名获取mimeType
        ContentInfo contentInfo= ContentInfoUtil.findExtensionMatch(extension);
        String mimeType= MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用字节流
        if(contentInfo!=null){
            mimeType=contentInfo.getMimeType();
        }
        return mimeType;
    }

    @Override
    public boolean addMediaFileToMinio(String localFilePath,String bucket,String mimeType,String objectName){
        try{
            UploadObjectArgs args=UploadObjectArgs.builder()
                    .bucket(bucket)
                    .filename(localFilePath)
                    .object(objectName)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(args);
            return true;
        }catch (Exception e){
            log.error("上传文件到minio失败:{}",bucket+"/"+objectName);
            return false;
        }
    }
    @Override
    @Transactional
    public MediaFiles addMediaFileToDb(Long companyId,String md5,UploadFileParamsDto paramsDto,String bucket,String objectName){
        //查询该文件是否已存在
        MediaFiles mediaFiles=mediaFilesMapper.selectById(md5);
        if (mediaFiles==null){
            mediaFiles=new MediaFiles();
            BeanUtils.copyProperties(paramsDto,mediaFiles);
            mediaFiles.setId(md5);
            mediaFiles.setFileId(md5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/"+bucket+"/"+objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus(ObjectAuditStatus.SUCCESS);
            mediaFiles.setStatus("1");
            int insert=mediaFilesMapper.insert(mediaFiles);
            if (insert<=0){
                LearningdogException.cast("保存文件信息到数据库失败");
            }
            log.debug("保存文件信息到数据库成功,fileMd5:{}",md5);
            //将文件信息插入待处理任务表
            addWaitingTask(mediaFiles);

        }
        return mediaFiles;
    }

    @Override
    public RestResponse<Boolean> checkfile(String fileMd5) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles!=null){
            String bucket=mediaFiles.getBucket();
            String filePath=mediaFiles.getFilePath();
            //从minio中读取文件
            InputStream inputStream=null;
            try{
                inputStream=minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(filePath)
                                .build()
                );
                if (inputStream!=null){
                    return RestResponse.success(true);
                }
            }catch (Exception e){
                log.debug("checkfile从minio中读取文件失败{}",e.getMessage());
            }
        }
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkchunk(String fileMd5, Integer chunkIndex) {
        //获取分块目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //获取分块路径
        String chunkFilePath=chunkFileFolderPath+chunkIndex;
        //从minio中读取文件
        InputStream inputStream=null;
        try{
            inputStream=minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket_video)
                            .object(chunkFilePath)
                            .build()
            );
            if (inputStream!=null){
                return RestResponse.success(true);
            }
        }catch (Exception e){

        }
        return RestResponse.success(false);

    }

    @Override
    public RestResponse uploadchunk(String fileMd5, int chunkIndex,String localFilePath) {
        //获取分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //获取分块文件路径
        String chunkFilePath=chunkFileFolderPath+chunkIndex;
        //将分块文件上传到minio
        boolean upload= addMediaFileToMinio(localFilePath,bucket_video,getFileMimeType(null),chunkFilePath);
        if (!upload){
            return RestResponse.validFail(false,"上传文件分块失败");
        }
        return RestResponse.success(true);
    }

    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto paramsDto) {
        //获取文件分块目录
        String chunkFileFolderPath=getChunkFileFolderPath(fileMd5);
        //获取文件扩展名
        String extension=paramsDto.getFilename().substring(paramsDto.getFilename().lastIndexOf("."));
        //获取合并文件目录
        String mergeFilePath=getMergeFilePath(fileMd5,extension);
        //合并分块文件
        List<ComposeSource> sources=Stream.iterate(0,i->++i)
                .limit(chunkTotal)
                .map(i->ComposeSource.builder()
                        .bucket(bucket_video)
                        .object(chunkFileFolderPath.concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());

        ComposeObjectArgs composeObjectArgs=ComposeObjectArgs.builder()
                .bucket(bucket_video)
                .object(mergeFilePath)
                .sources(sources)
                .build();
        try {
            minioClient.composeObject(composeObjectArgs);
            log.debug("合并文件成功,fileMd5:{}",fileMd5);
        } catch (Exception e){
            log.debug("合并文件失败,fileMd5:{},异常:{}",fileMd5,e.getMessage());
            return RestResponse.validFail(false,"合并文件分块失败");
        }
        //验证md5值
        //下载合并后的文件
        File minioFile=downloadFileFromMinio(bucket_video,mergeFilePath);
        if (minioFile==null){
            log.debug("下载合并后文件失败,mergeFilePath:{}",mergeFilePath);
            RestResponse.validFail(false,"下载合并后文件失败。");
        }
        try(InputStream inputStream=new FileInputStream(minioFile)){
            String downloadMd5 = DigestUtils.md5Hex(inputStream);
            if(!downloadMd5.equals(fileMd5)){
                return RestResponse.validFail(false,"文件合并校验失败，最终上传失败。");
            }
            paramsDto.setFileSize(minioFile.length());
            log.debug("校验文件成功,fileMd5:{}",fileMd5);
        }catch (Exception e){
            log.debug("校验文件失败,fileMd5:{},异常:{}",fileMd5,e.getMessage());
            return RestResponse.validFail(false,"文件合并校验失败，最终上传失败。");
        }finally {
            if (minioFile!=null){
                minioFile.delete();
            }
        }
        //文件信息保存到数据库
        mediaFilesService.addMediaFileToDb(companyId,fileMd5,paramsDto,bucket_video,mergeFilePath);
        //清除分块文件
        clearChunkFiles(chunkFileFolderPath,chunkTotal);
        return RestResponse.success(true);
    }



    @Override
    public File downloadFileFromMinio(String bucket,String objectName){
        File minioFile= null;
        InputStream inputStream=null;
        OutputStream outputStream=null;
        try {
            inputStream=minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
            //临时文件
            minioFile = File.createTempFile("minio","merge");
            outputStream=new FileOutputStream(minioFile);
            IOUtils.copy(inputStream,outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }  finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return minioFile;
    }


    /**
     * @param fileMd5:
     * @return String
     * @author getjiajia
     * @description 根据md5值获取分块文件目录
     */
    private String getChunkFileFolderPath(String fileMd5){
        return fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/chunk/";
    }

    /**
     * @param fileMd5:
     * @param extension:
     * @return String
     * @author getjiajia
     * @description 获取合并后文件目录
     */
    private String getMergeFilePath(String fileMd5,String extension){
        return fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/"+fileMd5+extension;
    }

    /**
     * @param chunkFileFolderPath:
     * @param chunkTotal:
     * @return void
     * @author getjiajia
     * @description 清除分块文件
     */
    private void clearChunkFiles(String chunkFileFolderPath,int chunkTotal){
        List<DeleteObject> deleteObjects= Stream.iterate(0,i->++i)
                .limit(chunkTotal)
                .map(i->new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                .collect(Collectors.toList());

        RemoveObjectsArgs removeObjectsArgs=RemoveObjectsArgs.builder()
                .bucket(bucket_video)
                .objects(deleteObjects)
                .build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        results.forEach(
                r->{
                    DeleteError deleteError=null;
                    try {
                        deleteError=r.get();
                    } catch (Exception e) {
                        log.error("清除文件分块失败,objectname:{}",deleteError.objectName(),e);
                    }
                }
        );
        log.debug("清除文件分块{}成功",chunkFileFolderPath);
    }

    /**
     * @param mediaFiles:
     * @return void
     * @author getjiajia
     * @description 添加待处理任务
     */
    private void addWaitingTask(MediaFiles mediaFiles){
        //获取文件扩展名
        String extension=mediaFiles.getFilename().substring(mediaFiles.getFilename().lastIndexOf("."));
        //获取文件类型
        String mimeType=getFileMimeType(extension);
        //如果是avi或flv类型视频加入到视频待处理表
        if("video/x-msvideo".equals(mimeType)||"video/x-flv".equals(mimeType)){
            MediaProcess mediaProcess=new MediaProcess();
            BeanUtils.copyProperties(mediaFiles,mediaProcess);
            mediaProcess.setStatus("1");
            mediaProcess.setFailCount(0);
            mediaProcess.setCreateDate(LocalDateTime.now());
            int insert = mediaProcessMapper.insert(mediaProcess);
            if (insert<=0){
                LearningdogException.cast("将待处理文件信息插入到数据库中失败");
            }
            log.debug("将待处理文件信息插入到数据库中成功：{}",mediaProcess.getFilePath());
        }
    }
}
