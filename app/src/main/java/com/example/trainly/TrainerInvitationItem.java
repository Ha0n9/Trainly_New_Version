package com.example.trainly;

public class TrainerInvitationItem {
    public int invitationId;
    public int trainerId;
    public String trainerName;
    public String trainerEmail;

    public TrainerInvitationItem(int invitationId, int trainerId, String trainerName, String trainerEmail) {
        this.invitationId = invitationId;
        this.trainerId = trainerId;
        this.trainerName = trainerName;
        this.trainerEmail = trainerEmail;
    }
}