ALTER TABLE `family`
ADD missing_from_site_count INT(32) NOT NULL DEFAULT 0;

ALTER TABLE `family`
ADD missing TINYINT(1) NOT NULL DEFAULT 0;