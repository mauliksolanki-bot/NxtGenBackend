-- Schema support for the forced password-reset flow:
--   * NXTGEN_USERS_MASTER.PASSWORD_RESET_REQUIRED flags users who must set a new
--     password at their next login (existing users default to 'N').
--   * NXTGEN_FORM.SUBMIT_LABEL lets a form definition configure its own submit
--     button text (NULL keeps the label supplied by the page).
ALTER TABLE NXTGEN_USERS_MASTER
    ADD COLUMN PASSWORD_RESET_REQUIRED CHAR(1) NOT NULL DEFAULT 'N';

ALTER TABLE NXTGEN_FORM
    ADD COLUMN SUBMIT_LABEL VARCHAR(100) NULL;
