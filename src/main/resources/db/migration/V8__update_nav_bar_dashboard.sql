-- Update the Dashboard menu URL from /home to /dashboard.
-- A new migration is used (not an edit to V3) because V3 may already be applied.
UPDATE NXTGEN_NAV_MENU
SET MENU_URL = '/dashboard'
WHERE MENU_CODE = 'DASHBOARD';
