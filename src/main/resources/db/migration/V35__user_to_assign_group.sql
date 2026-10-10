-- User-to-group assignment table (many-to-many: one user can belong to
-- multiple groups), mirroring the NXTGEN_USER_ROLES pattern.
CREATE TABLE NXTGEN_USER_GROUPS (
                                    ID        BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    USER_ID   BIGINT        NOT NULL,
                                    USERNAME  VARCHAR(255)  NOT NULL,
                                    GROUP_ID  BIGINT        NOT NULL,
                                    CRE_BY    VARCHAR(255),
                                    CRE_DATE  TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6),
                                    CONSTRAINT FK_NXTGEN_USER_GROUPS_USER FOREIGN KEY (USER_ID)
                                        REFERENCES NXTGEN_USERS_MASTER (ID),
                                    CONSTRAINT FK_NXTGEN_USER_GROUPS_GROUP FOREIGN KEY (GROUP_ID)
                                        REFERENCES NXTGEN_GROUPS_ND_TEAMS (ID),
                                    CONSTRAINT UQ_NXTGEN_USER_GROUPS_USER_GROUP UNIQUE (USER_ID, GROUP_ID),
                                    INDEX IDX_NXTGEN_USER_GROUPS_USER_ID (USER_ID),
                                    INDEX IDX_NXTGEN_USER_GROUPS_GROUP_ID (GROUP_ID)
);
