CREATE TABLE IF NOT EXISTS `war` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `war_time` TIMESTAMP NOT NULL,
    `node` VARCHAR(64) NULL DEFAULT NULL,
    `won` TINYINT(1) NULL DEFAULT NULL,
    `message_id` VARCHAR(64) NOT NULL,
    `text_channel` VARCHAR(64) NOT NULL,
    `guild_id` INT(32) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `war_guild_fk`
      FOREIGN KEY (`guild_id`)
      REFERENCES `bdo_guild` (`id`),
    CONSTRAINT `war_channel_fk`
      FOREIGN KEY (`text_channel`)
      REFERENCES `text_channel` (`id`))
ENGINE = innodb;

CREATE TABLE IF NOT EXISTS `war_attendance` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `user_id` VARCHAR(64) NOT NULL,
    `war_id` INT(32) NULL DEFAULT NULL,
    `no_show` TINYINT(1) NOT NULL DEFAULT 0,
    `tardy` TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `war_attendance_user_fk`
        FOREIGN KEY (`user_id`)
        REFERENCES `users` (`id`),
    CONSTRAINT `war_attendance_war_fk`
            FOREIGN KEY (`war_id`)
            REFERENCES `war` (`id`))
ENGINE = innodb;

CREATE TABLE IF NOT EXISTS `war_vod` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `war_id` INT(32) NULL DEFAULT NULL,
    `vod_link` VARCHAR(2048) NOT NULL,
    `type` VARCHAR(128) NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `war_vod_fk`
        FOREIGN KEY (`war_id`)
        REFERENCES `war` (`id`))
ENGINE = innodb;

CREATE TABLE IF NOT EXISTS `war_stats` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `war_id` INT(32) NULL DEFAULT NULL,
    `img_link` VARCHAR(2048) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `war_stats_fk`
        FOREIGN KEY (`war_id`)
        REFERENCES `war` (`id`))
ENGINE = innodb;

ALTER TABLE `guild`
ADD bdo_guild_id INT(32) NULL DEFAULT NULL,
ADD FOREIGN KEY (`bdo_guild_id`) REFERENCES `bdo_guild`(`id`);

ALTER TABLE `bdo_guild`
ADD war_days INT(32) NULL DEFAULT NULL;

ALTER TABLE `text_channel`
ADD war_channel INT(32) NULL DEFAULT NULL;