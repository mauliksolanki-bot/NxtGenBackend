package com.nxtgen.api.dto;

public class PopupConfigResponse {

    private final Long id;
    private final String popupName;
    private final String title;
    private final String displayMsg;
    private final boolean enabled;

    public PopupConfigResponse(Long id, String popupName, String title, String displayMsg, boolean enabled) {
        this.id = id;
        this.popupName = popupName;
        this.title = title;
        this.displayMsg = displayMsg;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public String getPopupName() {
        return popupName;
    }

    public String getTitle() {
        return title;
    }

    public String getDisplayMsg() {
        return displayMsg;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
