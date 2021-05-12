CREATE TABLE IF NOT EXISTS `gearset` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `ap` INT(32) NULL DEFAULT NULL,
    `awk_ap` INT(32) NULL DEFAULT NULL,
    `dp` INT(32) NULL DEFAULT NULL,
    `lvl` INT(32) NULL DEFAULT NULL,
    `class` VARCHAR(63) NULL DEFAULT NULL,
    `class_state` INT(2) NULL DEFAULT NULL,
    `axe` INT(32) NULL DEFAULT NULL,
    `planner_link` VARCHAR(1024) NULL DEFAULT NULL,
    `gear_img_link` VARCHAR(1024) NULL DEFAULT NULL,
    `user_id` VARCHAR(255),
    PRIMARY KEY (`id`),
    CONSTRAINT `user_gear_fk`
            FOREIGN KEY (`user_id`)
            REFERENCES `users` (`id`))
ENGINE = innodb;