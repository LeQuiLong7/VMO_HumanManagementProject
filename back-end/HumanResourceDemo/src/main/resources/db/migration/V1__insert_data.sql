INSERT INTO public.employee(
    id, first_name, email, password, role, current_salary, created_at, created_by)
VALUES (nextval('employee_id_seq'), 'admin', 'admin@company.com', '$2a$10$e2cgzLiUBBkuRiQwsgV4JuNUEmaEmu5/l1KW1NqGvM6Xa5UTWdqrO', 'ADMIN', 100, NOW(), -1);

INSERT INTO public.employee(
    id, first_name, email, password, role, current_salary, created_at, created_by)
VALUES (nextval('employee_id_seq'), 'pm', 'pm@company.com', '$2a$10$Moim/Y9okiehT27cKalI/eOsgfCw.6VPf1Z4ZpS76aDHZ/M5pwOBS', 'PM', 100,  NOW(), 1);

INSERT INTO public.employee(
    id, first_name, email, password, role, current_salary, created_at, created_by)
VALUES (nextval('employee_id_seq'), 'employee', 'employee@company.com', '$2a$10$GTNCA9YNa41iMypjpvW2kOA0pMEXnaEbRe6U441e/zzZuKKqLCac.', 'EMPLOYEE', 100, NOW(), 2);

INSERT INTO public.tech_group(
    id, name)
VALUES (nextval('tech_group_id_seq'), 'font end');

INSERT INTO public.tech_group(
    id, name)
VALUES (nextval('tech_group_id_seq'), 'back end');

INSERT INTO public.tech_group(
    id, name)
VALUES (nextval('tech_group_id_seq'), 'test');

INSERT INTO public.tech(
    id, tech_group_id, name)
VALUES (nextval('tech_id_seq'), 1, 'react js');

INSERT INTO public.tech(
    id, tech_group_id, name)
VALUES (nextval('tech_id_seq'), 1, 'angular js');

INSERT INTO public.tech(
    id, tech_group_id, name)
VALUES (nextval('tech_id_seq'), 1, 'vue js');

INSERT INTO public.tech(
    id, tech_group_id, name)
VALUES (nextval('tech_id_seq'), 2, 'java');

INSERT INTO public.tech(
    id, tech_group_id, name)
VALUES (nextval('tech_id_seq'), 2, 'php');
INSERT INTO public.tech(
    id, tech_group_id, name)
VALUES (nextval('tech_id_seq'), 2, '.net');

INSERT INTO public.tech(
    id, tech_group_id, name)
VALUES (nextval('tech_id_seq'), 3, 'selenium');


INSERT INTO public.employee_tech(
    year_of_experience, employee_id, tech_id)
VALUES (1.5, 3, 1);


INSERT INTO public.employee_tech(
    year_of_experience, employee_id, tech_id)
VALUES (2, 3, 4);



insert into client (id, company_name, country, description, logo_url) values (nextval('client_id_seq'),'a', 'a', 'a', 'a');