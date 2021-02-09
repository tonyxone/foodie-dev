package com.imooc.controller.center;

import com.imooc.controller.BaseController;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.center.CenterUserBO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.resource.FileUpload;
import com.imooc.service.center.CenterUserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.DateUtil;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value="用户信息接口",tags={"用户信息相关接口"})
@RestController
@RequestMapping("userInfo")
public class CenterUserController extends BaseController {

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FileUpload fileUpload;

    @ApiOperation(value = "获取用户信息",notes = "获取用户信息",httpMethod = "POST")
    @PostMapping("update")
    public IMOOCJSONResult update(
            @ApiParam(name = "userId",value="用户id",required = true)
            @RequestParam String userId,
            @RequestBody @Valid CenterUserBO centerUserBO,
            BindingResult result,
            HttpServletRequest request,
            HttpServletResponse response){

        if(result.hasErrors()){
            Map<String,String> errorMap = getErrors(result);
            return IMOOCJSONResult.errorMap(errorMap);
        }

        Users userResult = centerUserService.updateUserInfo(userId,centerUserBO);

        //增加令牌token,会整合进redis,分布式会话
        UsersVO usersVO = conventUsersVO(userResult);

        //userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request,response,"user",
                JsonUtils.objectToJson(usersVO),true);


        return IMOOCJSONResult.ok(userResult);
    }

    private Map<String,String> getErrors(BindingResult result){

        Map<String,String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
       for(FieldError error : errorList){
           String errorField = error.getField();
           String errorMsg = error.getDefaultMessage();

           map.put(errorField,errorMsg);
       }
       return map;
    }

    private Users setNullProperty(Users userResult){

        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);

        return userResult;

    }


    @ApiOperation(value = "用户修改头像",notes = "用户修改头像",httpMethod = "POST")
    @PostMapping("uploadFace")
    public IMOOCJSONResult uploadFace(
            @ApiParam(name = "userId",value="用户id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "file",value="用户头像",required = true)
            MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response){


        //定义头像保存地址
        //String fileSpace = IMAGE_USER_FACE_LOCATION;
        String fileSpace = fileUpload.getImageUserFaceLocation();

        //在路径上为每一个用户增加userId,用于区分不同用户上传
        String uploadPathPrefix = File.separator + userId;

        if(file != null){

            FileOutputStream fileOutputStream = null;

            try {
                String fileName = file.getOriginalFilename();

                if (StringUtils.isNotBlank(fileName)) {

                    //文件重命名 immoc-face.png -> ["immoc-face","png]
                    String fileNameArr[] = fileName.split("\\.");

                    //获取文件后缀名
                    String suffix = fileNameArr[fileNameArr.length - 1];

                    if(!suffix.equalsIgnoreCase("png") &&
                            !suffix.equalsIgnoreCase("jpg") &&
                            !suffix.equalsIgnoreCase("jpeg") &&
                            !suffix.equalsIgnoreCase("gif")){
                        return IMOOCJSONResult.errorMsg("图片格式不正确");
                    }

                    //face-{userid}.png
                    //文件名重组 覆盖式长传
                    String newFileName = "face-" + userId + "." + suffix;

                    //上传头像最终保存位置
                    String finalFacePath = fileSpace + uploadPathPrefix + File.separator + newFileName;

                    uploadPathPrefix += ("/" + newFileName);

                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null) {
                        // 创建文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    InputStream inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
                }catch(IOException e){
                    e.printStackTrace();
                }finally {
                try {
                    if(fileOutputStream != null){

                        fileOutputStream.flush();
                        fileOutputStream.close();
                        }
                    }catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }else{
            return IMOOCJSONResult.errorMsg("文件不能为空");
        }

        //获得图片服务地址
        //由于浏览器可能存在缓存情况，所以我们需要加上时间戳来保证更新后的图片可以及时刷新
        String imageUrl = fileUpload.getImageServerUrl() + uploadPathPrefix
                + "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);

        //更新用户头像到数据库
        Users userResult = centerUserService.updateUsersFace(userId,imageUrl);

        //增加令牌token,会整合进redis,分布式会话
        UsersVO usersVO = conventUsersVO(userResult);

        //userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request,response,"user",
                JsonUtils.objectToJson(usersVO),true);


        return IMOOCJSONResult.ok();
    }

}
