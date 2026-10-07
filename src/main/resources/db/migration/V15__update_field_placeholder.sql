-- Set the Username field placeholder to "Auto Generate" on the Create User form,
-- matching the ID field's placeholder convention for read-only, system-generated fields.
UPDATE NXTGEN_FORM_FIELDS ff
    JOIN NXTGEN_FORM f ON f.ID = ff.FORM_ID
    SET ff.PLACEHOLDER = 'Auto Generate'
WHERE f.FORM_NAME = 'CREATE_USER_FORM'
  AND ff.FIELD_NAME = 'USERNAME';
