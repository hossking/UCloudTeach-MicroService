package cn.gpnusz.ucloudteachentity.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author h0ss
 * @description 试卷批阅信息实体
 * @date 2022/4/7 - 10:27
 */
public class ExamCheck implements Serializable {
    private static final Long serialVersionUID = 76511283074506165L;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "id不能为空")
    private Long id;

    @NotNull(message = "分数不能为空")
    private Integer score;

    @NotNull(message = "正确题数不能为空")
    private Integer rightCount;

    @NotNull(message = "错误题数不能为空")
    private Integer errorCount;

    @NotNull(message = "批阅列表不能为空")
    private String checkList;

    public ExamCheck() {
    }

    public ExamCheck(Long id, Integer score, Integer rightCount, Integer errorCount, String checkList) {
        this.id = id;
        this.score = score;
        this.rightCount = rightCount;
        this.errorCount = errorCount;
        this.checkList = checkList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getRightCount() {
        return rightCount;
    }

    public void setRightCount(Integer rightCount) {
        this.rightCount = rightCount;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public String getCheckList() {
        return checkList;
    }

    public void setCheckList(String checkList) {
        this.checkList = checkList;
    }

    @Override
    public String toString() {
        return "ExamCheck{" +
                "id=" + id +
                ", score=" + score +
                ", rightCount=" + rightCount +
                ", errorCount=" + errorCount +
                ", checkList='" + checkList + '\'' +
                '}';
    }
}
