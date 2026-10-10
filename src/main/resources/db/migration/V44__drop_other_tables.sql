-- Removes the Service Item / Request / Approval tables created in
-- V40__create_service_catalog_tables.sql. These are being redesigned, so
-- drop them (if they exist) for now; Category and Sub-Category tables are
-- kept since the tile-based catalog browsing feature depends on them.
-- Dropped in FK-safe order (children before parents).
DROP TABLE IF EXISTS NXTGEN_SVC_REQUEST_APPROVAL;
DROP TABLE IF EXISTS NXTGEN_SVC_REQUEST_ATTACHMENT;
DROP TABLE IF EXISTS NXTGEN_SVC_REQUEST_FIELD_VALUE;
DROP TABLE IF EXISTS NXTGEN_SVC_REQUEST;
DROP TABLE IF EXISTS NXTGEN_SVC_ITEM_FIELD;
DROP TABLE IF EXISTS NXTGEN_SVC_ITEM;
DROP TABLE IF EXISTS NXTGEN_SVC_APPROVAL_STEP_CONFIG;
