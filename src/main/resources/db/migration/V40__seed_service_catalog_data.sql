-- Seeds the configurable Service Catalog: 12 top-level categories, their
-- sub-categories, a default "request" service item under every
-- sub-category, and full dynamic field metadata for the three fully
-- specified example services (Hardware > Laptop > New Laptop,
-- Software > Software Installation, Access & Identity > Application Access).

-- ---------------------------------------------------------------------
-- 1) Categories
-- ---------------------------------------------------------------------
INSERT INTO NXTGEN_SVC_CATEGORY (NAME, ICON, DESCRIPTION, DISPLAY_ORDER, IS_ACTIVE) VALUES
                                                                                        ('Hardware', '💻', 'Laptop, Monitor & Peripheral Services', 1, 'Y'),
                                                                                        ('Software', '🖥️', 'Software installation/access', 2, 'Y'),
                                                                                        ('Access & Identity', '🔐', 'Access and permission requests', 3, 'Y'),
                                                                                        ('Email & Collaboration', '📧', 'Email and collaboration services', 4, 'Y'),
                                                                                        ('Network', '🌐', 'Network-related services', 5, 'Y'),
                                                                                        ('Employee Services', '👤', 'Employee IT services', 6, 'Y'),
                                                                                        ('Cloud Services', '☁️', 'Cloud infrastructure/services', 7, 'Y'),
                                                                                        ('Database Services', '🗄️', 'Database-related requests', 8, 'Y'),
                                                                                        ('Mobile Services', '📱', 'Mobile/device services', 9, 'Y'),
                                                                                        ('Security', '🛡️', 'Cybersecurity services', 10, 'Y'),
                                                                                        ('Printing', '🖨️', 'Printing services', 11, 'Y'),
                                                                                        ('Other IT Services', '🔧', 'Miscellaneous IT requests', 12, 'Y');

-- ---------------------------------------------------------------------
-- 2) Sub-Categories
-- ---------------------------------------------------------------------
SET @cat_hardware = (SELECT ID FROM NXTGEN_SVC_CATEGORY WHERE NAME = 'Hardware');
SET @cat_software = (SELECT ID FROM NXTGEN_SVC_CATEGORY WHERE NAME = 'Software');
SET @cat_access = (SELECT ID FROM NXTGEN_SVC_CATEGORY WHERE NAME = 'Access & Identity');
SET @cat_email = (SELECT ID FROM NXTGEN_SVC_CATEGORY WHERE NAME = 'Email & Collaboration');
SET @cat_network = (SELECT ID FROM NXTGEN_SVC_CATEGORY WHERE NAME = 'Network');
SET @cat_employee = (SELECT ID FROM NXTGEN_SVC_CATEGORY WHERE NAME = 'Employee Services');
SET @cat_cloud = (SELECT ID FROM NXTGEN_SVC_CATEGORY WHERE NAME = 'Cloud Services');
SET @cat_db = (SELECT ID FROM NXTGEN_SVC_CATEGORY WHERE NAME = 'Database Services');
SET @cat_mobile = (SELECT ID FROM NXTGEN_SVC_CATEGORY WHERE NAME = 'Mobile Services');
SET @cat_security = (SELECT ID FROM NXTGEN_SVC_CATEGORY WHERE NAME = 'Security');
SET @cat_printing = (SELECT ID FROM NXTGEN_SVC_CATEGORY WHERE NAME = 'Printing');
SET @cat_other = (SELECT ID FROM NXTGEN_SVC_CATEGORY WHERE NAME = 'Other IT Services');

INSERT INTO NXTGEN_SVC_SUBCATEGORY (CATEGORY_ID, NAME, ICON, DESCRIPTION, DISPLAY_ORDER, IS_ACTIVE) VALUES
                                                                                                        (@cat_hardware, 'Laptop', '💻', 'Laptop-related requests', 1, 'Y'),
                                                                                                        (@cat_hardware, 'Desktop', '🖥️', 'Desktop-related requests', 2, 'Y'),
                                                                                                        (@cat_hardware, 'Monitor', '🖥️', 'Monitor requests', 3, 'Y'),
                                                                                                        (@cat_hardware, 'Keyboard & Mouse', '⌨️', 'Peripheral requests', 4, 'Y'),
                                                                                                        (@cat_hardware, 'Headset', '🎧', 'Headset/audio equipment', 5, 'Y'),
                                                                                                        (@cat_hardware, 'Mobile Device', '📱', 'Corporate mobile device', 6, 'Y'),
                                                                                                        (@cat_hardware, 'Printer', '🖨️', 'Printer-related requests', 7, 'Y'),
                                                                                                        (@cat_hardware, 'Hardware Replacement', '🔄', 'Replacement of existing hardware', 8, 'Y'),
                                                                                                        (@cat_hardware, 'Hardware Upgrade', '🛠️', 'RAM, SSD, etc.', 9, 'Y'),
                                                                                                        (@cat_hardware, 'Other Hardware', '📦', 'Other hardware requests', 10, 'Y'),

                                                                                                        (@cat_software, 'Software Installation', '🖥️', 'Install licensed/approved software', 1, 'Y'),
                                                                                                        (@cat_software, 'Software Access', '🔑', 'Access to an existing software license', 2, 'Y'),
                                                                                                        (@cat_software, 'Software Upgrade', '⬆️', 'Upgrade existing software version', 3, 'Y'),
                                                                                                        (@cat_software, 'Software Issue', '🐞', 'Report a software issue', 4, 'Y'),

                                                                                                        (@cat_access, 'VPN Access', '🔐', 'Request VPN access', 1, 'Y'),
                                                                                                        (@cat_access, 'Application Access', '🔑', 'Access to application', 2, 'Y'),
                                                                                                        (@cat_access, 'Shared Folder', '📁', 'Network/shared folder', 3, 'Y'),
                                                                                                        (@cat_access, 'Database Access', '🗄️', 'DB access', 4, 'Y'),
                                                                                                        (@cat_access, 'Email Group', '📧', 'Distribution list', 5, 'Y'),
                                                                                                        (@cat_access, 'Admin Access', '🛡️', 'Elevated privileges', 6, 'Y'),
                                                                                                        (@cat_access, 'MFA', '🔒', 'MFA enrollment/reset', 7, 'Y'),
                                                                                                        (@cat_access, 'SSO', '🔗', 'SSO access', 8, 'Y'),
                                                                                                        (@cat_access, 'Service Account', '⚙️', 'Service account request', 9, 'Y'),

                                                                                                        (@cat_email, 'Mailbox', '📧', 'Mailbox related requests', 1, 'Y'),
                                                                                                        (@cat_email, 'Teams', '👥', 'Microsoft Teams requests', 2, 'Y'),
                                                                                                        (@cat_email, 'Distribution List', '📋', 'Distribution list requests', 3, 'Y'),

                                                                                                        (@cat_network, 'Wi-Fi', '📶', 'Wi-Fi related requests', 1, 'Y'),
                                                                                                        (@cat_network, 'VPN', '🔐', 'VPN related requests', 2, 'Y'),
                                                                                                        (@cat_network, 'LAN', '🌐', 'LAN related requests', 3, 'Y'),

                                                                                                        (@cat_employee, 'New Joiner', '🆕', 'New joiner onboarding IT setup', 1, 'Y'),
                                                                                                        (@cat_employee, 'Transfer', '🔁', 'Employee transfer IT updates', 2, 'Y'),

                                                                                                        (@cat_cloud, 'AWS', '☁️', 'AWS related requests', 1, 'Y'),
                                                                                                        (@cat_cloud, 'Azure', '☁️', 'Azure related requests', 2, 'Y'),

                                                                                                        (@cat_db, 'DB Access', '🗄️', 'Database access request', 1, 'Y'),
                                                                                                        (@cat_db, 'DB Creation', '🗄️', 'Database creation request', 2, 'Y'),

                                                                                                        (@cat_mobile, 'Mobile', '📱', 'Mobile device request', 1, 'Y'),
                                                                                                        (@cat_mobile, 'SIM', '📶', 'SIM card request', 2, 'Y'),
                                                                                                        (@cat_mobile, 'MDM', '🛡️', 'Mobile device management enrollment', 3, 'Y'),

                                                                                                        (@cat_security, 'Security Exception', '🛡️', 'Security exception request', 1, 'Y'),
                                                                                                        (@cat_security, 'MFA', '🔒', 'MFA enrollment/reset', 2, 'Y'),

                                                                                                        (@cat_printing, 'Printer Access', '🖨️', 'Printer access request', 1, 'Y'),
                                                                                                        (@cat_printing, 'Printer Issue', '🖨️', 'Printer issue report', 2, 'Y'),

                                                                                                        (@cat_other, 'General IT Request', '🔧', 'Other/general IT request', 1, 'Y');

-- ---------------------------------------------------------------------
-- 3) Service Items: one per sub-category (named "Request <Sub-Category>")
--    so every tile is immediately submittable, even before specific
--    dynamic fields are defined for it.
-- ---------------------------------------------------------------------
INSERT INTO NXTGEN_SVC_ITEM (SUBCATEGORY_ID, NAME, DESCRIPTION, DISPLAY_ORDER, IS_ACTIVE)
SELECT ID, CONCAT('Request ', NAME), CONCAT('Submit a ', NAME, ' request'), 1, 'Y'
FROM NXTGEN_SVC_SUBCATEGORY;

-- Rename/retarget the three fully specified example services so their
-- titles match the walkthrough exactly.
UPDATE NXTGEN_SVC_ITEM SET NAME = 'New Laptop', DESCRIPTION = 'Request a new laptop'
WHERE SUBCATEGORY_ID = (SELECT ID FROM NXTGEN_SVC_SUBCATEGORY WHERE NAME = 'Laptop' AND CATEGORY_ID = @cat_hardware);

UPDATE NXTGEN_SVC_ITEM SET DESCRIPTION = 'Install licensed/approved software on a device'
WHERE SUBCATEGORY_ID = (SELECT ID FROM NXTGEN_SVC_SUBCATEGORY WHERE NAME = 'Software Installation' AND CATEGORY_ID = @cat_software);

UPDATE NXTGEN_SVC_ITEM SET DESCRIPTION = 'Request access to an application'
WHERE SUBCATEGORY_ID = (SELECT ID FROM NXTGEN_SVC_SUBCATEGORY WHERE NAME = 'Application Access' AND CATEGORY_ID = @cat_access);

SET @item_new_laptop = (
    SELECT I.ID FROM NXTGEN_SVC_ITEM I
    JOIN NXTGEN_SVC_SUBCATEGORY S ON S.ID = I.SUBCATEGORY_ID
    WHERE S.NAME = 'Laptop' AND S.CATEGORY_ID = @cat_hardware
);
SET @item_software_install = (
    SELECT I.ID FROM NXTGEN_SVC_ITEM I
    JOIN NXTGEN_SVC_SUBCATEGORY S ON S.ID = I.SUBCATEGORY_ID
    WHERE S.NAME = 'Software Installation' AND S.CATEGORY_ID = @cat_software
);
SET @item_app_access = (
    SELECT I.ID FROM NXTGEN_SVC_ITEM I
    JOIN NXTGEN_SVC_SUBCATEGORY S ON S.ID = I.SUBCATEGORY_ID
    WHERE S.NAME = 'Application Access' AND S.CATEGORY_ID = @cat_access
);

-- ---------------------------------------------------------------------
-- 4) Dynamic field metadata: Hardware > Laptop > New Laptop
-- ---------------------------------------------------------------------
INSERT INTO NXTGEN_SVC_ITEM_FIELD
(SERVICE_ITEM_ID, SECTION, FIELD_LABEL, FIELD_NAME, FIELD_TYPE, IS_REQUIRED, DISPLAY_ORDER, OPTIONS, CONDITIONAL_FIELD, CONDITIONAL_VALUE)
VALUES
    (@item_new_laptop, 'Service Details', 'Laptop Type', 'laptopType', 'DROPDOWN', 'Y', 1, 'Standard|Developer|Executive|Rugged', NULL, NULL),
    (@item_new_laptop, 'Service Details', 'Operating System', 'operatingSystem', 'DROPDOWN', 'Y', 2, 'Windows 11|Windows 10|macOS|Linux', NULL, NULL),
    (@item_new_laptop, 'Service Details', 'Processor Requirement', 'processorRequirement', 'DROPDOWN', 'N', 3, 'Standard|i5/Ryzen 5|i7/Ryzen 7|i9/Ryzen 9', NULL, NULL),
    (@item_new_laptop, 'Service Details', 'RAM Requirement', 'ramRequirement', 'DROPDOWN', 'N', 4, '8 GB|16 GB|32 GB|64 GB', NULL, NULL),
    (@item_new_laptop, 'Service Details', 'Storage Requirement', 'storageRequirement', 'DROPDOWN', 'N', 5, '256 GB SSD|512 GB SSD|1 TB SSD', NULL, NULL),
    (@item_new_laptop, 'Service Details', 'Existing Device', 'existingDevice', 'DROPDOWN', 'N', 6, 'Yes|No', NULL, NULL),
    (@item_new_laptop, 'Service Details', 'Existing Asset ID', 'existingAssetId', 'SEARCH', 'N', 7, NULL, 'existingDevice', 'Yes'),
    (@item_new_laptop, 'Service Details', 'Location', 'serviceLocation', 'DROPDOWN', 'Y', 8, 'Head Office|Branch Office|Remote/Work From Home', NULL, NULL),
    (@item_new_laptop, 'Service Details', 'Delivery Location', 'deliveryLocation', 'TEXT', 'Y', 9, NULL, NULL, NULL),
    (@item_new_laptop, 'Business Justification', 'Business Justification', 'businessJustification', 'TEXTAREA', 'Y', 10, NULL, NULL, NULL),
    (@item_new_laptop, 'Business Justification', 'Required By Date', 'requiredByDate', 'DATE', 'Y', 11, NULL, NULL, NULL);

-- ---------------------------------------------------------------------
-- 5) Dynamic field metadata: Software > Software Installation
-- ---------------------------------------------------------------------
INSERT INTO NXTGEN_SVC_ITEM_FIELD
(SERVICE_ITEM_ID, SECTION, FIELD_LABEL, FIELD_NAME, FIELD_TYPE, IS_REQUIRED, DISPLAY_ORDER, OPTIONS, CONDITIONAL_FIELD, CONDITIONAL_VALUE)
VALUES
    (@item_software_install, 'Service Details', 'Software Name', 'softwareName', 'SEARCH', 'Y', 1, NULL, NULL, NULL),
    (@item_software_install, 'Service Details', 'Version', 'softwareVersion', 'TEXT', 'N', 2, NULL, NULL, NULL),
    (@item_software_install, 'Service Details', 'Installation Device', 'installationDevice', 'SEARCH', 'Y', 3, NULL, NULL, NULL),
    (@item_software_install, 'Service Details', 'License Required', 'licenseRequired', 'DROPDOWN', 'Y', 4, 'Yes|No', NULL, NULL),
    (@item_software_install, 'Service Details', 'License Type', 'licenseType', 'DROPDOWN', 'N', 5, 'Named User|Concurrent|Perpetual|Subscription', 'licenseRequired', 'Yes'),
    (@item_software_install, 'Business Justification', 'Business Justification', 'businessJustification', 'TEXTAREA', 'Y', 6, NULL, NULL, NULL),
    (@item_software_install, 'Business Justification', 'Required By', 'requiredByDate', 'DATE', 'Y', 7, NULL, NULL, NULL),
    (@item_software_install, 'Service Details', 'Duration', 'duration', 'DROPDOWN', 'N', 8, 'Permanent|30 Days|90 Days|1 Year', NULL, NULL),
    (@item_software_install, 'Additional Information', 'Additional Details', 'additionalDetails', 'TEXTAREA', 'N', 9, NULL, NULL, NULL);

-- ---------------------------------------------------------------------
-- 6) Dynamic field metadata: Access & Identity > Application Access
-- ---------------------------------------------------------------------
INSERT INTO NXTGEN_SVC_ITEM_FIELD
(SERVICE_ITEM_ID, SECTION, FIELD_LABEL, FIELD_NAME, FIELD_TYPE, IS_REQUIRED, DISPLAY_ORDER, OPTIONS, CONDITIONAL_FIELD, CONDITIONAL_VALUE)
VALUES
    (@item_app_access, 'Service Details', 'Application', 'application', 'SEARCH', 'Y', 1, NULL, NULL, NULL),
    (@item_app_access, 'Service Details', 'Access Type', 'accessType', 'DROPDOWN', 'Y', 2, 'New Access|Modify Access|Remove Access|Temporary Access', NULL, NULL),
    (@item_app_access, 'Service Details', 'Access Level', 'accessLevel', 'DROPDOWN', 'Y', 3, 'Read Only|Standard User|Power User|Administrator', NULL, NULL),
    (@item_app_access, 'Service Details', 'Environment', 'environment', 'DROPDOWN', 'Y', 4, 'Production|UAT|Test|Development', NULL, NULL),
    (@item_app_access, 'Business Justification', 'Business Justification', 'businessJustification', 'TEXTAREA', 'Y', 5, NULL, NULL, NULL),
    (@item_app_access, 'Service Details', 'Start Date', 'startDate', 'DATE', 'Y', 6, NULL, NULL, NULL),
    (@item_app_access, 'Service Details', 'End Date', 'endDate', 'DATE', 'N', 7, NULL, NULL, NULL);
