package model;

import config.BTOApplicationStatus;
import config.FlatType;

public class BTOApplication {
    private final User applicant;
    private final BTOProject btoProject;
    private final FlatType flatType;
    private BTOApplicationStatus btoApplicationStatus = BTOApplicationStatus.PENDING;

    public BTOApplication(User applicant, BTOProject btoProject, FlatType flatType){
        this.applicant = applicant;
        this.btoProject = btoProject;
        this.flatType = flatType;
    }

    public User getApplicant() {
        return applicant;
    }

    public BTOProject getBtoProject() {
        return btoProject;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public BTOApplicationStatus getBtoApplicationStatus() {
        return btoApplicationStatus;
    }
}