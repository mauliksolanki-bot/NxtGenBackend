-- Warehouse-style users master data table (bulk demo/seed dataset), separate
-- from the live NXTGEN_USERS_MASTER authentication table. LAST_NM and
-- EMAIL_ADDRESS are each unique so no surname or email repeats across the
-- 50,000 seeded records.
CREATE TABLE NXTGEN_USERS_MASTER_DATA_WH (
                                             ID             BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             FIRST_NM       VARCHAR(255) NOT NULL,
                                             LAST_NM        VARCHAR(255) NOT NULL,
                                             EMAIL_ADDRESS  VARCHAR(255) NOT NULL,
                                             IS_ACTIVE      CHAR(1) DEFAULT 'Y',
                                             CONSTRAINT UQ_NXTGEN_USERS_MDW_LAST_NM UNIQUE (LAST_NM),
                                             CONSTRAINT UQ_NXTGEN_USERS_MDW_EMAIL UNIQUE (EMAIL_ADDRESS)
);
