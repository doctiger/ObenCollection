package com.obenproto.oben.api.response;

import com.obenproto.oben.api.domain.AvatarInfo;

import java.util.ArrayList;

public class GetAllUserAvatarsResponse extends ArrayList<AvatarInfo> {
    public AvatarInfo getAvatar(int mode) {
        for (AvatarInfo item : this) {
            if (item.Avatar.modeId == mode) {
                return item;
            }
        }
        return null;
    }
}
