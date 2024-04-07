INSERT INTO public.employee(
    id, first_name, email, password, role, created_at, created_by)
VALUES (nextval('employee_id_seq'), 'admin', 'admin@company.com', 'password', 'ADMIN', NOW(), -1);

INSERT INTO public.employee(
    id, first_name, email, password, role, created_at, created_by)
VALUES (nextval('employee_id_seq'), 'pm', 'pm@company.com', 'password', 'PM', NOW(), 1);

INSERT INTO public.employee(
    id, first_name, email, password, role, created_at, created_by)
VALUES (nextval('employee_id_seq'), 'employee', 'employee@company.com', 'password', 'EMPLOYEE', NOW(), 2);




insert into client (id, company_name, country, description, logo_url) values (nextval('client_id_seq'),'a', 'a', 'a', 'a');