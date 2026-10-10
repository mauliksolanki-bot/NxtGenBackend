-- Adds a configurable title to popup/confirmation dialogs so the frontend
-- can render both a title and message from NXTGEN_POPUP_CONFIG metadata.
ALTER TABLE NXTGEN_POPUP_CONFIG
    ADD COLUMN TITLE VARCHAR(255) NULL AFTER POPUP_NAME;

UPDATE NXTGEN_POPUP_CONFIG
SET TITLE = 'Delete User'
WHERE POPUP_NAME = 'DELETE_USER_CONFIRM';
