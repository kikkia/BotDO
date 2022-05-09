ALTER TABLE `family`
ADD missing_from_site_count INT(32) NOT NULL DEFAULT 0
ADD missing TINYINT(1) NOT NULL DEFAULT 0;