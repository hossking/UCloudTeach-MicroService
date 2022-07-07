package cn.gpnusz.ucloudteachentity.resp;


import cn.gpnusz.ucloudteachentity.entity.Question;

/**
 * @author h0ss
 * @description 公共的试题信息，屏蔽答案/解析等字段
 * @date 2021/12/1 - 23:01
 */
public class CommonQuestionResp extends Question {
    private static final Long serialVersionUID = 885112889112256165L;

    @Override
    public void setAnswerText(String answerText) {
        super.setAnswerText(null);
    }

    @Override
    public void setAnalysis(String analysis) {
        super.setAnalysis(null);
    }

    @Override
    public void setAnswerOption(String answerOption) {
        super.setAnswerOption(null);
    }

}
