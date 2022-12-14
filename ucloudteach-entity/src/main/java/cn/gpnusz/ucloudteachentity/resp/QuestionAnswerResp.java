package cn.gpnusz.ucloudteachentity.resp;

import java.io.Serializable;

/**
 * @author h0ss
 * @description 题目答案返回实体
 * @date 2021/12/2 - 1:10
 */
public class QuestionAnswerResp implements Serializable {
    private static final Long serialVersionUID = 315112889827456165L;

    private String answerText;

    private String answerOption;

    private String analysis;

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public String getAnswerOption() {
        return answerOption;
    }

    public void setAnswerOption(String answerOption) {
        this.answerOption = answerOption;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    @Override
    public String toString() {
        return "QuestionAnswerResp{" +
                "answerText='" + answerText + '\'' +
                ", answerOption='" + answerOption + '\'' +
                ", analysis='" + analysis + '\'' +
                '}';
    }
}
