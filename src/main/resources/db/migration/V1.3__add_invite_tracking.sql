CREATE TABLE IF NOT EXISTS `guild_invite` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `guild_id` VARCHAR(255) NOT NULL,
    `code` VARCHAR(255) NOT NULL,
    `uses` INT(32) NOT NULL,
    `guild_prefix` VARCHAR(255) NULL DEFAULT NULL,
    `welcome_message` VARCHAR(1024) NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `invite_guild_fk`
        FOREIGN KEY(`guild_id`)
        REFERENCES `guild` (`id`)
)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `invite_role` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `role_id` VARCHAR(255) NOT NULL,
    `guild_invite_id` INT(32) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `invite_role_invite_fk`
        FOREIGN KEY(`guild_invite_id`)
        REFERENCES `guild_invite` (`id`)
)
ENGINE = InnoDB;

ALTER TABLE guild
ADD recruit_role VARCHAR(255),
ADD recruit_message VARCHAR(255),
ADD welcome_channel VARCHAR(255),
ADD rules_channel VARCHAR(255);