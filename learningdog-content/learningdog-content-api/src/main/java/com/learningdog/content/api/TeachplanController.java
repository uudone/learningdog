package com.learningdog.content.api;

import com.learningdog.content.model.dto.BindTeachplanMediaDto;
import com.learningdog.content.model.dto.SaveTeachplanDto;
import com.learningdog.content.model.dto.TeachplanTreeDto;
import com.learningdog.content.service.TeachplanMediaService;
import com.learningdog.content.service.TeachplanService;
import com.learningdog.media.po.MediaFiles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 课程计划 前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@Api("课程计划接口")
@RequestMapping("/teachplan")
public class TeachplanController {

    @Resource
    private TeachplanService  teachplanService;

    @Resource
    TeachplanMediaService teachplanMediaService;

    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/{courseId}/tree-nodes")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType = "Long",paramType = "path")
    public List<TeachplanTreeDto> getTreeNodes(@PathVariable("courseId") Long courseId){
        return teachplanService.getTreeNodes(courseId);
    }

    @ApiOperation("新增或修改课程计划")
    @PostMapping
    public void saveTeachplan(@RequestBody @Validated SaveTeachplanDto saveTeachplanDto){
        teachplanService.saveTeachplan(saveTeachplanDto);
    }

    @ApiOperation("课程计划向下移动")
    @PostMapping("/movedown/{teachplanId}")
    public void movedownTeachplan(@PathVariable("teachplanId")Long teachplanId){
        teachplanService.movedownTeachplan(teachplanId);
    }

    @ApiOperation("课程计划向上移动")
    @PostMapping("/moveup/{teachplanId}")
    public void moveupTeachplan(@PathVariable("teachplanId")Long teachplanId){
        teachplanService.moveupTeachplan(teachplanId);
    }

    @ApiOperation("删除课程计划")
    @DeleteMapping("{teachplanId}")
    public void deleteTeachplan(@PathVariable("teachplanId")Long teachplanId){
        teachplanService.deleteTeachplanById(teachplanId);
    }

    @ApiOperation("课程计划和媒资信息绑定")
    @PostMapping("/association/media")
    public void associationMedia(@RequestBody @Validated BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanMediaService.associationMedia(bindTeachplanMediaDto);
    }

    @ApiOperation("删除课程计划和媒资信息的绑定信息")
    @DeleteMapping("/association/media/{teachplanId}/{mediaId}")
    public void deleteAssociationMedia(@PathVariable("teachplanId")Long teachplanId,
                                       @PathVariable("mediaId")String mediaId){
        teachplanMediaService.deleteAssociationMedia(teachplanId,mediaId);
    }


}
