-- Super Admin is now auto-derived from the selected Role (Yes when the role
-- is Super Admin, No otherwise), so make the dropdown read-only and default
-- it to "No" until a role is chosen.
UPDATE NXTGEN_FORM_FIELDS ff
    JOIN NXTGEN_FORM f ON f.ID = ff.FORM_ID
    SET ff.IS_READONLY = 'Y',
        ff.DEFAULT_VALUE = 'N'
WHERE f.FORM_NAME = 'CREATE_USER_FORM'
  AND ff.FIELD_NAME = 'SUPER_ADMIN';
