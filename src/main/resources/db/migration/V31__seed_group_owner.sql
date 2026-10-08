-- Seed the "Group Owner" role: users holding this role are eligible to be
-- selected as a Group's Owner on the Create Group form's Owner search field.
INSERT INTO NXTGEN_ROLES (ROLE_NAME, DESCRIPTION, IS_ACTIVE)
VALUES ('GROUP OWNER', 'Can be assigned as the owner of a Group / Team.', 'Y');
