package com.obenproto.oben.response;

/**
 * Created by Petro Rington on 12/5/2015.
 */
public class UserAvatar {

    private int avatarId;
    private int activation;
    private String recordURL;
    private String message;
    private String status;

    public int getAvatarId() {
        return avatarId;
    }

    public int getActivation() {
        return activation;
    }

    public String getRecordURL() {
        return recordURL;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "UserAvatar{" +
                "avatarId=" + avatarId +
                ", recordURL='" + recordURL + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

