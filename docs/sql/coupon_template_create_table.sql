drop table if exists coupon_template;
CREATE TABLE IF NOT EXISTS coupon_template
(
    `id`            int(11)       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `template_code` varchar(16)   not null comment '模版编号',
    `available`     boolean       NOT NULL DEFAULT false COMMENT '是否是可用状态; true: 可用, false: 不可用',
    `expired`       boolean       NOT NULL DEFAULT false COMMENT '是否过期; true: 是, false: 否',
    `name`          varchar(64)   NOT NULL DEFAULT '' COMMENT '优惠券名称',
    `logo`          varchar(256)  NOT NULL DEFAULT '' COMMENT '优惠券 logo',
    `intro`         varchar(256)  NOT NULL DEFAULT '' COMMENT '优惠券描述',
    `category`      varchar(64)   NOT NULL DEFAULT '' COMMENT '优惠券分类',
    `product_line`  int(11)       NOT NULL DEFAULT '0' COMMENT '产品线',
    `coupon_count`  int(11)       NOT NULL DEFAULT '0' COMMENT '总数',
    `create_time`   datetime      NOT NULL DEFAULT current_timestamp COMMENT '创建时间',
    `user_id`       bigint(20)    NOT NULL DEFAULT '0' COMMENT '创建用户',
    `template_key`  varchar(128)  NOT NULL DEFAULT '' COMMENT '优惠券模板的编码',
    `target`        int(11)       NOT NULL DEFAULT '0' COMMENT '目标用户',
    `rule`          varchar(1024) NOT NULL DEFAULT '' COMMENT '优惠券规则: TemplateRule 的 json 表示',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_user_id` (`user_id`),
    UNIQUE KEY `name` (`name`),
    UNIQUE KEY `template_code` (template_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='优惠券模板表';