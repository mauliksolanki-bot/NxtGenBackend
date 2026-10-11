-- Service Request tables.

-- Running counters used to generate SR_ID (RITM0000001) and REQ_ID (REQ0000001).
CREATE TABLE IF NOT EXISTS NXTGEN_SR_SEQUENCE (
                                                  SEQ_NAME VARCHAR(30) NOT NULL,
    NEXT_VAL BIGINT      NOT NULL DEFAULT 1,
    PRIMARY KEY (SEQ_NAME)
    );

-- Service request header.
CREATE TABLE IF NOT EXISTS NXTGEN_SR_MASTER (
                                                SR_ID              VARCHAR(50)  NOT NULL,
    REQ_ID             VARCHAR(50)  NOT NULL,
    REQUEST_TYPE       VARCHAR(100) NULL,
    TITLE              VARCHAR(200) NULL,
    REQUESTED_FOR      VARCHAR(200) NULL,
    DUE_DATE           DATE         NULL,
    CI_ITEM            VARCHAR(100) NULL,
    OPENED_AT          DATETIME     NULL,
    OPENED_BY          VARCHAR(200) NULL,
    OPENED_BY_USERNAME VARCHAR(100) NULL,
    STAGE              VARCHAR(50)  NULL,
    STATE              VARCHAR(50)  NULL,
    QUANTITY           INT          NULL,
    ESTIMATED_DELIVERY DATE         NULL,
    CRE_BY             VARCHAR(100) NULL,
    CRE_DATE           DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UPD_BY             VARCHAR(100) NULL,
    UPD_DATE           DATETIME     NULL,
    PRIMARY KEY (SR_ID),
    UNIQUE KEY UQ_NXTGEN_SR_MASTER_REQ (REQ_ID)
    );

-- Description (COMMENTS) and Work Notes of a request.
CREATE TABLE IF NOT EXISTS NXTGEN_SR_COMMENTS (
                                                  ID         BIGINT      NOT NULL AUTO_INCREMENT,
                                                  SR_ID      VARCHAR(50) NOT NULL,
    REQ_ID     VARCHAR(50) NULL,
    COMMENTS   LONGTEXT    NULL,
    WORK_NOTES LONGTEXT    NULL,
    PRIMARY KEY (ID),
    INDEX IDX_NXTGEN_SR_COMMENTS_SR (SR_ID),
    CONSTRAINT FK_NXTGEN_SR_COMMENTS_SR FOREIGN KEY (SR_ID) REFERENCES NXTGEN_SR_MASTER (SR_ID)
    );

-- Approval steps raised for a request when it is submitted.
CREATE TABLE IF NOT EXISTS NXTGEN_SR_APPROVALS (
                                                   ID                  BIGINT      NOT NULL AUTO_INCREMENT,
                                                   SR_ID               VARCHAR(50) NOT NULL,
    REQ_ID              VARCHAR(50) NULL,
    APPROVAL_LEVEL      INT         NOT NULL DEFAULT 1,
    APROVAL_STATUS      VARCHAR(50) NULL,
    APPROVAL_PENDING_TO VARCHAR(50) NULL,
    PRIMARY KEY (ID),
    INDEX IDX_NXTGEN_SR_APPROVALS_SR (SR_ID),
    CONSTRAINT FK_NXTGEN_SR_APPROVALS_SR FOREIGN KEY (SR_ID) REFERENCES NXTGEN_SR_MASTER (SR_ID)
    );

-- Configures which approvals a request type goes through, in order.
-- Example: 'Licence Software Request' -> level 1 Licence Manager, level 2 IT Manager.
CREATE TABLE IF NOT EXISTS NXTGEN_SR_APPROVAL_CHECKS (
                                                         ID             BIGINT       NOT NULL AUTO_INCREMENT,
                                                         REQUEST_TYPE   VARCHAR(100) NOT NULL,
    APPROVAL_LEVEL INT          NOT NULL,
    APPROVAL_NAME  VARCHAR(50)  NOT NULL,
    APPROVER_ROLE  VARCHAR(100) NULL,
    IS_ACTIVE      VARCHAR(1)   NOT NULL DEFAULT 'Y',
    CRE_BY         VARCHAR(100) NULL,
    CRE_DATE       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    UNIQUE KEY UQ_NXTGEN_SR_APPROVAL_CHECKS (REQUEST_TYPE, APPROVAL_LEVEL)
    );

-- Drop-down values (Stage, State) for the service request form.
CREATE TABLE IF NOT EXISTS NXTGEN_SR_LOOKUP (
                                                ID            BIGINT       NOT NULL AUTO_INCREMENT,
                                                LOOKUP_TYPE   VARCHAR(30)  NOT NULL,
    LOOKUP_CODE   VARCHAR(50)  NOT NULL,
    LOOKUP_LABEL  VARCHAR(100) NOT NULL,
    DISPLAY_ORDER INT          NOT NULL DEFAULT 0,
    IS_ACTIVE     VARCHAR(1)   NOT NULL DEFAULT 'Y',
    PRIMARY KEY (ID),
    UNIQUE KEY UQ_NXTGEN_SR_LOOKUP (LOOKUP_TYPE, LOOKUP_CODE)
    );

-- Files attached to a request.
CREATE TABLE IF NOT EXISTS NXTGEN_SR_ATTACHMENTS (
                                                     ID           BIGINT       NOT NULL AUTO_INCREMENT,
                                                     SR_ID        VARCHAR(50)  NOT NULL,
    FILE_NAME    VARCHAR(255) NOT NULL,
    CONTENT_TYPE VARCHAR(100) NULL,
    FILE_SIZE    BIGINT       NOT NULL DEFAULT 0,
    FILE_DATA    LONGBLOB     NOT NULL,
    UPLOADED_BY  VARCHAR(100) NULL,
    UPLOADED_AT  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    INDEX IDX_NXTGEN_SR_ATTACHMENTS_SR (SR_ID),
    CONSTRAINT FK_NXTGEN_SR_ATTACHMENTS_SR FOREIGN KEY (SR_ID) REFERENCES NXTGEN_SR_MASTER (SR_ID)
    );
