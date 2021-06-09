CREATE TABLE IF NOT EXISTS `war_dm_signups` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `war_id` INT(32) NOT NULL,
    `user_id` VARCHAR(255) NOT NULL,
    `message_id` VARCHAR(255) NOT NULL,
    `active` TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    CONSTRAINT `war_signup_fk`
        FOREIGN KEY (`war_id`)
        REFERENCES `war` (`id`))
ENGINE = innodb;