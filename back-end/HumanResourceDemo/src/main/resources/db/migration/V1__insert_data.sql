INSERT INTO public.employee(
    id, first_name, email, password, role, created_at, created_by)
VALUES (1, 'admin', 'admin@company.com', '$2a$10$e2cgzLiUBBkuRiQwsgV4JuNUEmaEmu5/l1KW1NqGvM6Xa5UTWdqrO', 'ADMIN', NOW(), -1);

INSERT INTO public.employee(
    id, first_name, email, password, role, created_at, created_by)
VALUES (2, 'pm', 'pm@company.com', '$2a$10$Moim/Y9okiehT27cKalI/eOsgfCw.6VPf1Z4ZpS76aDHZ/M5pwOBS', 'PM', NOW(), 1);

INSERT INTO public.employee(
    id, first_name, email, password, role, created_at, created_by)
VALUES (3, 'employee', 'employee@company.com', '$2a$10$GTNCA9YNa41iMypjpvW2kOA0pMEXnaEbRe6U441e/zzZuKKqLCac.', 'EMPLOYEE', NOW(), 2);




insert into client (id, company_name, country, description, logo_url) values (nextval('client_id_seq'),'a', 'a', 'a', 'a');