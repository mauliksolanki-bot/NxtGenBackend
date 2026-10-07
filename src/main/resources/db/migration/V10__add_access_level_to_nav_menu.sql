-- Adds role-based visibility control to nav menu items.
-- ACCESS_LEVEL holds either 'ALL' (visible to every user) or a specific
-- ROLE_NAME from NXTGEN_ROLES (visible only to users holding that role).
ALTER TABLE NXTGEN_NAV_MENU
    ADD COLUMN ACCESS_LEVEL VARCHAR(50) NOT NULL DEFAULT 'ALL';

UPDATE NXTGEN_NAV_MENU
SET ACCESS_LEVEL = 'ALL'
WHERE ACCESS_LEVEL IS NULL;
