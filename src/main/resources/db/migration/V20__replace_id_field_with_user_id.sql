-- Remove the unused read-only "ID" field from the Create User form and
-- replace it with a searchable "User ID" field that looks records up from
-- NXTGEN_USERS_MASTER_DATA_WH (see NxtGenUsersWHController).
DELETE FROM NXTGEN_FORM_FIELDS
WHERE FIELD_NAME = 'ID'
  AND FORM_ID = (SELECT ID FROM (SELECT ID FROM NXTGEN_FORM WHERE FORM_NAME = 'CREATE_USER_FORM') AS form_lookup);

INSERT INTO NXTGEN_FORM_FIELDS
    (FORM_ID, TEMPLATE_TYPE, DISPLAY_ORDER, FIELD_NAME, FIELD_LABEL, DATA_FIELD, FIELD_TYPE,
     PLACEHOLDER, IS_MANDATORY, IS_READONLY, DEFAULT_VALUE, OPTIONS_SOURCE, CRE_BY)
SELECT f.ID, 'USER_FORM', 1, 'USER_ID', 'User ID', 'userId', 'SEARCH',
       'Enter User ID', 'Y', 'N', NULL, NULL, 'SYSTEM'
FROM NXTGEN_FORM f WHERE f.FORM_NAME = 'CREATE_USER_FORM';

-- Email address is no longer computed from the typed first/last name; it is
-- populated only once a user record is selected from the search results.
UPDATE NXTGEN_FORM_FIELDS
SET PLACEHOLDER = 'Select a User ID to populate'
WHERE FIELD_NAME = 'EMAIL_ADDRESS'
  AND FORM_ID = (SELECT ID FROM (SELECT ID FROM NXTGEN_FORM WHERE FORM_NAME = 'CREATE_USER_FORM') AS form_lookup);