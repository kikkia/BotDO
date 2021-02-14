CREATE TABLE IF NOT EXISTS `event_type` (
    `id` INT(32) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `description` VARCHAR(2047) NOT NULL)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `event` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
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
        REFERENCES `users` (`id`),
    CONSTRAINT `event_type_event`
        FOREIGN KEY (`event_type`)
        REFERENCES `event_type` (`id`))
ENGINE = InnoDB;
