-- Lets a form field span the full row (e.g. Title, Description). Fields default
-- to the normal two-column layout.
ALTER TABLE NXTGEN_FORM_FIELDS
    ADD COLUMN IS_FULL_WIDTH CHAR(1) NOT NULL DEFAULT 'N';
