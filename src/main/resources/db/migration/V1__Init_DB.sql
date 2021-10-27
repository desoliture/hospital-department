create table users(
                      user_id serial primary key,

                      email varchar(255) not null unique,
                      pass varchar(255) not null,

                      first_name varchar(255) not null,
                      last_name varchar(255) not null,

                      u_role varchar(32) not null,
                      spec varchar(255),
                      active bool,
                      birth date
);

create table assignments(
                            assg_id serial primary key,
                            pat_id int not null,

                            assigner_id int not null,
                            assigned_id int not null,

                            assg_type varchar(32) not null,
                            descr text not null,

                            completed boolean not null,

                            creation_date timestamp not null,
                            assg_date timestamp not null,
                            conclusion text,


                            foreign key(pat_id)
                                references users(user_id),
                            foreign key(assigner_id)
                                references users(user_id),
                            foreign key(assigned_id)
                                references users(user_id)
);