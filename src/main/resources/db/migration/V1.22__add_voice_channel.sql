-- ----------------------------------------
-- Table `voice_channel`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `voice_channel` (
    `id` VARCHAR(255) NOT NULL,
    `guild` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `war` TINYINT(1) NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `guild_id_vc_fk`
        FOREIGN KEY (`guild`)
        REFERENCES `guild` (`id`))
ENGINE = InnoDB;
