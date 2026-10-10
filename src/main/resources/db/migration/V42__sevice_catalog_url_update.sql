-- Renames the Service Catalog nav menu URL from /catalog to /service-catalog
-- to match the frontend route update.
UPDATE NXTGEN_NAV_MENU SET MENU_URL = '/service-catalog'
WHERE MENU_CODE = 'SERVICE_CATALOG';
