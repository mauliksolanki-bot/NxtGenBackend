-- Make the Create User "Role" field a multi-select dropdown so one user can
-- be assigned multiple roles at once.
UPDATE NXTGEN_FORM_FIELDS
SET FIELD_TYPE = 'MULTISELECT',
    PLACEHOLDER = 'Select Role(s)'
WHERE FIELD_NAME = 'ROLE'
  AND FORM_ID = (SELECT ID FROM (SELECT ID FROM NXTGEN_FORM WHERE FORM_NAME = 'CREATE_USER_FORM') AS form_lookup);
