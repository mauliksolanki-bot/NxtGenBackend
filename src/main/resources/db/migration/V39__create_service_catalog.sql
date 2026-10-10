-- Core tables backing the Service Catalog feature: a 3-level configurable
-- catalog (Category -> Sub-Category -> Service Item), per-service-item
-- dynamic form field metadata, the submitted requests themselves, the
-- dynamic field values captured per request, attachments, and a simple
-- configurable approval workflow.

CREATE TABLE NXTGEN_SVC_CATEGORY (
                                     ID             BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     NAME           VARCHAR(150) NOT NULL,
                                     ICON           VARCHAR(10),
                                     DESCRIPTION    VARCHAR(255),
                                     DISPLAY_ORDER  INT          DEFAULT 0,
                                     IS_ACTIVE      CHAR(1)      DEFAULT 'Y',
                                     CONSTRAINT UQ_NXTGEN_SVC_CATEGORY_NAME UNIQUE (NAME)
);

CREATE TABLE NXTGEN_SVC_SUBCATEGORY (
                                        ID             BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        CATEGORY_ID    BIGINT       NOT NULL,
                                        NAME           VARCHAR(150) NOT NULL,
                                        ICON           VARCHAR(10),
                                        DESCRIPTION    VARCHAR(255),
                                        DISPLAY_ORDER  INT          DEFAULT 0,
                                        IS_ACTIVE      CHAR(1)      DEFAULT 'Y',
                                        CONSTRAINT FK_NXTGEN_SVC_SUBCATEGORY_CATEGORY FOREIGN KEY (CATEGORY_ID) REFERENCES NXTGEN_SVC_CATEGORY (ID),
                                        INDEX IDX_NXTGEN_SVC_SUBCATEGORY_CATEGORY (CATEGORY_ID)
);

CREATE TABLE NXTGEN_SVC_ITEM (
                                 ID              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 SUBCATEGORY_ID  BIGINT       NOT NULL,
                                 NAME            VARCHAR(150) NOT NULL,
                                 DESCRIPTION     VARCHAR(255),
                                 DISPLAY_ORDER   INT          DEFAULT 0,
                                 IS_ACTIVE       CHAR(1)      DEFAULT 'Y',
                                 CONSTRAINT FK_NXTGEN_SVC_ITEM_SUBCATEGORY FOREIGN KEY (SUBCATEGORY_ID) REFERENCES NXTGEN_SVC_SUBCATEGORY (ID),
                                 INDEX IDX_NXTGEN_SVC_ITEM_SUBCATEGORY (SUBCATEGORY_ID)
);

CREATE TABLE NXTGEN_SVC_ITEM_FIELD (
                                       ID                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       SERVICE_ITEM_ID     BIGINT        NOT NULL,
                                       SECTION             VARCHAR(100)  DEFAULT 'Service Details',
                                       FIELD_LABEL         VARCHAR(150)  NOT NULL,
                                       FIELD_NAME          VARCHAR(100)  NOT NULL,
                                       FIELD_TYPE          VARCHAR(30)   NOT NULL,
                                       IS_REQUIRED         CHAR(1)       DEFAULT 'N',
                                       DISPLAY_ORDER       INT           DEFAULT 0,
                                       OPTIONS             VARCHAR(1000) NULL,
                                       CONDITIONAL_FIELD   VARCHAR(100)  NULL,
                                       CONDITIONAL_VALUE   VARCHAR(100)  NULL,
                                       CONSTRAINT FK_NXTGEN_SVC_ITEM_FIELD_ITEM FOREIGN KEY (SERVICE_ITEM_ID) REFERENCES NXTGEN_SVC_ITEM (ID),
                                       INDEX IDX_NXTGEN_SVC_ITEM_FIELD_ITEM (SERVICE_ITEM_ID)
);

CREATE TABLE NXTGEN_SVC_REQUEST (
                                    ID                       BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    REQUEST_NUMBER           VARCHAR(30)   NOT NULL,
                                    CATEGORY_ID              BIGINT        NOT NULL,
                                    SUBCATEGORY_ID           BIGINT        NOT NULL,
                                    SERVICE_ITEM_ID          BIGINT        NOT NULL,
                                    REQUEST_TITLE            VARCHAR(255)  NOT NULL,
                                    DESCRIPTION              VARCHAR(2000),
                                    REQUESTED_FOR_USER_ID    BIGINT        NOT NULL,
                                    REQUESTED_BY_USER_ID     BIGINT        NOT NULL,
                                    REQUEST_TYPE             VARCHAR(50)   NOT NULL,
                                    PRIORITY                 VARCHAR(20),
                                    URGENCY                  VARCHAR(20),
                                    IMPACT                   VARCHAR(20),
                                    BUSINESS_JUSTIFICATION   VARCHAR(2000),
                                    REQUIRED_BY_DATE         DATE,
                                    PREFERRED_CONTACT_METHOD VARCHAR(30),
                                    CONTACT_NUMBER           VARCHAR(30),
                                    LOCATION                 VARCHAR(150),
                                    ADDITIONAL_COMMENTS      VARCHAR(2000),
                                    STATUS                   VARCHAR(30)   DEFAULT 'SUBMITTED',
                                    CRE_DATE                 TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6),
                                    UPD_DATE                 TIMESTAMP(6)  NULL,
                                    CONSTRAINT UQ_NXTGEN_SVC_REQUEST_NUMBER UNIQUE (REQUEST_NUMBER),
                                    CONSTRAINT FK_NXTGEN_SVC_REQUEST_CATEGORY FOREIGN KEY (CATEGORY_ID) REFERENCES NXTGEN_SVC_CATEGORY (ID),
                                    CONSTRAINT FK_NXTGEN_SVC_REQUEST_SUBCATEGORY FOREIGN KEY (SUBCATEGORY_ID) REFERENCES NXTGEN_SVC_SUBCATEGORY (ID),
                                    CONSTRAINT FK_NXTGEN_SVC_REQUEST_ITEM FOREIGN KEY (SERVICE_ITEM_ID) REFERENCES NXTGEN_SVC_ITEM (ID),
                                    INDEX IDX_NXTGEN_SVC_REQUEST_REQUESTED_FOR (REQUESTED_FOR_USER_ID)
);

CREATE TABLE NXTGEN_SVC_REQUEST_FIELD_VALUE (
                                                ID          BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                REQUEST_ID  BIGINT        NOT NULL,
                                                FIELD_LABEL VARCHAR(150),
                                                FIELD_NAME  VARCHAR(100),
                                                FIELD_VALUE VARCHAR(2000),
                                                CONSTRAINT FK_NXTGEN_SVC_REQ_FIELD_VALUE_REQUEST FOREIGN KEY (REQUEST_ID) REFERENCES NXTGEN_SVC_REQUEST (ID),
                                                INDEX IDX_NXTGEN_SVC_REQ_FIELD_VALUE_REQUEST (REQUEST_ID)
);

CREATE TABLE NXTGEN_SVC_REQUEST_ATTACHMENT (
                                               ID            BIGINT AUTO_INCREMENT PRIMARY KEY,
                                               REQUEST_ID    BIGINT       NOT NULL,
                                               FILE_NAME     VARCHAR(255) NOT NULL,
                                               CONTENT_TYPE  VARCHAR(100),
                                               FILE_SIZE     BIGINT,
                                               FILE_DATA     LONGBLOB,
                                               UPLOADED_AT   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
                                               CONSTRAINT FK_NXTGEN_SVC_REQ_ATTACHMENT_REQUEST FOREIGN KEY (REQUEST_ID) REFERENCES NXTGEN_SVC_REQUEST (ID),
                                               INDEX IDX_NXTGEN_SVC_REQ_ATTACHMENT_REQUEST (REQUEST_ID)
);

CREATE TABLE NXTGEN_SVC_APPROVAL_STEP_CONFIG (
                                                 ID             BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 STEP_ORDER     INT          NOT NULL,
                                                 STEP_NAME      VARCHAR(100) NOT NULL,
                                                 APPROVER_TYPE  VARCHAR(30)  NOT NULL,
                                                 IS_ACTIVE      CHAR(1)      DEFAULT 'Y'
);

CREATE TABLE NXTGEN_SVC_REQUEST_APPROVAL (
                                             ID                BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             REQUEST_ID        BIGINT       NOT NULL,
                                             STEP_ORDER        INT          NOT NULL,
                                             STEP_NAME         VARCHAR(100) NOT NULL,
                                             APPROVER_TYPE     VARCHAR(30)  NOT NULL,
                                             APPROVER_USER_ID  BIGINT       NULL,
                                             STATUS            VARCHAR(20)  DEFAULT 'PENDING',
                                             ACTED_AT          TIMESTAMP(6) NULL,
                                             COMMENTS          VARCHAR(500),
                                             CONSTRAINT FK_NXTGEN_SVC_REQ_APPROVAL_REQUEST FOREIGN KEY (REQUEST_ID) REFERENCES NXTGEN_SVC_REQUEST (ID),
                                             INDEX IDX_NXTGEN_SVC_REQ_APPROVAL_REQUEST (REQUEST_ID)
);

INSERT INTO NXTGEN_SVC_APPROVAL_STEP_CONFIG (STEP_ORDER, STEP_NAME, APPROVER_TYPE, IS_ACTIVE) VALUES
                                                                                                  (1, 'Manager Approval', 'MANAGER', 'Y'),
                                                                                                  (2, 'IT Team Assignment', 'IT_TEAM', 'Y'),
                                                                                                  (3, 'Fulfillment', 'IT_TEAM', 'Y'),
                                                                                                  (4, 'User Validation', 'REQUESTER', 'Y');
