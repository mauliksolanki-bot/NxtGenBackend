-- Seed the initial administrator user and assign the SUPER ADMIN role.
-- Password is BCrypt-hashed (plaintext: nxtgendemo123) to match
-- UserAuthenticationService's PasswordEncoder-based verification.
INSERT INTO NXTGEN_USERS_MASTER
    (FIRST_NM, LAST_NM, EMPL_NM, USERNAME, PASSWORD, EMAIL_ADDRESS, ACTV_FLAG, IS_LOCKED, IS_SUP_ADMIN)
VALUES
    ('Administrator', 'User', 'Administrator User', 'administrator',
     '$2a$10$CSaaT5enkgg3gYDguwbdAeWenmAFw331mcPeUGg62NpNjL2NzgZgm',
     'administrator@nxtgen.com', 'Y', 'N', 'Y');

INSERT INTO NXTGEN_USER_ROLES (USER_ID, USERNAME, ROLE_ID, CRE_BY)
SELECT u.ID, u.USERNAME, r.ID, 'system'
FROM NXTGEN_USERS_MASTER u
JOIN NXTGEN_ROLES r ON r.ROLE_NAME = 'SUPER ADMIN'
WHERE u.USERNAME = 'administrator';
