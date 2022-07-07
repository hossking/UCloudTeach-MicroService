package cn.gpnusz.userweb.controller;

import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.StudentInfo;
import cn.gpnusz.ucloudteachentity.req.StuResetPasswordReq;
import cn.gpnusz.ucloudteachentity.req.StudentInfoQueryReq;
import cn.gpnusz.ucloudteachentity.req.StudentInfoSaveReq;
import cn.gpnusz.userinterface.service.StudentInfoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author h0ss
 * @description 学员信息API[管理员]
 * @date 2021/11/12 14:31
 */
@RestController
@RequestMapping("/api/admin/stu")
public class StuController {

    @DubboReference(version = "1.0.0")
    private StudentInfoService studentInfoService;

    /**
     * 展示所有学员信息数据
     *
     * @param studentInfoQueryReq : 请求参数（分页参数）
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.resp.StudentInfo>>
     * @author h0ss
     */
    @GetMapping("/list")
    public CommonResp<PageResp<StudentInfo>> list(@Valid StudentInfoQueryReq studentInfoQueryReq) {
        CommonResp<PageResp<StudentInfo>> resp = new CommonResp<>();
        resp.setMessage("数据获取成功!");
        resp.setContent(studentInfoService.getAll(studentInfoQueryReq));
        return resp;
    }

    /**
     * 按传入条件查询学员信息
     *
     * @param studentInfoQueryReq : 学员信息查询参数
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<cn.gpnusz.ucloudteachentity.common.PageResp<cn.gpnusz.ucloudteachentity.resp.StudentInfo>>
     * @author h0ss
     */
    @GetMapping("/search")
    public CommonResp<PageResp<StudentInfo>> search(@Valid StudentInfoQueryReq studentInfoQueryReq) {
        CommonResp<PageResp<StudentInfo>> resp = new CommonResp<>();
        resp.setMessage("数据获取成功!");
        resp.setContent(studentInfoService.getAllByCondition(studentInfoQueryReq));
        return resp;
    }

    /**
     * 新增/编辑学员信息
     *
     * @param studentInfoSaveReq : 保存的学员信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody StudentInfoSaveReq studentInfoSaveReq) {
        studentInfoService.save(studentInfoSaveReq);
        return new CommonResp<>();
    }

    /**
     * 重置密码接口
     *
     * @param stuResetPasswordReq : 重置密码学员信息
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/resetPassword")
    public CommonResp<Object> resetPassword(@Valid @RequestBody StuResetPasswordReq stuResetPasswordReq) {
        studentInfoService.resetPassword(stuResetPasswordReq);
        return new CommonResp<>();
    }

    /**
     * 根据id删除学员信息
     *
     * @param id : 学员id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        studentInfoService.delete(id);
        return new CommonResp<>();
    }

    /**
     * 解除对学员封禁的接口
     *
     * @param id : 学员id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PutMapping("/enable/{id}")
    public CommonResp<Object> enable(@PathVariable Long id) {
        studentInfoService.banStudent(id, Boolean.FALSE);
        return new CommonResp<>();
    }

    /**
     * 封禁学员的接口
     *
     * @param id : 学员id
     * @return : cn.gpnusz.ucloudteachentity.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PutMapping("/disable/{id}")
    public CommonResp<Object> disable(@PathVariable Long id) {
        studentInfoService.banStudent(id, Boolean.TRUE);
        return new CommonResp<>();
    }
}
