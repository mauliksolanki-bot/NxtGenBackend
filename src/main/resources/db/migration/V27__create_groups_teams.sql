-- Groups & Teams table.
CREATE TABLE NXTGEN_GROUPS_ND_TEAMS (
                                        ID               BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        GRP_NAME         VARCHAR(255)  NOT NULL,
                                        GRP_DESCRIPTION  VARCHAR(255),
                                        IS_ACTIVE        CHAR(1)       DEFAULT 'Y',
                                        CONSTRAINT UQ_NXTGEN_GROUPS_ND_TEAMS_GRP_NAME UNIQUE (GRP_NAME)
);
