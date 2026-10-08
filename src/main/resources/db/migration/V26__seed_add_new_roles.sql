-- Seed baseline ITSM roles used across Administration > Roles.
INSERT INTO NXTGEN_ROLES (ROLE_NAME, DESCRIPTION, IS_ACTIVE)
VALUES
    ('ITIL', 'Provides basic access to create incidents, service requests, and change requests.', 'Y'),
    ('SERVICE DESK AGENT', 'Handles incoming incidents and service requests, performs triage, and manages day-to-day ticket resolution.', 'Y'),
    ('CAB MANAGER', 'Leads the Change Advisory Board, schedules change review meetings, and approves or rejects submitted changes.', 'Y'),
    ('CAB MEMBER', 'Participates in Change Advisory Board reviews and provides approval input on submitted change requests.', 'Y'),
    ('CONFIGURATION ITEM OWNER', 'Owns and maintains assigned configuration items, ensuring their records stay accurate and up to date.', 'Y');
