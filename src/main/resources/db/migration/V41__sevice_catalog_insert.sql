-- The Service Catalog landing page should only surface these 9 categories
-- as tiles; the remaining seeded categories (Employee Services, Mobile
-- Services, Printing) stay configured in the table (so their sub-categories
-- and service items are preserved) but are deactivated so they are hidden
-- from the tile page until the business decides to surface them.
UPDATE NXTGEN_SVC_CATEGORY SET IS_ACTIVE = 'N'
WHERE NAME IN ('Employee Services', 'Mobile Services', 'Printing');

UPDATE NXTGEN_SVC_CATEGORY SET NAME = 'Other IT Request'
WHERE NAME = 'Other IT Services';

-- Re-sequence the display order of the remaining active categories so the
-- tile grid renders them in the exact order requested.
UPDATE NXTGEN_SVC_CATEGORY SET DISPLAY_ORDER = 1 WHERE NAME = 'Hardware';
UPDATE NXTGEN_SVC_CATEGORY SET DISPLAY_ORDER = 2 WHERE NAME = 'Software';
UPDATE NXTGEN_SVC_CATEGORY SET DISPLAY_ORDER = 3 WHERE NAME = 'Access & Identity';
UPDATE NXTGEN_SVC_CATEGORY SET DISPLAY_ORDER = 4 WHERE NAME = 'Email & Collaboration';
UPDATE NXTGEN_SVC_CATEGORY SET DISPLAY_ORDER = 5 WHERE NAME = 'Network';
UPDATE NXTGEN_SVC_CATEGORY SET DISPLAY_ORDER = 6 WHERE NAME = 'Cloud Services';
UPDATE NXTGEN_SVC_CATEGORY SET DISPLAY_ORDER = 7 WHERE NAME = 'Database Services';
UPDATE NXTGEN_SVC_CATEGORY SET DISPLAY_ORDER = 8 WHERE NAME = 'Security';
UPDATE NXTGEN_SVC_CATEGORY SET DISPLAY_ORDER = 9 WHERE NAME = 'Other IT Request';
