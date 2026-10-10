package com.nxtgen.api.service;

import org.springframework.stereotype.Service;

import com.nxtgen.api.dto.PopupConfigResponse;
import com.nxtgen.api.entity.PopupConfigEntity;
import com.nxtgen.api.exception.PopupNotFoundException;
import com.nxtgen.api.repository.PopupConfigRepository;

import static com.nxtgen.api.constant.NxtGenCommonConstant.ENABLED_FLAG;

/**
 * Common endpoint support for fetching popup/confirmation-dialog metadata
 * (NXTGEN_POPUP_CONFIG), mirroring the Grid/Form config pattern.
 */
@Service
public class PopupConfigService {

    private static final String POPUP_NOT_FOUND_MESSAGE = "Popup configuration is unavailable right now.";

    private final PopupConfigRepository popupConfigRepository;

    public PopupConfigService(PopupConfigRepository popupConfigRepository) {
        this.popupConfigRepository = popupConfigRepository;
    }

    public PopupConfigResponse getPopupConfig(String popupName) {
        PopupConfigEntity popup = popupConfigRepository.findByPopupName(popupName)
                .orElseThrow(() -> new PopupNotFoundException(POPUP_NOT_FOUND_MESSAGE));

        return new PopupConfigResponse(
                popup.getId(),
                popup.getPopupName(),
                popup.getTitle(),
                popup.getDisplayMsg(),
                ENABLED_FLAG.equalsIgnoreCase(popup.getIsEnabled())
        );
    }
}
