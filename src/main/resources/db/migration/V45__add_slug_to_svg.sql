-- Adds URL-friendly SLUG columns to the Service Catalog category and
-- sub-category tables so the frontend can route by slug
-- (e.g. /service-catalog/database-services) instead of numeric IDs.
ALTER TABLE NXTGEN_SVC_CATEGORY ADD COLUMN SLUG VARCHAR(160) NULL;
ALTER TABLE NXTGEN_SVC_SUBCATEGORY ADD COLUMN SLUG VARCHAR(160) NULL;

-- Slug = lowercase NAME with '&' -> 'and', punctuation removed, and spaces
-- or slashes turned into single hyphens.
UPDATE NXTGEN_SVC_CATEGORY
SET SLUG = LOWER(
        REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
                                                                                NAME,
                                                                                '(', ''),
                                                                        ')', ''),
                                                                ',', ''),
                                                        ' / ', '-'),
                                                '/', '-'),
                                        ' & ', '-and-'),
                                '&', 'and'),
                        '  ', ' '),
                ' ', '-')
           );

UPDATE NXTGEN_SVC_SUBCATEGORY
SET SLUG = LOWER(
        REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
                                                                                NAME,
                                                                                '(', ''),
                                                                        ')', ''),
                                                                ',', ''),
                                                        ' / ', '-'),
                                                '/', '-'),
                                        ' & ', '-and-'),
                                '&', 'and'),
                        '  ', ' '),
                ' ', '-')
           );

ALTER TABLE NXTGEN_SVC_CATEGORY MODIFY COLUMN SLUG VARCHAR(160) NOT NULL;
ALTER TABLE NXTGEN_SVC_SUBCATEGORY MODIFY COLUMN SLUG VARCHAR(160) NOT NULL;

ALTER TABLE NXTGEN_SVC_CATEGORY ADD CONSTRAINT UQ_NXTGEN_SVC_CATEGORY_SLUG UNIQUE (SLUG);
-- Sub-category slugs only need to be unique within their own category, since
-- the sub-category URL is always scoped under a parent category slug.
ALTER TABLE NXTGEN_SVC_SUBCATEGORY ADD CONSTRAINT UQ_NXTGEN_SVC_SUBCATEGORY_CATEGORY_SLUG UNIQUE (CATEGORY_ID, SLUG);
