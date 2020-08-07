create table user_role (
    user_id int8 not null,
    roles varchar(64));

create table users (
    id int8 not null,
    password varchar(255),
    username varchar(255),
    primary key (id));

alter table if exists user_role
    add constraint user_role_user_fk
        foreign key (user_id) references users;