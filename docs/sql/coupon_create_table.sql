drop table if exists coupon;
CREATE TABLE IF NOT EXISTS coupon
(
    `id`            int(11)     NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `template_code` varchar(16) NOT NULL DEFAULT '0' COMMENT '优惠券模版Code',
    `member_code`   varchar(20) NOT NULL DEFAULT '0' COMMENT '领取用户',
    `coupon_code`   varchar(64) NOT NULL DEFAULT '' COMMENT '优惠券码',
    `assign_time`   datetime    NOT NULL DEFAULT current_timestamp COMMENT '领取时间',
    `status`        int(11)     NOT NULL DEFAULT '0' COMMENT '优惠券的状态',
    PRIMARY KEY (`id`),
    KEY `idx_template_code` (template_code),
    KEY `idx_user_code` (member_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='优惠券(用户领取的记录)';