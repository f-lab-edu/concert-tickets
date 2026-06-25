DROP TABLE IF EXISTS `booking`;
DROP TABLE IF EXISTS `seat_inventory`;
DROP TABLE IF EXISTS `seat`;
DROP TABLE IF EXISTS `performance`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `email_verify_token`;
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users`
(
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '고유번호',
    `email`      VARCHAR(50)                       NOT NULL COMMENT '이메일',
    `password`   VARCHAR(60)                       NOT NULL COMMENT '비밀번호',
    `name`       VARCHAR(50)                       NOT NULL COMMENT '이름',
    `phone`      VARCHAR(50)                       NULL COMMENT '연락처',
    `role`       VARCHAR(30)                       NOT NULL DEFAULT 'USER' COMMENT '권한',
    `created_at` DATETIME                          NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `updated_at` DATETIME                          NULL COMMENT '수정일',
    `deleted`    TINYINT(1)                        NOT NULL DEFAULT 0 COMMENT '삭제 여부'
) comment '회원';

CREATE TABLE `email_verify_token`
(
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '고유번호',
    `token`      VARCHAR(50)                       NOT NULL COMMENT '토큰',
    `email`      VARCHAR(50)                       NOT NULL COMMENT '이메일',
    `expires_at` DATETIME                          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '만료시간',
    `consume_at` DATETIME                          NULL COMMENT '사용자 인증 시간',
    `created_at` DATETIME                          NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '생성일'
) comment '이메일 인증 토큰';

CREATE TABLE `category`
(
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '고유번호',
    `name`       VARCHAR(100)                      NOT NULL COMMENT '분류',
    `created_at` DATETIME                          NOT NULL DEFAULT NOW() COMMENT '생성일',
    `updated_at` DATETIME                          NULL COMMENT '수정일',
    `deleted`    TINYINT(1)                        NOT NULL DEFAULT 0 COMMENT '삭제 여부'
) comment '카테고리';

CREATE TABLE `product`
(
    `id`               BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '고유번호',
    `category_id`      BIGINT                            NOT NULL COMMENT '카테고리 고유번호',
    `title`            VARCHAR(100)                      NOT NULL COMMENT '공연명',
    `running_time`     INT                               NOT NULL COMMENT '관람시간',
    `start_date`       DATE                              NOT NULL COMMENT '시작일',
    `end_date`         DATE                              NOT NULL COMMENT '종료일',
    `booking_open_at`  DATETIME                          NOT NULL COMMENT '예매 오픈 일시',
    `booking_close_at` DATETIME                          NOT NULL COMMENT '예매 마감 일시',
    `created_at`       DATETIME                          NOT NULL DEFAULT NOW() COMMENT '생성일',
    `updated_at`       DATETIME                          NULL COMMENT '수정일',
    `deleted`          TINYINT(1)                        NOT NULL DEFAULT 0 COMMENT '삭제 여부'
) comment '상품';

CREATE TABLE `performance`
(
    `id`                 BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '고유번호',
    `product_id`         BIGINT                            NOT NULL COMMENT '상품 고유번호',
    `show_at`            DATETIME                          NOT NULL COMMENT '공연 일시',
    `status`             VARCHAR(50)                       NOT NULL COMMENT '예매 상태(예매 가능 / 매진 / 취소)',
    `created_at`         DATETIME                          NOT NULL DEFAULT NOW() COMMENT '생성일',
    `updated_at`         DATETIME                          NULL COMMENT '수정일',
    `deleted`            TINYINT(1)                        NOT NULL DEFAULT 0 COMMENT '삭제 여부'
) comment '공연 회차';

CREATE TABLE `seat`
(
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '고유번호',
    `product_id` BIGINT                            NOT NULL COMMENT '상품 고유번호',
    `zone`       VARCHAR(50)                       NOT NULL COMMENT '구역 (1층 A구역)',
    `row`        VARCHAR(50)                       NOT NULL COMMENT '열',
    `seat_no`    VARCHAR(50)                       NOT NULL COMMENT '번호',
    `grade`      VARCHAR(100)                      NOT NULL COMMENT '등급명',
    `price`      INT                               NOT NULL COMMENT '가격',
    `color`      VARCHAR(100)                      NOT NULL COMMENT '색상',
    `created_at` DATETIME                          NOT NULL DEFAULT NOW() COMMENT '생성일',
    `updated_at` DATETIME                          NULL COMMENT '수정일',
    `deleted`    TINYINT(1)                        NOT NULL DEFAULT 0 COMMENT '삭제 여부'
) comment '좌석';

CREATE TABLE `seat_inventory`
(
    `id`               BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '고유번호',
    `user_id`          BIGINT                            NULL COMMENT '회원 고유번호',
    `seat_id`          BIGINT                            NOT NULL COMMENT '좌석 고유번호',
    `performance_id`   BIGINT                            NOT NULL COMMENT '회차 고유번호',
    `status`           VARCHAR(50)                       NOT NULL COMMENT '좌석 상태(AVAILABLE/HELD/SOLD/BLOCKED)',
    `held_until`       DATETIME                          NULL COMMENT '선점(결제 중) 만료 시각)',
    `version`          BIGINT                            NOT NULL DEFAULT 0 COMMENT '버전',
    `created_at`       DATETIME                          NOT NULL DEFAULT NOW() COMMENT '생성일',
    `updated_at`       DATETIME                          NULL COMMENT '수정일',
    `deleted`          TINYINT(1)                        NOT NULL DEFAULT 0 COMMENT '삭제 여부'
) comment '좌석 슬롯';

CREATE TABLE `booking`
(
    `id`             BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '고유번호',
    `user_id`        BIGINT                            NOT NULL COMMENT '회원 고유번호',
    `performance_id` BIGINT                            NOT NULL COMMENT '회차 고유번호',
    `total_amount`   INT                               NOT NULL COMMENT '결제 금액',
    `booking_status` VARCHAR(30)                       NOT NULL COMMENT '결제 상태(PENDING/PAID/CANCELLED)',
    `created_at`     DATETIME                          NOT NULL DEFAULT NOW() COMMENT '생성일',
    `updated_at`     DATETIME                          NULL COMMENT '수정일',
    `deleted`        TINYINT(1)                        NOT NULL DEFAULT 0 COMMENT '삭제 여부'
) comment '예매';