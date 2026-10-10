-- Adds the "Owner" column to the Groups & Teams grid, between Description
-- and Active, shifting Active/Actions down one position.
UPDATE NXTGEN_GRID_COLUMN gc
    JOIN NXTGEN_GRID g ON g.ID = gc.GRID_ID
    SET gc.DISPLAY_ORDER = gc.DISPLAY_ORDER + 1
WHERE g.GRID_NAME = 'NXTGEN_GRP_TEAMS'
  AND gc.DATA_FIELD IN ('isActive', 'actions');

INSERT INTO NXTGEN_GRID_COLUMN
(GRID_ID, COLUMN_NAME, DATA_FIELD, WIDTH, DISPLAY_ORDER, IS_SORTABLE, IS_FILTERABLE, IS_HIDDEN, CRE_BY)
SELECT ID, 'OWNER', 'groupOwner', 220, 4, 'Y', 'Y', 'N', 'SYSTEM'
FROM NXTGEN_GRID WHERE GRID_NAME = 'NXTGEN_GRP_TEAMS';
