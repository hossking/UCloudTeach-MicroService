package cn.gpnusz.ucloudteachentity.resp;


import cn.gpnusz.ucloudteachentity.entity.ExamInfo;

/**
 * @author h0ss
 * @description 封装完整考试信息的类
 * @date 2021/11/20 - 17:16
 */
public class ExamCust extends ExamInfo {
    private static final Long serialVersionUID = 9289525334561265L;

    private Integer totalScore;

    private String studentName;

    private String paperName;

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    @Override
    public String toString() {
        return "ExamCust{" +
                "totalScore=" + totalScore +
                ", studentName='" + studentName + '\'' +
                ", paperName='" + paperName + '\'' +
                "} " + super.toString();
    }
}
