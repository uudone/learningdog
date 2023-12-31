package com.learningdog.media.api;

import com.learningdog.base.code.ResourcesType;
import com.learningdog.base.model.PageParams;
import com.learningdog.base.model.PageResult;
import com.learningdog.base.model.RestResponse;
import com.learningdog.media.model.dto.QueryMediaParamsDto;
import com.learningdog.media.model.dto.UploadFileParamsDto;
import com.learningdog.media.model.dto.UploadFileResultDto;
import com.learningdog.media.po.MediaFiles;
import com.learningdog.media.service.MediaFilesService;
import com.learningdog.media.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * <p>
 * 媒资信息 前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
public class MediaFilesController {

    @Resource
    private MediaFilesService  mediaFilesService;

    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams params,@RequestBody QueryMediaParamsDto queryMediaParamsDto){
        Long companyId=SecurityUtils.getCompanyId();
        return mediaFilesService.list(companyId,params,queryMediaParamsDto);
    }

    @ApiOperation(value = "上传文件接口",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequestMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResultDto uploadFile(@RequestPart("filedata")MultipartFile multipartFile,
                                          @RequestParam(value = "folder",required = false)String folder,
                                          @RequestParam(value = "objectName",required = false)String objectName) throws IOException {
        Long companyId=SecurityUtils.getCompanyId();
        UploadFileParamsDto paramsDto=new UploadFileParamsDto();
        if (StringUtils.isEmpty(objectName)){//如果为空，则是图片类型
            paramsDto.setFileType(ResourcesType.PICTURE);
        }else {//不为空则为其他类型
            paramsDto.setFileType(ResourcesType.OTHER);
        }
        paramsDto.setFilename(multipartFile.getOriginalFilename());//文件原名称
        //创建临时文件
        File tempFile=null;
        UploadFileResultDto resultDto;
        try{
            tempFile=File.createTempFile("minio","temp");
            multipartFile.transferTo(tempFile);
            //文件路径
            String absolutePath=tempFile.getAbsolutePath();
            //上传文件
            resultDto= mediaFilesService.uploadFile(companyId,paramsDto,absolutePath,objectName);
        }finally {
            //删除临时文件
            if (tempFile!=null)
                tempFile.delete();
        }
        return resultDto;
    }

    @ApiOperation("文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(@RequestParam("fileMd5") String fileMd5){
        return mediaFilesService.checkfile(fileMd5);
    }

    @ApiOperation("分块文件上传前检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5")String fileMd5,
                                            @RequestParam("chunk")Integer chunk){
        return mediaFilesService.checkchunk(fileMd5,chunk);
    }

    @ApiOperation("上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5")String fileMd5,
                                    @RequestParam("chunk")Integer chunk)throws IOException{
        File tempFile=null;
        try {
            //创建临时文件
            tempFile=File.createTempFile("minio","temp");
            //上传的文件拷贝到临时文件夹
            file.transferTo(tempFile);
            //文件路径
            String absolutePath = tempFile.getAbsolutePath();

            return mediaFilesService.uploadchunk(fileMd5, chunk, absolutePath);
        } finally {
            if (tempFile!=null)
                tempFile.delete();
        }

    }

    @ApiOperation("合并分块文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergechunks(@RequestParam("fileMd5")String fileMd5,
                                    @RequestParam("fileName")String fileName,
                                    @RequestParam("chunkTotal")Integer chunkTotal){
        Long companyId= SecurityUtils.getCompanyId();
        UploadFileParamsDto paramsDto=new UploadFileParamsDto();
        paramsDto.setFileType(ResourcesType.VIDEO);
        paramsDto.setTags("课程视频");
        paramsDto.setRemark("");
        paramsDto.setFilename(fileName);
        return mediaFilesService.mergechunks(companyId,fileMd5,chunkTotal,paramsDto);
    }

    @ApiOperation("根据id查询媒资文件信息")
    @GetMapping("/files/{mediaFilesId}")
    public MediaFiles getMediaFiles(@PathVariable("mediaFilesId")String mediaFilesId){
        Long companyId=SecurityUtils.getCompanyId();
        return mediaFilesService.getMediaFiles(companyId,mediaFilesId);
    }

    @ApiOperation("删除媒资文件信息")
    @DeleteMapping("/{fileMd5}")
    @PreAuthorize("hasAuthority('lg_teachmanager_course')")
    public void deleteMediaFile(@PathVariable("fileMd5")String fileMd5){
        Long companyId=SecurityUtils.getCompanyId();
        mediaFilesService.deleteMediaFile(companyId,fileMd5);
    }

    @ApiOperation("删除静态课程文件信息")
    @DeleteMapping("/courseHtml")
    @PreAuthorize("hasAuthority('lg_teachmanager_course')")
    public void deleteCourseHtml(String filePath){
        Long companyId=SecurityUtils.getCompanyId();
        mediaFilesService.deleteCourseHtml(companyId,filePath);
    }

}
