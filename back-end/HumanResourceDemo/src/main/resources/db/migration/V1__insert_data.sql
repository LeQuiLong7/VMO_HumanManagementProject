INSERT INTO public.employee(
    id, first_name, email, password, role, avatar_url, current_salary, created_at, created_by, current_effort)
VALUES (nextval('employee_id_seq'), 'admin', 'admin@company.com', '$2a$10$e2cgzLiUBBkuRiQwsgV4JuNUEmaEmu5/l1KW1NqGvM6Xa5UTWdqrO', 'ADMIN', '', 100, NOW(), -1, 0),
       (nextval('employee_id_seq'), 'pm', 'pm@company.com', '$2a$10$Moim/Y9okiehT27cKalI/eOsgfCw.6VPf1Z4ZpS76aDHZ/M5pwOBS', 'PM', '', 100,  NOW(), 1, 0),
       (nextval('employee_id_seq'), 'employee', 'employee@company.com', '$2a$10$GTNCA9YNa41iMypjpvW2kOA0pMEXnaEbRe6U441e/zzZuKKqLCac.', 'EMPLOYEE', '', 100, NOW(), 2, 0);
INSERT INTO public.tech_group(
    id, name)
VALUES (nextval('tech_group_id_seq'), 'font end'),
       (nextval('tech_group_id_seq'), 'back end'),
       (nextval('tech_group_id_seq'), 'test');

INSERT INTO public.tech(
    id,  name)
VALUES (nextval('tech_id_seq'),  'react js'),
       (nextval('tech_id_seq'),  'angular js'),
       (nextval('tech_id_seq'),  'vue js'),
       (nextval('tech_id_seq'),  'java'),
       (nextval('tech_id_seq'),  'php'),
       (nextval('tech_id_seq'),  '.net'),
       (nextval('tech_id_seq'),  'selenium');

DO $$
    DECLARE
        start_date DATE := '2024-01-01';
        end_date DATE := CURRENT_DATE - INTERVAL '1 day'; -- Today's date
        currentt_date DATE ;
        employeeId INTEGER;
        effort INTEGER;
    BEGIN
        FOR employeeId IN select id from employee
        LOOP
            currentt_date := start_date;
            WHILE currentt_date <= end_date LOOP
                effort := FLOOR(RANDOM() * 51 + 50);

                INSERT INTO public.effort_history ("employee_id", "date", "effort")
                VALUES (employeeId, currentt_date, effort);

                currentt_date := currentt_date + INTERVAL '1 day';
            END LOOP;
        END LOOP;
END $$;