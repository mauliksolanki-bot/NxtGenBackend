-- Generic popup/confirmation-dialog configuration table, mirroring the
-- metadata-driven grid/form pattern so confirmation messages (e.g. delete
-- warnings) are configurable rather than hard-coded in the frontend.
CREATE TABLE NXTGEN_POPUP_CONFIG (
                                     ID           BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     POPUP_NAME   VARCHAR(255) NOT NULL,
                                     DISPLAY_MSG  VARCHAR(255) NOT NULL,
                                     IS_ENABLED   CHAR(1)      DEFAULT 'Y',
                                     CRE_BY       VARCHAR(255),
                                     CRE_DATE     TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
                                     CONSTRAINT UQ_NXTGEN_POPUP_CONFIG_NAME UNIQUE (POPUP_NAME)
);

INSERT INTO NXTGEN_POPUP_CONFIG (POPUP_NAME, DISPLAY_MSG, IS_ENABLED, CRE_BY)
VALUES ('DELETE_USER_CONFIRM', 'Are you sure you want to delete this user?', 'Y', 'SYSTEM');
