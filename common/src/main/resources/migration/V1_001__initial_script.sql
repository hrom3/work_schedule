create table role
(
    id        serial      not null
        constraint user_roles_pk
            primary key,
    role_name varchar(50) not null
);

alter table role
    owner to postgres;

create unique index user_roles_role_uindex
    on role (role_name);

create unique index user_roles_role_id_uindex
    on role (id);

create table department
(
    id              serial      not null
        constraint department_pk
            primary key,
    department_name varchar(50) not null
);

alter table department
    owner to postgres;

create unique index department_id_depart_uindex
    on department (id);

create table user_work_schedule
(
    id              bigserial            not null
        constraint user_work_schedule_pk
            primary key,
    id_users        bigint               not null,
    id_rate         integer              not null,
    work_day        json                 not null,
    expiration_date date,
    is_used         boolean default true not null
);

alter table user_work_schedule
    owner to postgres;

create unique index user_work_schedule_id_uindex
    on user_work_schedule (id);

create table issues_from_jira
(
    id                    bigserial   not null
        constraint type_of_user_days_pk
            primary key,
    name_of_project       varchar(50) not null,
    short_name_of_project varchar(10) not null,
    jira_issues_id        bigint      not null
);

alter table issues_from_jira
    owner to postgres;

create unique index type_of_user_days_id_type_of_day_uindex
    on issues_from_jira (id);

create unique index issues_from_jira_jira_issues_id_uindex
    on issues_from_jira (jira_issues_id);

create index issues_from_jira_name_of_project_index
    on issues_from_jira (name_of_project);

create index issues_from_jira_short_name_of_project_jira_issues_id_index
    on issues_from_jira (short_name_of_project, jira_issues_id);

create table rate
(
    id                  serial        not null
        constraint rate_pk
            primary key,
    salary_rate         numeric(3, 2) not null,
    work_hour           integer       not null,
    work_hour_short_day integer
);

alter table rate
    owner to postgres;

create unique index rate_id_rate_uindex
    on rate (id);

create unique index rate_salary_rate_uindex
    on rate (salary_rate);

create unique index rate_work_hour_uindex
    on rate (work_hour);

create table type_of_calendar_days
(
    id   serial not null
        constraint type_of_calendar_days_pk
            primary key,
    type varchar(50)
);

alter table type_of_calendar_days
    owner to postgres;

create table "production calendar"
(
    id                           bigserial not null
        constraint "production calendar_pk"
            primary key,
    date                         date      not null,
    id_type_of_prod_calendar_day integer   not null
        constraint "production calendar_type_of_calendar_days_id_fk"
            references type_of_calendar_days,
    description                  varchar(50)
);

alter table "production calendar"
    owner to postgres;

create unique index "production calendar_id_uindex"
    on "production calendar" (id);

create unique index type_of_calendar_days_id_uindex
    on type_of_calendar_days (id);

create table rooms
(
    id          serial     not null
        constraint rooms_pk
            primary key,
    room_number varchar(8) not null
);

alter table rooms
    owner to postgres;

create table users
(
    id            bigserial             not null
        constraint users_pk
            primary key,
    name          varchar(50)           not null,
    surname       varchar(70)           not null,
    email         varchar(100)          not null,
    birth_day     date,
    department_id integer               not null
        constraint users_department_id_depart_fk
            references department,
    created       timestamp             not null,
    changed       timestamp             not null,
    is_deleted    boolean default false not null,
    rate_id       integer               not null
        constraint users_rate_id_rate_fk
            references rate,
    middle_name   varchar(60),
    room_id       integer
        constraint users_rooms_id_fk
            references rooms,
    is_confirmed  boolean default false not null
);

alter table users
    owner to postgres;

create unique index users_id_uindex
    on users (id);

create unique index users_email_uindex
    on users (email);

create index users_name_index
    on users (name);

create index users_name_surname_index
    on users (name, surname);

create index users_surname_index
    on users (surname);

create index users_surname_name_middle_name_index
    on users (surname, name, middle_name);

create table user_worked_time
(
    id         bigserial not null
        constraint user_worked_time_pk
            primary key,
    id_users   bigint    not null
        constraint user_worked_time_users_id_fk
            references users,
    work       text,
    start_time timestamp not null,
    end_time   timestamp not null,
    id_issue   bigint
        constraint user_worked_time_issues_from_jira_id_fk
            references issues_from_jira
);

alter table user_worked_time
    owner to postgres;

create unique index user_worked_time_id_time_uindex
    on user_worked_time (id);

create index user_worked_time_end_time_index
    on user_worked_time (end_time);

create index user_worked_time_id_issue_index
    on user_worked_time (id_issue);

create index user_worked_time_start_time_index
    on user_worked_time (start_time);

create table users_role
(
    id         bigserial not null
        constraint users_role_pk
            primary key,
    id_user    bigint    not null
        constraint users_role_users_id_fk
            references users
            on update cascade on delete cascade,
    id_role    integer   not null
        constraint users_role_role_id_fk
            references role
            on update cascade on delete cascade,
    permission jsonb
);

alter table users_role
    owner to postgres;

create index users_role_id_user_index
    on users_role (id_user);

create index users_role_id_role_index
    on users_role (id_role);

create unique index users_role_id_user_id_role_uindex
    on users_role (id_user, id_role);

create table credential
(
    id       bigserial   not null
        constraint credential_pk
            primary key,
    id_users bigint      not null
        constraint credential_users_id_fk
            references users
            on update cascade on delete cascade,
    login    varchar(50) not null,
    password text        not null
);

alter table credential
    owner to postgres;

create unique index credential_id_users
    on credential (id_users);

create unique index credential_login_uindex
    on credential (login);

create index credential_login_password_index
    on credential (login, password);

create unique index rooms_id_uindex
    on rooms (id);

create index rooms_room_number_index
    on rooms (room_number);

create unique index rooms_room_number_uindex
    on rooms (room_number);

create table confirmation_id_table
(
    id       bigserial   not null
        constraint confirmation_id_table_pk
            primary key,
    uuid     varchar(36) not null,
    due_date timestamp   not null,
    user_id  bigserial   not null
        constraint confirmation_id_table_users_id_fk
            references users
);

alter table confirmation_id_table
    owner to postgres;

create unique index confirmation_id_table_id_uindex
    on confirmation_id_table (id);

create unique index confirmation_id_table_uuid_uindex
    on confirmation_id_table (uuid);

