-- Pair service-request fields like a ServiceNow record form:
-- Number | Opened, Request Type | Opened by, Request | Stage,
-- Requested for | State, Due date | Quantity, Configuration item | Estimated delivery.
-- Title, Description, Work notes (and Subcategory) span the full row.

INSERT INTO NXTGEN_FORM_FIELDS
(FORM_ID, TEMPLATE_TYPE, DISPLAY_ORDER, FIELD_NAME, FIELD_LABEL, DATA_FIELD, FIELD_TYPE,
 PLACEHOLDER, IS_MANDATORY, IS_READONLY, DEFAULT_VALUE, OPTIONS_SOURCE, IS_FULL_WIDTH, CRE_BY)
SELECT f.ID, 'SR_FORM', 5, 'REQUEST', 'Request', 'reqId', 'TEXT',
       '', 'N', 'Y', NULL, NULL, 'N', 'SYSTEM'
FROM NXTGEN_FORM f
WHERE (f.FORM_NAME = 'CREATE_SR_FORM' OR f.FORM_NAME LIKE 'SR_FORM_%')
  AND NOT EXISTS (
      SELECT 1
      FROM NXTGEN_FORM_FIELDS x
      WHERE x.FORM_ID = f.ID
        AND x.DATA_FIELD = 'reqId'
  );

UPDATE NXTGEN_FORM_FIELDS
SET DISPLAY_ORDER = CASE DATA_FIELD
        WHEN 'srId' THEN 1
        WHEN 'openedAt' THEN 2
        WHEN 'requestType' THEN 3
        WHEN 'openedBy' THEN 4
        WHEN 'reqId' THEN 5
        WHEN 'stage' THEN 6
        WHEN 'requestedFor' THEN 7
        WHEN 'state' THEN 8
        WHEN 'dueDate' THEN 9
        WHEN 'quantity' THEN 10
        WHEN 'ciItem' THEN 11
        WHEN 'estimatedDelivery' THEN 12
        WHEN 'subcategory' THEN 13
        WHEN 'title' THEN 20
        WHEN 'description' THEN 21
        WHEN 'workNotes' THEN 22
        ELSE DISPLAY_ORDER
    END,
    FIELD_LABEL = CASE DATA_FIELD
        WHEN 'srId' THEN 'Number'
        WHEN 'openedAt' THEN 'Opened'
        WHEN 'requestType' THEN 'Request Type'
        WHEN 'openedBy' THEN 'Opened by'
        WHEN 'reqId' THEN 'Request'
        WHEN 'stage' THEN 'Stage'
        WHEN 'requestedFor' THEN 'Requested for'
        WHEN 'state' THEN 'State'
        WHEN 'dueDate' THEN 'Due date'
        WHEN 'quantity' THEN 'Quantity'
        WHEN 'ciItem' THEN 'Configuration item'
        WHEN 'estimatedDelivery' THEN 'Estimated delivery'
        WHEN 'subcategory' THEN 'Subcategory'
        WHEN 'title' THEN 'Title'
        WHEN 'description' THEN 'Description'
        WHEN 'workNotes' THEN 'Work notes'
        ELSE FIELD_LABEL
    END,
    IS_FULL_WIDTH = CASE
        WHEN DATA_FIELD IN ('title', 'description', 'workNotes', 'subcategory') THEN 'Y'
        ELSE 'N'
    END
WHERE FORM_ID IN (
    SELECT ID
    FROM (
        SELECT ID
        FROM NXTGEN_FORM
        WHERE FORM_NAME = 'CREATE_SR_FORM'
           OR FORM_NAME LIKE 'SR_FORM_%'
    ) sr_forms
);
