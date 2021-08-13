insert into public.department (id, department_name)
values (1, 'QA engineer'),
       (2, 'Software engineer'),
       (3, 'Algorithmist');

insert into public.rate (id, salary_rate, work_hour, work_hour_short_day)
values (1, 0.25, 2, null),
       (2, 0.50, 4, null),
       (3, 0.75, 6, null),
       (4, 1.00, 8, 7),
       (5, 1.25, 10, null),
       (6, 1.50, 14, null);

insert into public.role (id, role_name)
values (1, 'ROLE_ADMIN'),
       (3, 'ROLE_USER'),
       (2, 'ROLE_MODERATOR');

insert into public.rooms (id, room_number)
values (1, '207a'),
       (2, '209'),
       (3, '210a'),
       (4, '213a'),
       (5, '203a'),
       (6, '328'),
       (7, '205');

insert into public.users (id, name, surname, email, birth_day, department_id, created, changed,
                          is_deleted, rate_id, middle_name, room_id, is_confirmed)
values (14, 'Ivan', 'Ivanov', 'tester@bsuir.by', '1985-03-25', 2, '2021-06-23 23:36:15.975000',
        '2021-06-23 23:36:15.975000', false, 1, null, 1, true),
       (3, 'Ратмир', 'БОБОВНИКОВА', 'vqyzo@bsuir.by', '1999-02-08', 2, '2021-06-02 01:04:58.825000',
        '2021-06-02 01:04:59.208000', true, 4, 'nRDhDCrVMjDwEq', 2, true),
       (5, 'Vika', 'Davydova', 'vika_1985@emc.lab', '1985-07-21', 3, '2021-06-13 23:05:45.996000',
        '2021-06-13 23:05:45.996000', false, 4, null, 1, true),
       (36, 'Юрий', 'Фролов', 'al-hrom-3@mail.ru.2', '2019-04-03', 1, '2021-07-20 23:14:47.560000',
        '2021-07-20 23:14:47.560000', false, 5, 'Вл', 5, false),
       (2, 'Александр', 'Ахрамович', 'al-hrom-3@mail.ru', '1983-09-29', 1,
        '2021-05-27 23:15:12.000000', '2021-05-27 23:15:17.000000', false, 4, 'Владимирович', 3,
        true),
       (37, 'Сергей', 'Прохоров', 'al-hrom-3@nnnnmail.ru', '2000-04-03', 2,
        '2021-07-21 00:26:42.759000', '2021-07-21 00:26:42.759000', false, 4, 'Вл', 2, false);


insert into public.credential (id, id_users, login, password)
values (2, 2, 'hrom', '$2a$10$beBjYbOcNByKAk2g7ley5uvfMlFPUXUa61eyxDjhWqGMQTUCtICVy'),
       (1, 5, 'vikasolnce', '$2a$10$lBmoBvy1b/H02Y98JzbUlObFI7fX4SnEEp4FOpQtLs061tm505lZS'),
       (9, 14, 'tester', '$2a$10$0sFwXPVifpiha8Eqmowzp.vFuikhXYRkvKviXf5Rz2oLToSNv3TTi'),
       (3, 3, 'ratmir_1', '$2a$10$0JI3lW2i4rQLhrS6ZGVQFukHaFiyAnw9eIciylbLgySO7JpzHZscy');


insert into public.users_role (id, id_user, id_role, permission)
values (2, 2, 1, null),
       (3, 3, 2, null),
       (5, 5, 2, null),
       (6, 2, 2, null),
       (7, 2, 3, null),
       (8, 14, 1, null),
       (9, 14, 2, null),
       (10, 14, 3, null);

insert into public.confirmation_id_table (id, uuid, due_date, user_id)
values (1, '328cad31-8e0d-4eea-ac43-3b57c52dcf64', '2021-07-22 00:20:03.184000', 37);