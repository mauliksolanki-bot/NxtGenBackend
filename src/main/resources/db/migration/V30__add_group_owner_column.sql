-- Adds the Group Owner column to the Groups & Teams table.
ALTER TABLE NXTGEN_GROUPS_ND_TEAMS
    ADD COLUMN GROUP_OWNER VARCHAR(255) NULL;
