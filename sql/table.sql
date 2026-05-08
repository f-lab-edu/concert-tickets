CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY comment '고유번호',
    email    VARCHAR(50) NOT NULL comment '이메일',
    password   VARCHAR(60) NOT NULL comment '비밀번호',
    name       VARCHAR(50) NOT NULL comment '이름',
    phone       VARCHAR(50) comment '연락처',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP comment '생성일',
    created_by BIGINT NOT NULL comment '생성자',
    updated_at DATETIME comment '수정일',
    updated_by BIGINT comment '수정자',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' comment '상태'
);