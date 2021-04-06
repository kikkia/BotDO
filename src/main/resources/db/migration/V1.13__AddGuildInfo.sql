CREATE TABLE IF NOT EXISTS `family` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `external_id` VARCHAR(1024) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `region` VARCHAR(255) NOT NULL,
    `last_updated` TIMESTAMP NOT NULL,
    PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `bdo_guild` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `region` VARCHAR(255) NOT NULL,
    `master_family` INT(32) NULL,
    `last_scan` TIMESTAMP NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `bdo_guild_master_fk`
        FOREIGN KEY (`master_family`)
        REFERENCES `family` (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `guild_family_membership` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `family` INT(32) NOT NULL,
    `guild` INT(32) NOT NULL,
    `created` TIMESTAMP NOT NULL,
    `removed` TIMESTAMP NULL DEFAULT NULL,
    `active` TINYINT NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    CONSTRAINT `family_membership_fk`
        FOREIGN KEY (`family`)
        REFERENCES `family` (`id`),
    CONSTRAINT `bdo_guild_membership_fk`
        FOREIGN KEY (`guild`)
        REFERENCES `bdo_guild` (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `character` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `family` INT(32) NOT NULL,
    `class` VARCHAR(255) NOT NULL,
    `level` INT(32) NOT NULL,
    `main` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `character_family_fk`
        FOREIGN KEY (`family`)
        REFERENCES `family` (`id`))
ENGINE = InnoDB;

