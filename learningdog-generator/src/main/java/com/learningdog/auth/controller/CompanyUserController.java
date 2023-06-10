package com.learningdog.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.learningdog.auth.service.CompanyUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@RequestMapping("companyUser")
public class CompanyUserController {

    @Autowired
    private CompanyUserService  companyUserService;
}