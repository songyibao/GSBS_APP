package com.songyb.bs.classes;

public class grade {
    private String credit;
    private String gradeScore;
    private String name;
    private String score;
    private String jxb_id;
    private int term;
    private int year;

    public void setJxb_id(String jxb_id) {
        this.jxb_id = jxb_id;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public void setGradeScore(String gradeScore) {
        this.gradeScore = gradeScore;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getJxb_id() {
        return jxb_id;
    }

    public String getCredit() {
        return credit;
    }

    public String getGradeScore() {
        return gradeScore;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }

    public int getTerm() {
        return term;
    }

    public int getYear() {
        return year;
    }
}
