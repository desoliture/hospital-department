create extension if not exists pgcrypto;

update users set pass = crypt(pass, gen_salt('bf', 8));