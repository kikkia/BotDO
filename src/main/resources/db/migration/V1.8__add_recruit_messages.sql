CREATE TABLE IF NOT EXISTS `recruitment_post` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `author_id` VARCHAR(32) NOT NULL,
    `guild_id` VARCHAR(32) NOT NULL,
    `channel` VARCHAR(32) NOT NULL,
    `created` TIMESTAMP NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `recruitment_message_user_fk`
        FOREIGN KEY(`author_id`)
        REFERENCES `users` (`id`),
    CONSTRAINT `recruitment_message_guild_fk`
        FOREIGN KEY(`guild_id`)
        REFERENCES `guild` (`id`)
)
ENGINE = InnoDB;