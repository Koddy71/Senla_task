package com.sen.dto.request;

import jakarta.validation.constraints.NotNull;

public class CreateDialogRequest {
    @NotNull(message = "Требуется идентификатор участника")
    private String participantLogin;

    public CreateDialogRequest() {
    }

    public String getParticipantLogin() {
        return participantLogin;
    }

    public void setParticipantLogin(String participantLogin) {
        this.participantLogin = participantLogin;
    }
}