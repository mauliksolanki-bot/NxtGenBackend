-- Tracks consecutive failed logins; the account is locked after 3 in a row.
ALTER TABLE NXTGEN_USERS_MASTER
    ADD COLUMN FAILED_LOGIN_ATTEMPTS INT NOT NULL DEFAULT 0;
