package cn.gpnusz.examweb.controller;

import cn.gpnusz.examinterface.service.ExamPaperService;
import cn.gpnusz.ucloudteachentity.common.CommonResp;
import cn.gpnusz.ucloudteachentity.common.PageResp;
import cn.gpnusz.ucloudteachentity.entity.ExamPaper;
import cn.gpnusz.ucloudteachentity.req.ExamPaperQueryReq;
import cn.gpnusz.ucloudteachentity.req.ExamPaperSaveReq;
import cn.gpnusz.ucloudteachentity.req.PaperQuestionSaveReq;
import cn.gpnusz.ucloudteachentity.resp.QuestionCust;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author h0ss
 * @description 操作试卷信息的接口
 * @date 2021/11/19 19:26
 */

@RestController
@RequestMapping("/api/admin/exam-paper")
public class ExamPaperController {

    @DubboReference(version = "1.0.0")
    private ExamPaperService examPaperService;

    /**
     * 按传入条件查询试卷信息
     *
     * @param examPaper : 查询条件对象
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<cn.gpnusz.ucloudteach.common.PageResp<cn.gpnusz.ucloudteach.entity.ExamPaper>>
     * @author h0ss
     */
    @GetMapping("/search")
    public CommonResp<PageResp<ExamPaper>> search(@Valid ExamPaperQueryReq examPaper) {
        CommonResp<PageResp<ExamPaper>> resp = new CommonResp<>();
        resp.setMessage("数据获取成功!");
        resp.setContent(examPaperService.getAllByCondition(examPaper));
        return resp;
    }

    /**
     * 新增/编辑试卷信息
     *
     * @param examPaperSaveReq : 保存的试卷信息
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ExamPaperSaveReq examPaperSaveReq) {
        examPaperService.save(examPaperSaveReq);
        return new CommonResp<>();
    }

    /**
     * 根据id删除试卷信息
     *
     * @param id : 试卷id
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        examPaperService.delete(id);
        return new CommonResp<>();
    }

    /**
     * 向试卷中添加题目/编辑题号的接口
     *
     * @param paperQuestionSaveReq : 试卷-题目关联信息
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @PostMapping("/save-question")
    public CommonResp<Object> addQuestion(@Valid @RequestBody PaperQuestionSaveReq paperQuestionSaveReq) {
        examPaperService.savePaperAndQuestion(paperQuestionSaveReq);
        return new CommonResp<>();
    }

    /**
     * 从试卷中删除id的接口
     *
     * @param id : 试卷-题目关联信息id
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.lang.Object>
     * @author h0ss
     */
    @DeleteMapping("/delete-question/{id}")
    public CommonResp<Object> deleteQuestion(@PathVariable Long id) {
        examPaperService.deletePaperAndQuestion(id);
        return new CommonResp<>();
    }

    /**
     * 根据试卷id查询试题信息
     *
     * @param id : 试卷id
     * @return : cn.gpnusz.ucloudteach.common.CommonResp<java.util.List<cn.gpnusz.ucloudteach.resp.QuestionCust>>
     * @author h0ss
     */
    @GetMapping("/query-questions")
    public CommonResp<List<QuestionCust>> getQuestions(@Valid Long id) {
        CommonResp<List<QuestionCust>> resp = new CommonResp<>();
        resp.setContent(examPaperService.getQuestions(id));
        return resp;
    }
}
