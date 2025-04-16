package model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

import config.EnquiryStatus;

public class Enquiry implements DataModel{
    public static final Comparator<Enquiry> SORT_BY_CREATED_AT_DESC =
        Comparator.comparing(Enquiry::getCreatedAt).reversed();

    @CSVField(index = 0)
    private String uuid;

    @CSVField(index = 1, foreignKey = true)
    private BTOProject btoProject;

    @CSVField(index = 2, foreignKey = true)
    private User enquirer;

    @CSVField(index = 3)
    private String subject;

    @CSVField(index = 4)
    private String enquiry;

    @CSVField(index = 5)
    private String reply;

    @CSVField(index = 6)
    private EnquiryStatus enquiryStatus;

    @CSVField(index = 7)
    private LocalDateTime createdAt;

    @SuppressWarnings("unused")
    private Enquiry(){};

    public Enquiry(BTOProject btoProject, User enquirer, String subject, String enquiry){
        UUID uuid = UUID.randomUUID();
        this.uuid = uuid.toString();

        this.btoProject = btoProject;

        this.subject = subject;
        this.enquiry = enquiry;
        this.reply = "";

        this.enquiryStatus = EnquiryStatus.UNREPLIED;
        this.createdAt = LocalDateTime.now();

        this.enquirer = enquirer;
    }

    @Override
    public String getPK() {
        return uuid;
    }

    public User getEnquirer() {
        return enquirer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEnquiry() {
        return enquiry;
    }

    public void setEnquiry(String enquiry) {
        this.enquiry = enquiry;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
        this.enquiryStatus = EnquiryStatus.REPLIED;
    }

    public void revertReply(){
        this.reply = "";
        this.enquiryStatus = EnquiryStatus.UNREPLIED;
    }

    public BTOProject getBTOProject() {
        return btoProject;
    }

    public EnquiryStatus getEnquiryStatus() {
        return enquiryStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean canBeAltered(){
        return enquiryStatus == EnquiryStatus.UNREPLIED;
    }
}
