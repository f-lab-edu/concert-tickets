create table users
(
    id         bigint auto_increment comment '고유번호'
        primary key,
    email      varchar(50)                           not null comment '이메일',
    password   varchar(60)                           not null comment '비밀번호',
    name       varchar(50)                           not null comment '이름',
    phone      varchar(50)                           null comment '연락처',
    role       varchar(30) default 'USER'            not null comment '권한',
    created_at datetime    default CURRENT_TIMESTAMP null comment '생성일',
    created_by bigint                                not null comment '생성자',
    updated_at datetime                              null comment '수정일',
    updated_by bigint                                null comment '수정자',
    status     varchar(20) default 'ACTIVE'          not null comment '상태'
);
