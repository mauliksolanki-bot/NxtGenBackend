-- Requests uses the same grid registry and column metadata as Administration grids.
CREATE INDEX IDX_SR_MASTER_OWNER_OPENED ON NXTGEN_SR_MASTER (OPENED_BY_USERNAME, OPENED_AT, SR_ID);

INSERT INTO NXTGEN_GRID (GRID_NAME, DATA_API, LOADING_MESSAGE, CRE_BY)
VALUES ('REQUESTS_GRID', '/api/griddata?gridName=REQUESTS_GRID', 'Loading requests...', 'SYSTEM');

INSERT INTO NXTGEN_GRID_COLUMN
(GRID_ID, COLUMN_NAME, DATA_FIELD, WIDTH, DISPLAY_ORDER, IS_SORTABLE, IS_FILTERABLE, IS_HIDDEN, CRE_BY)
SELECT g.ID, c.label, c.field, c.width, c.ord, 'Y', 'Y', 'N', 'SYSTEM'
FROM NXTGEN_GRID g JOIN (
    SELECT 'Number' label, 'number' field, 220 width, 1 ord
    UNION ALL SELECT 'Catalog', 'catalog', 190, 2
    UNION ALL SELECT 'Item', 'item', 260, 3
    UNION ALL SELECT 'Approval', 'approval', 200, 4
    UNION ALL SELECT 'Quantity', 'quantity', 110, 5
    UNION ALL SELECT 'Stage', 'stage', 270, 6
) c ON 1 = 1
WHERE g.GRID_NAME = 'REQUESTS_GRID';
