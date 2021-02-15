CREATE TABLE IF NOT EXISTS `event` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `author` VARCHAR(255) NOT NULL,
    `guild_id` VARCHAR(255) NOT NULL,
    `event_type` INT(32) NOT NULL,
    `next_time` TIMESTAMP NOT NULL,
    PRIMARY KEY(`id`),
    CONSTRAINT `event_guild_fk`
        FOREIGN KEY (`guild_id`)
        REFERENCES `guild` (`id`),
    CONSTRAINT `event_author_fk`
        FOREIGN KEY (`author`)
        REFERENCES `users` (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `event_roles` (
    `event_id` INT(32) NOT NULL,
    `role_id` VARCHAR(255) NOT NULL,
    PRIMARY KEY(`event_id`, `role_id`),
    CONSTRAINT `event_role_fk`
        FOREIGN KEY (`event_id`)
        REFERENCES `event` (`id`))
ENGINE = InnoDB;
