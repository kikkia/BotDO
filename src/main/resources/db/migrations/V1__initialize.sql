-- ----------------------------------------
-- Table `guild`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `guild` (
    `id` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NULL DEFAULT NULL,
    PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- ----------------------------------------
-- Table `text_channel`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `text_channel` (
    `id` VARCHAR(255) NOT NULL,
    `guild` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `announcement` TINYINT(1) NULL DEFAULT 0,
    `persistent` TINYINT(1) NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `guild_id_fk`
        FOREIGN KEY (`guild`)
        REFERENCES `guild` (`id`))
ENGINE = InnoDB;


-- ----------------------------------------
-- Table `user`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
    `id` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `family_name` VARCHAR(255) NULL DEFAULT NULL,
    PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- ----------------------------------------
-- Table `guild_membership`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `guild_membership` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `user_id` VARCHAR(255),
    `guild_id` VARCHAR(255),
    PRIMARY KEY (`id`),
    CONSTRAINT `guild_membership_user_id_fk`
        FOREIGN KEY(`user_id`)
        REFERENCES `users` (`id`),
    CONSTRAINT `guild_membership_guild_id_fk`
        FOREIGN KEY(`guild_id`)
        REFERENCES `guild` (`id`))
ENGINE = InnoDB;


-- ----------------------------------------
-- Table `scroll_inventory`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `scroll_inventory` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `user_id` VARCHAR(255) NOT NULL,
    `red_nose` INT(32) NOT NULL DEFAULT 0,
    `a_red_nose` INT(32) NOT NULL DEFAULT 0,
    `giath` INT(32) NOT NULL DEFAULT 0,
    `a_giath` INT(32) NOT NULL DEFAULT 0,
    `bheg` INT(32) NOT NULL DEFAULT 0,
    `a_bheg` INT(32) NOT NULL DEFAULT 0,
    `moghulis` INT(32) NOT NULL DEFAULT 0,
    `agrakhan` INT(32) NOT NULL DEFAULT 0,
    `narc` INT(32) NOT NULL DEFAULT 0,
    `a_narc` INT(32) NOT NULL DEFAULT 0,
    `ronin` INT(32) NOT NULL DEFAULT 0,
    `a_ronin` INT(32) NOT NULL DEFAULT 0,
    `dim` INT(32) NOT NULL DEFAULT 0,
    `a_dim` INT(32) NOT NULL DEFAULT 0,
    `muskan` INT(32) NOT NULL DEFAULT 0,
    `a_muskan` INT(32) NOT NULL DEFAULT 0,
    `hexe` INT(32) NOT NULL DEFAULT 0,
    `a_hexe` INT(32) NOT NULL DEFAULT 0,
    `ahib` INT(32) NOT NULL DEFAULT 0,
    `a_ahib` INT(32) NOT NULL DEFAULT 0,
    `urugon` INT(32) NOT NULL DEFAULT 0,
    `a_urugon` INT(32) NOT NULL DEFAULT 0,
    `putrum` INT(32) NOT NULL DEFAULT 0,
    `titium` INT(32) NOT NULL DEFAULT 0,
    `a_titium` INT(32) NOT NULL DEFAULT 0,
    `arc` INT(32) NOT NULL DEFAULT 0,
    `cartian` INT(32) NOT NULL DEFAULT 0,
    `pila_fe` INT(32) NOT NULL DEFAULT 0,
    `voodoo` INT(32) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `user_inven_id_fk`
        FOREIGN KEY(`user_id`)
        REFERENCES `users` (`id`),
ENGINE = InnoDB;


-- ----------------------------------------
-- Table `scroll_history`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `scroll_history` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `user_id` VARCHAR(255) NOT NULL,
    `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `red_nose` INT(32) NOT NULL DEFAULT 0,
    `a_red_nose` INT(32) NOT NULL DEFAULT 0,
    `giath` INT(32) NOT NULL DEFAULT 0,
    `a_giath` INT(32) NOT NULL DEFAULT 0,
    `bheg` INT(32) NOT NULL DEFAULT 0,
    `a_bheg` INT(32) NOT NULL DEFAULT 0,
    `moghulis` INT(32) NOT NULL DEFAULT 0,
    `Agrakhan` INT(32) NOT NULL DEFAULT 0,
    `narc` INT(32) NOT NULL DEFAULT 0,
    `a_narc` INT(32) NOT NULL DEFAULT 0,
    `ronin` INT(32) NOT NULL DEFAULT 0,
    `a_ronin` INT(32) NOT NULL DEFAULT 0,
    `dim` INT(32) NOT NULL DEFAULT 0,
    `a_dim` INT(32) NOT NULL DEFAULT 0,
    `muskan` INT(32) NOT NULL DEFAULT 0,
    `a_muskan` INT(32) NOT NULL DEFAULT 0,
    `hexe` INT(32) NOT NULL DEFAULT 0,
    `a_hexe` INT(32) NOT NULL DEFAULT 0,
    `ahib` INT(32) NOT NULL DEFAULT 0,
    `a_ahib` INT(32) NOT NULL DEFAULT 0,
    `urugon` INT(32) NOT NULL DEFAULT 0,
    `a_urugon` INT(32) NOT NULL DEFAULT 0,
    `putrum` INT(32) NOT NULL DEFAULT 0,
    `titium` INT(32) NOT NULL DEFAULT 0,
    `a_titium` INT(32) NOT NULL DEFAULT 0,
    `arc` INT(32) NOT NULL DEFAULT 0,
    `cartian` INT(32) NOT NULL DEFAULT 0,
    `pila_fe` INT(32) NOT NULL DEFAULT 0,
    `voodoo` INT(32) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `user_history_id_fk`
        FOREIGN KEY(`user_id`)
        REFERENCES `users` (`id`))
ENGINE = InnoDB;

-- ----------------------------------------
-- Table `scroll_group`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `scroll_group` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `guild_id` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `scroll_group_guild_fk`
        FOREIGN KEY(`guild_id`)
        REFERENCES `guild` (`id`)
)
ENGINE = InnoDB;

-- ----------------------------------------
-- Table `user_scroll_group`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `user_scroll_group` (
    `id` INT(32) NOT NULL AUTO_INCREMENT,
    `user_id` VARCHAR(255),
    `scroll_group_id` VARCHAR(255),
    PRIMARY KEY (`id`),
    CONSTRAINT `scroll_group_user_id_fk`
        FOREIGN KEY(`user_id`)
        REFERENCES `users` (`id`),
    CONSTRAINT `scroll_group_id_fk`
        FOREIGN KEY(`scroll_group_id`)
        REFERENCES `scroll_group` (`id`))
ENGINE = InnoDB;
