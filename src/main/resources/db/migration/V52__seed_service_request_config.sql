-- Counters, lookup values, approval configuration and the CREATE_SR_FORM definition.
INSERT INTO NXTGEN_SR_SEQUENCE (SEQ_NAME, NEXT_VAL) VALUES ('SR', 1), ('REQ', 1);

INSERT INTO NXTGEN_SR_LOOKUP (LOOKUP_TYPE, LOOKUP_CODE, LOOKUP_LABEL, DISPLAY_ORDER) VALUES
                                                                                         ('STAGE', 'New',         'New',         1),
                                                                                         ('STAGE', 'Approval',    'Approval',    2),
                                                                                         ('STAGE', 'Fulfillment', 'Fulfillment', 3),
                                                                                         ('STAGE', 'Delivery',    'Delivery',    4),
                                                                                         ('STAGE', 'Closed',      'Closed',      5),
                                                                                         ('STATE', 'Draft',            'Draft',            1),
                                                                                         ('STATE', 'Submitted',        'Submitted',        2),
                                                                                         ('STATE', 'In Progress',      'In Progress',      3),
                                                                                         ('STATE', 'Closed Complete',  'Closed Complete',  4),
                                                                                         ('STATE', 'Closed Cancelled', 'Closed Cancelled', 5);

INSERT INTO NXTGEN_SR_APPROVAL_CHECKS (REQUEST_TYPE, APPROVAL_LEVEL, APPROVAL_NAME, APPROVER_ROLE, CRE_BY) VALUES
                                                                                                               ('Licence Software Request', 1, 'Licence Manager',  'LICENCE MANAGER',  'SYSTEM'),
                                                                                                               ('Licence Software Request', 2, 'IT Manager',       'IT MANAGER',       'SYSTEM'),
                                                                                                               ('Hardware Request',         1, 'Line Manager',     'LINE MANAGER',     'SYSTEM'),
                                                                                                               ('Hardware Request',         2, 'IT Manager',       'IT MANAGER',       'SYSTEM'),
                                                                                                               ('Access Request',           1, 'Line Manager',     'LINE MANAGER',     'SYSTEM'),
                                                                                                               ('Access Request',           2, 'Security Manager', 'SECURITY MANAGER', 'SYSTEM'),
                                                                                                               ('Other IT Request',         1, 'IT Manager',       'IT MANAGER',       'SYSTEM');

INSERT INTO NXTGEN_FORM (FORM_NAME, DATA_API, TEMPLATE_TYPE, CRE_BY)
VALUES ('CREATE_SR_FORM', '/api/sr', 'SR_FORM', 'SYSTEM');

INSERT INTO NXTGEN_FORM_FIELDS
(FORM_ID, TEMPLATE_TYPE, DISPLAY_ORDER, FIELD_NAME, FIELD_LABEL, DATA_FIELD, FIELD_TYPE,
 PLACEHOLDER, IS_MANDATORY, IS_READONLY, DEFAULT_VALUE, OPTIONS_SOURCE, IS_FULL_WIDTH, CRE_BY)
SELECT f.ID, 'SR_FORM', v.ord, v.fname, v.flabel, v.dfield, v.ftype,
       v.placeholder, v.mandatory, v.readonly, v.defval, v.optsrc, v.fullw, 'SYSTEM'
FROM NXTGEN_FORM f
         JOIN (
    SELECT 1 AS ord, 'SR_NUMBER' AS fname, 'Sr Number' AS flabel, 'srId' AS dfield, 'TEXT' AS ftype,
           'Generated automatically' AS placeholder, 'N' AS mandatory, 'Y' AS readonly, NULL AS defval, NULL AS optsrc, 'N' AS fullw
    UNION ALL SELECT 2, 'OPENED', 'Opened', 'openedAt', 'TEXT', '', 'N', 'Y', NULL, NULL, 'N'
    UNION ALL SELECT 3, 'REQUEST_TYPE', 'Request Type', 'requestType', 'DROPDOWN', 'Select request type', 'Y', 'N', NULL, 'SR_REQUEST_TYPE', 'N'
    UNION ALL SELECT 4, 'OPENED_BY', 'Opened By', 'openedBy', 'TEXT', '', 'N', 'Y', NULL, NULL, 'N'
    UNION ALL SELECT 5, 'STAGE', 'Stage', 'stage', 'DROPDOWN', '', 'N', 'Y', 'New', 'SR_LOOKUP:STAGE', 'N'
    UNION ALL SELECT 6, 'REQUESTED_FOR', 'Requested For', 'requestedFor', 'SEARCH', 'Search user', 'Y', 'N', NULL, NULL, 'N'
    UNION ALL SELECT 7, 'STATE', 'State', 'state', 'DROPDOWN', '', 'N', 'Y', 'Draft', 'SR_LOOKUP:STATE', 'N'
    UNION ALL SELECT 8, 'DUE_DATE', 'Due Date', 'dueDate', 'DATE', '', 'N', 'N', NULL, NULL, 'N'
    UNION ALL SELECT 9, 'QUANTITY', 'Quantity', 'quantity', 'NUMBER', 'Enter quantity', 'Y', 'N', '1', NULL, 'N'
    UNION ALL SELECT 10, 'CONFIGURATION_ITEM', 'Configuration Item', 'ciItem', 'SEARCH', 'Search configuration item', 'N', 'N', NULL, NULL, 'N'
    UNION ALL SELECT 11, 'ESTIMATED_DELIVERY', 'Estimated Delivery', 'estimatedDelivery', 'DATE', '', 'N', 'N', NULL, NULL, 'N'
    UNION ALL SELECT 12, 'TITLE', 'Title', 'title', 'TEXT', 'Enter title', 'Y', 'N', NULL, NULL, 'Y'
    UNION ALL SELECT 13, 'DESCRIPTION', 'Description', 'description', 'TEXTAREA', 'Describe your request', 'Y', 'N', NULL, NULL, 'Y'
    UNION ALL SELECT 14, 'WORK_NOTES', 'Work Notes', 'workNotes', 'TEXTAREA', 'Add work notes', 'N', 'N', NULL, NULL, 'Y'
) v
WHERE f.FORM_NAME = 'CREATE_SR_FORM';
