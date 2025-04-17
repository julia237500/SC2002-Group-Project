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
    private String backupSubject;

    @CSVField(index = 4)
    private String enquiry;
    private String backupEnquiry;

    @CSVField(index = 5)
    private String reply;
    private String backupReply;

    @CSVField(index = 6)
    private EnquiryStatus enquiryStatus;
    private EnquiryStatus backupEnquiryStatus;

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

    public User getEnquirer() {
        return enquirer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        backup();
        this.subject = subject;
    }

    public String getEnquiry() {
        return enquiry;
    }

    public void setEnquiry(String enquiry) {
        backup();
        this.enquiry = enquiry;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        backup();
        this.reply = reply;
        this.enquiryStatus = EnquiryStatus.REPLIED;
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

    @Override
    public String getPK() {
        return uuid;
    }

    @Override
    public void backup() {
        this.backupSubject = subject;
        this.backupEnquiry = enquiry;
        this.backupReply = reply;
        this.backupEnquiryStatus = enquiryStatus;
    }

    @Override
    public void restore() {
        this.subject = backupSubject;
        this.enquiry = backupEnquiry;
        this.reply = backupReply;
        this.enquiryStatus = backupEnquiryStatus;
    }
}
