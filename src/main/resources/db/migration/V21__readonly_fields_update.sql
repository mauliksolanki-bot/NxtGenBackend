-- Make First Name and Last Name read-only on the Create User form: they are
-- populated only via the User ID search-and-select field, never typed directly.
UPDATE NXTGEN_FORM_FIELDS
SET IS_READONLY = 'Y'
WHERE FIELD_NAME IN ('FIRST_NAME', 'LAST_NAME')
  AND FORM_ID = (SELECT ID FROM (SELECT ID FROM NXTGEN_FORM WHERE FORM_NAME = 'CREATE_USER_FORM') AS form_lookup);

-- Track which NXTGEN_USERS_MASTER_DATA_WH record a created user came from,
-- so re-creating the same selected User ID can be detected and rejected.
ALTER TABLE NXTGEN_USERS_MASTER
    ADD COLUMN SRC_USER_ID BIGINT NULL;
