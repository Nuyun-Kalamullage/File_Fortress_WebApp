package edu.sltc.vaadin.models;

import java.time.LocalTime;

/**
 * @author Nuyun-Kalamullage
 * @IDE IntelliJ IDEA
 * @date 23/11/2023
 * @package edu.sltc.vaadin.models
 * @project_Name File_Fortress_WebApp
 */
// Example ExamModel.java
public class ExamModel {
    private static ExamModel instance;
    public static boolean serverIsRunning = false; // Track server state
    private String moduleCode;
    private String moduleName;
    private String moduleDescription;
    private String lateSubmission;
    private LocalTime startTime;
    private LocalTime endTime;
    private String ExamPaperName;

    private ExamModel() {
        // private constructor to enforce Singleton pattern
    }

    public static ExamModel getInstance() {
        if (instance == null) {
            instance = new ExamModel();
        }
        return instance;
    }

    // Getter and setter methods for exam details
    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleDescription() {
        return moduleDescription;
    }

    public void setModuleDescription(String moduleDescription) {
        this.moduleDescription = moduleDescription;
    }

    public String getLateSubmission() {
        return lateSubmission;
    }

    public void setLateSubmission(String lateSubmission) {
        this.lateSubmission = lateSubmission;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getExamPaperName() {
        return ExamPaperName;
    }

    public void setExamPaperName(String examPaperName) {
        ExamPaperName = examPaperName;
    }

    @Override
    public String toString() {
        return "ExamModel{" +
                "moduleCode='" + moduleCode + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", moduleDescription='" + moduleDescription + '\'' +
                ", lateSubmission='" + lateSubmission + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", ExamPaperName='" + ExamPaperName + '\'' +
                '}';
    }
}

