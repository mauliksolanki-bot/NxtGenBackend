-- Revoked JWT tokens (logout invalidation).
-- A row here means the token identified by TOKEN_JTI has been explicitly
-- logged out and must be treated as invalid even if not yet expired.
CREATE TABLE NXTGEN_REVOKED_TOKENS (
                                       ID          BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       TOKEN_JTI   VARCHAR(255)  NOT NULL,
                                       USERNAME    VARCHAR(255)  NOT NULL,
                                       EXPIRES_AT  TIMESTAMP(6)  NOT NULL,
                                       CRE_DATE    TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6),
                                       CONSTRAINT UQ_NXTGEN_REVOKED_TOKENS_JTI UNIQUE (TOKEN_JTI)
);
