package com.sportsCommittee.sportsinventory.Entity;

public class StudentData {

    private String studentId;
    private String dateOfIssue;

    public StudentData(){}

    public StudentData(String studentId, String dateOfIssue) {
        this.studentId = studentId;
        this.dateOfIssue = dateOfIssue;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(String dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }
}
