-- Prevent duplicate user email addresses.
ALTER TABLE NXTGEN_USERS_MASTER
    ADD CONSTRAINT UQ_NXTGEN_USERS_MASTER_EMAIL UNIQUE (EMAIL_ADDRESS);

-- Extend the generic form-field metadata table so it can fully describe
-- inputs (label, type, placeholder, validation, default value, dropdown
-- options source) for metadata-driven forms such as Create User.
ALTER TABLE NXTGEN_FORM_FIELDS
    ADD COLUMN FIELD_NAME     VARCHAR(255),
    ADD COLUMN FIELD_LABEL    VARCHAR(255),
    ADD COLUMN DATA_FIELD     VARCHAR(255),
    ADD COLUMN FIELD_TYPE     VARCHAR(50),
    ADD COLUMN PLACEHOLDER    VARCHAR(255),
    ADD COLUMN IS_MANDATORY   VARCHAR(1) DEFAULT 'N',
    ADD COLUMN IS_READONLY    VARCHAR(1) DEFAULT 'N',
    ADD COLUMN DEFAULT_VALUE  VARCHAR(255),
    ADD COLUMN OPTIONS_SOURCE VARCHAR(255);
