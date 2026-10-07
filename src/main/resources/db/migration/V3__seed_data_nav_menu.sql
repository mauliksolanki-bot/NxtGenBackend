-- Seed top-level (PARENT) side navigation menu entries.
-- IS_ENABLED='Y' makes each item visible; PARENT_MENU_ID is NULL since these are
-- root-level parents (no owning menu).
INSERT INTO NXTGEN_NAV_MENU
    (MENU_NAME, MENU_CODE, MENU_DISPLAY_NAME, MENU_URL, IS_ENABLED, MENU_TYPE, PARENT_MENU_ID)
VALUES
    ('Dashboard',       'DASHBOARD',       'Dashboard',       '/home',     'Y', 'PARENT', NULL),
    ('Service Catalog', 'SERVICE_CATALOG', 'Service Catalog', '/catalog',  'Y', 'PARENT', NULL),
    ('Incidents',       'INCIDENTS',       'Incidents',       '/incidents','Y', 'PARENT', NULL),
    ('Requests',        'REQUESTS',        'Requests',        '/requests', 'Y', 'PARENT', NULL);
