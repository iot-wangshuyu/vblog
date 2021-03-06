-- auto Generated on 2019-08-30 21:50:00 
-- DROP TABLE IF EXISTS `v_role`; 
CREATE TABLE `v_role`(
    `name` VARCHAR (50) NOT NULL DEFAULT '' COMMENT 'name',
    `id` BIGINT (15) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `is_del` SMALLINT (5) NOT NULL DEFAULT 0 COMMENT 'isDel',
    `create_time` DATETIME NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT 'createTime',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updateTime',
    PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '`v_role`';
