insert into users (email, pass,
                   first_name, last_name,
                   u_role, active)
values ('a', '1', 'admin', 'admin', 'ADMIN', true);

insert into users (email, pass,
                   first_name, last_name,
                   u_role, spec, active)
values ('n', '1', 'Natalya', 'Zaslavskii', 'DOCTOR', 'FAMILY_DOC', true);

insert into users (email, pass,
                   first_name, last_name,
                   u_role, birth, active)
values ('i', '1', 'Ivan', 'Kozka', 'PATIENT', '1999-11-23', true);

insert into users (email, pass,
                   first_name, last_name,
                   u_role, active)
values ('m', '1', 'Mila', 'Koka', 'NURSE', true);