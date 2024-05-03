 create table appointment_types
            (
                id      bigserial
                    constraint appointment_types_pk
                        primary key
                    unique,
                type_name varchar(50)
                    unique
            );

            create unique index appointment_types_id_uindex
                on appointment_types (id);

            create table medicine_categories
            (
                id      bigserial
                    constraint medicine_category_pk
                        primary key
                    unique,
                category_name varchar(50) not null
                    unique
            );

            create unique index medicine_category_id_uindex
                on medicine_categories (id);

            create unique index medicine_category_category_name_uindex
                on medicine_categories (category_name);

            create table roles
            (
                id      serial
                    constraint roles_pk
                        primary key
                    unique,
                role_name varchar(50) not null
                    unique
            );

            create unique index roles_id_uindex
                on roles (id);

            create table appointments_permissions
            (
                role_id             bigint not null
                    constraint appointments_permissions_roles_id_fk
                        references roles,
                appointment_type_id bigint not null
                    constraint appointments_permissions_appointment_types_id_fk
                        references appointment_types
            );

            create unique index appointments_permissions_role_id_appointment_id_uindex
                on appointments_permissions (role_id, appointment_type_id);

            create table users
            (
                id               bigserial
                    constraint users_pk
                        primary key
                    unique,
                first_name    varchar(50)  not null,
                last_name   varchar(50)  not null,
                date_of_birth    date         not null,
                email            varchar(100) not null
                    unique,
                phone            varchar(15)  not null
                    unique,
                users_password         varchar(500) not null,
                role_id          bigint       not null
                    constraint users_roles_id_fk
                        references roles
                        on delete cascade,
                is_being_treated boolean
            );

            create table doctors
            (
                user_id              bigint not null
                    constraint doctors_users_id_fk
                        references users
                        on delete cascade,
                medicine_category_id bigint not null
                    constraint doctors_medicine_categories_id_fk
                        references medicine_categories
                        on delete cascade,
                constraint doctors_pk
                    primary key (user_id, medicine_category_id)
            );

            create unique index doctors_user_id_medicine_category_id_uindex
                on doctors (user_id, medicine_category_id);

            create table diagnosis_types
            (
                id      bigserial
                    primary key
                    unique,
                type_name varchar(50)
                    unique

            );

            create table diagnoses
            (
                id                serial
                    constraint diagnosis_pk
                        primary key
                    unique,
                text_description           varchar(500),
                diagnosis_type_id bigint
                    constraint diagnoses_diagnosis_types_id_fk
                        references diagnosis_types
                        on delete cascade
            );

            create table appointments
            (
                id                  bigserial
                    constraint appointments_pk
                        primary key
                    unique,
                patient_id          bigint not null
                    constraint appointments_users_patients_id_fk
                        references users
                        on delete cascade,
                doctor_id           bigint not null
                    constraint appointments_users_doctors_id_fk
                        references users
                        on delete cascade,
                appointment_type_id bigint not null
                    constraint appointments_appointment_types_id_fk
                        references appointment_types
                        on delete cascade,
                diagnosis_id        bigint
                    constraint appointments_diagnoses_id_fk
                        references diagnoses
                        on delete cascade,
                appointment_date    timestamp   not null
            );

            create unique index appointments_id_uindex
                on appointments (id);

            create unique index appointments_diagnosis_id_uindex
                on appointments (diagnosis_id);

            create unique index diagnosis_id_uindex
                on diagnoses (id);
 insert into roles (role_name)
            values ('DOCTOR'),
                   ('NURSE'),
                   ('PATIENT'),
                   ('ADMIN');

            insert into appointment_types(type_name)
            values ('CONSULTATION'),
                   ('OPERATION'),
                   ('PROCEDURE'),
                   ('MEDICATION');

            --- DOCTORS
            insert into appointments_permissions (role_id, appointment_type_id)
            select r.id, a.id
            from roles r,
                 appointment_types a
            where r.role_name = 'DOCTOR';

            --- NURSES
            insert into appointments_permissions (role_id, appointment_type_id)
            select r.id, a.id
            from roles r,
                 appointment_types a
            where r.role_name = 'NURSE'
              and a.type_name != 'OPERATION';

            --- ADMINS
            insert into appointments_permissions (role_id, appointment_type_id)
            select r.id, a.id
            from roles r,
                 appointment_types a
            where r.role_name = 'ADMIN';

            insert into medicine_categories(category_name)
            values ('PEDIATRICIAN'),
                   ('TRAUMATOLOGIST'),
                   ('SURGEON'),
                   ('NEUROLOGISTS');

            insert into diagnosis_types(type_name)
            values ('TREATING'),
                   ('SOUND_HEALING'),
                   ('MEDITATION'),
                   ('OPERATION'),
                   ('CLINICAL_EXAMINATION'),
                   ('TREATING_IS_FINISHED'),
                   ('INTENSIVE_TREATING');
 create table users_doctors
            (
                user_id   bigint not null,
                doctor_id bigint not null
            );

            create unique index users_doctors_user_id_doctor_id_uindex
                on users_doctors (user_id, doctor_id);
 alter table appointments
                add canceled bool default false not null;

 -- USERS TABLE ---
-- patients
-- password = 'patient{patient_num}' e.g. 'patient1', 'patient15'
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Руслан',
                   'Черненко',
                   '1982-10-17',
                   'patient1@gmail.com',
                   '+380395335151',
                   '$2b$10$mZCmdsH8D6JFNCFq219X.ubWyoFT8pxg.e6KJOQz3PXb3muU3ZpOi',
                   r.id,
                   true
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Михайло',
                   'Шапіро',
                   '2002-03-31',
                   'patient2@gmail.com',
                   '+380399100801',
                   '$2b$10$Xugaqb.DEPPkmZmVuojVguvMgrDQq4VtYZhjl7.4K97dGDWZckZkC',
                   r.id,
                   true
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Софія',
                   'Мілованов',
                   '1976-07-17',
                   'patient3@gmail.com',
                   '+380398705172',
                   '$2b$10$eUGLhHZJOqkh1qr.OViS9.P.Gt7658h2Ojb25s/xe41Nw7LVxILfq',
                   r.id,
                   true
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Максим',
                   'Попенко',
                   '2002-11-22',
                   'patient4@gmail.com',
                   '+380393450559',
                   '$2b$10$kBQFOngWt6WovH05PujUCOYdq7f1cTxavGY2nytmqMU0W1r8By2w2',
                   r.id,
                   true
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Марія',
                   'Коломийцева',
                   '1970-02-22',
                   'patient5@gmail.com',
                   '+380396663302',
                   '$2b$10$B8TletknH/S1efxDnI91B.VzmLZglQDz4XUKZQcQ2qXGF9XF7kQnm',
                   r.id,
                   true
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Maria',
                   'Lando',
                   '1973-08-12',
                   'patient6@gmail.com',
                   '+380390348683',
                   '$2b$10$nxZWhs7zGgDH4Kg1Nz8aUuAAoysuKhpTupc4UUpKeFSXMR1GW4AQm',
                   r.id,
                   true
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Camir',
                   'Shevchenko',
                   '1976-03-25',
                   'patient7@gmail.com',
                   '+380391226159',
                   '$2b$10$uZ6Y3IEzKsQOCSHFQkfse.uY5HUZAZnrDa9U/R25VtzFz9XDbVfRu',
                   r.id,
                   true
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Rudolph',
                   'Fortune',
                   '1990-04-10',
                   'patient8@gmail.com',
                   '+380394543974',
                   '$2b$10$msv5BKs1asZBbU8daVLhKODjvhL5IlgcNcwKUNXVn5FbqLuzxbL46',
                   r.id,
                   true
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Mikhailo',
                   'Banner',
                   '1993-08-07',
                   'patient9@gmail.com',
                   '+380399393189',
                   '$2b$10$EmD2cKNURGS6DBuhvNmd7ugjCtu.ZGfWnzEVrLFOOxl9bWgSk1jtO',
                   r.id,
                   true
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Fedora',
                   'Bourne',
                   '1963-05-08',
                   'patient10@gmail.com',
                   '+380674414021',
                   '$2b$10$W7i9mZc30VwBZ28Or/5/ke3HpsX7UEeCNduEEyw0VfHH7O1NJUZWe',
                   r.id,
                   true
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Олена',
                   'Сергієнко',
                   '1971-06-20',
                   'patient11@gmail.com',
                   '+380392434835',
                   '$2b$10$O.N/Xw5zdaqny1.uPobwju6lgpL4eaMjOQJ.yvk12fdbzbLS3iEj.',
                   r.id,
                   true
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Марина',
                   'Іванченко',
                   '1970-12-03',
                   'patient12@gmail.com',
                   '+380397148274',
                   '$2b$10$VFqe92PhJ6.6a3yryaKuIe4CLNoiPwzMov92Sy1kIpfw6gKzgVv66',
                   r.id,
                   true
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Валерій',
                   'Захарчук',
                   '1968-01-04',
                   'patient13@gmail.com',
                   '+380398600919',
                   '$2b$10$bqWPcIvFdXtyrAW7D6lPm.CxbWtZpO1pT/MJpjrZAbPffEWti3Qa6',
                   r.id,
                   false
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Ніна',
                   'Мірошниченко',
                   '2003-05-20',
                   'patient14@gmail.com',
                   '+380736909579',
                   '$2b$10$REeFn1LQf0hKH4KcGXJie.caX7W9eRzz1zkSRN3AknvJILEbub3Fy',
                   r.id,
                   false
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Поліна',
                   'Іванівна',
                   '1981-12-27',
                   'patient15@gmail.com',
                   '+380399037529',
                   '$2b$10$DMR7Tr/v8eiw4zIJo/0VNeYhcYchk8vP8eb0pk82DPmNdP/jjnyGm',
                   r.id,
                   false
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Георгій',
                   'Васильєв',
                   '1975-11-04',
                   'patient16@gmail.com',
                   '+380398982447',
                   '$2b$10$x9uMBMVgeZt.YhOQ7fCnkuRSlVFdcrrXKXyVrZVCVx7cqV5KX40sq',
                   r.id,
                   false
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Поліна',
                   'Боднаренко',
                   '1960-03-20',
                   'patient17@gmail.com',
                   '+380397548423',
                   '$2b$10$CnLJP3AJlk5LuPg7zMtQWuHIXz1J9fJqjhWhIMuV1NVDlHp6S/20y',
                   r.id,
                   false
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Анастасія',
                   'Дмитренко',
                   '1961-11-17',
                   'patient18@gmail.com',
                   '+380738443045',
                   '$2b$10$P9CYR2vizZCm7FgaHBGoR.f6/qoaqIlrmldaQO7IjfpaNr9QH/1Im',
                   r.id,
                   false
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Ілля',
                   'Шевчук',
                   '1983-12-16',
                   'patient19@gmail.com',
                   '+380393743490',
                   '$2b$10$XdzDwfLNjUKWismu2g3CGOU98whobyDHDp8/AqUW086HUUO3xL0Pq',
                   r.id,
                   false
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Лариса',
                   'Романченко',
                   '1963-05-26',
                   'patient20@gmail.com',
                   '+380398902394',
                   '$2b$10$OFfDoBuLp04asCGl9aYPrepvL/1dIlIFdoiQnYNcB1SXNqM/SzV6.',
                   r.id,
                   false
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Всеволод',
                   'Гнатюк',
                   '1961-06-30',
                   'patient21@gmail.com',
                   '+380397776947',
                   '$2b$10$4SPZQgVfqvXm5aOjrHj7qOFsgn4.fjXuGCOdGpA3U2mtvFyKASS1y',
                   r.id,
                   false
            from roles r
            where role_name = 'PATIENT';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Назар',
                   'Середа',
                   '1967-07-18',
                   'patient22@gmail.com',
                   '+380397713488',
                   '$2b$10$BtXGyZpfNPNasVPamjhiAem5V/B2q6eFIFvTprkXa34cB6skGbFIq',
                   r.id,
                   false
            from roles r
            where role_name = 'PATIENT';

            -- doctors
-- password = 'doctor{doctor_num}' e.g. 'doctor1', 'doctor8'
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Адам',
                   'Василенко',
                   '1979-09-06',
                   'doctor1@gmail.com',
                   '+380399180308',
                   '$2b$10$EHLXe/PWKz.Q0W06dX/NreFkYAkM7iE.fU3qx2Va0JAp762STZf56',
                   r.id,
                   false
            from roles r
            where role_name = 'DOCTOR';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Федір',
                   'Микуличин',
                   '1993-07-04',
                   'doctor2@gmail.com',
                   '+380634808200',
                   '$2b$10$5y2k58vR.rhjhuV6TObhV.H53WD2HORPTJKkfrDbWUqs4I3ndI0lG',
                   r.id,
                   false
            from roles r
            where role_name = 'DOCTOR';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Костянтин',
                   'Шевчук',
                   '1993-12-02',
                   'doctor3@gmail.com',
                   '+380502290730',
                   '$2b$10$sGSlXtaOB6gKlVshn08d/eznxBxDwVAHVniFqLvabZRqCSNndR80u',
                   r.id,
                   false
            from roles r
            where role_name = 'DOCTOR';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Галина',
                   'Йосипівна',
                   '1992-02-06',
                   'doctor4@gmail.com',
                   '+380729138592',
                   '$2b$10$ADV4BtGVsUiU6rzRWLnG2.uJy1EG4zUk/gSqL1FNhYEjxPFzTqUbe',
                   r.id,
                   false
            from roles r
            where role_name = 'DOCTOR';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Sherley',
                   'Blackman',
                   '1982-12-07',
                   'doctor5@gmail.com',
                   '+380396214042',
                   '$2b$10$kIoli7TDzUC4Z.4bK6Pwi.pWs2KDqebciX2vCRs0f2WN6XVcudB9W',
                   r.id,
                   false
            from roles r
            where role_name = 'DOCTOR';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Timotha',
                   'Lowe',
                   '1992-02-17',
                   'doctor6@gmail.com',
                   '+380398503495',
                   '$2b$10$PSzf46MfVIngqNxZOPmsZOFgVi/G3mVXzQkzm4dHg1MLlvoxTQCse',
                   r.id,
                   false
            from roles r
            where role_name = 'DOCTOR';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Balfour',
                   'Matthewson',
                   '1993-04-02',
                   'doctor7@gmail.com',
                   '+380393278927',
                   '$2b$10$a4f1IjvI6s1NFH5VRhEHX.V1WY5ufB8xfeNpzfR.oXJUNfKR8MIFK',
                   r.id,
                   false
            from roles r
            where role_name = 'DOCTOR';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Khrystyna',
                   'Butts',
                   '1961-11-21',
                   'doctor8@gmail.com',
                   '+380506143554',
                   '$2b$10$7tzNeJxYSa0dGgDeZcUll.7uDubt0PyB0ZYV00UWWrDNkenVyDKXO',
                   r.id,
                   false
            from roles r
            where role_name = 'DOCTOR';

            -- nurses
-- password = 'nurse{nurse_num}' e.g. 'nurse1', 'nurse2'
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Поліна',
                   'Кравченко',
                   '2002-02-04',
                   'nurse1@gmail.com',
                   '+380395734652',
                   '$2b$10$0aUpk8asyzUZ81bko2c0Ue/Glar9G33aTSg2RyP3G12uuBVTokZ3G',
                   r.id,
                   false
            from roles r
            where role_name = 'NURSE';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Taisiya',
                   'Rickard',
                   '2002-09-19',
                   'nurse2@gmail.com',
                   '+380394909530',
                   '$2b$10$MBIZxInKPWIwvOPpVKGV9eLcmgOFHkruKDCp78C.TYcYKYglhvrE.',
                   r.id,
                   false
            from roles r
            where role_name = 'NURSE';
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Анна',
                   'Кравцова',
                   '1960-06-27',
                   'nurse3@gmail.com',
                   '+380395175832',
                   '$2b$10$jD4MbR1EKSK6fxZZAd0E8.bIaP4txIku2C.wpTE7Qwm1KZrCWWEda',
                   r.id,
                   false
            from roles r
            where role_name = 'NURSE';

            -- admin
-- password = 'admin'
            insert into users (first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
            select 'Viktor',
                   'Oblapenko',
                   '1988-06-13',
                   'viktor.oblapenko@gmail.com',
                   '+380984096936',
                   '$2b$10$i4cHOY2zQZYSsXlNCMdy2OTcXLgXJDMnjkLVvLF2lpgDJuTGg5ngu',
                   r.id,
                   false
            from roles r
            where role_name = 'ADMIN';


            --- USERS_DOCTORS TABLE ---
--- doctor 1, patients 1, 2, 3, 4, 15, 16, 17, 18, 19, 20, 21, 22
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient1@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient2@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient3@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient4@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient15@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient16@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient17@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient18@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient19@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient20@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient21@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient22@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com');

--- doctor 3, patients 5, 6, 7, 8, 9
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient5@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor3@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient6@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor3@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient7@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor3@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient8@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor3@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient9@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor3@gmail.com');

--- doctor 4, patient 10
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient10@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor4@gmail.com');

--- doctor 5, patients 11, 12, 13
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient11@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor5@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient12@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor5@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient13@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor5@gmail.com');

--- doctor 6
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient14@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor6@gmail.com');
            insert into users_doctors(user_id, doctor_id)
            select (select patients.id from users patients where patients.email = 'patient15@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor6@gmail.com');


--- DOCTORS TABLE ---
            insert into doctors (user_id, medicine_category_id)
            select (select id from users where email = 'doctor1@gmail.com'),
                   (select id from medicine_categories where category_name = 'PEDIATRICIAN');
            insert into doctors (user_id, medicine_category_id)
            select (select id from users where email = 'doctor2@gmail.com'),
                   (select id from medicine_categories where category_name = 'PEDIATRICIAN');
            insert into doctors (user_id, medicine_category_id)
            select (select id from users where email = 'doctor3@gmail.com'),
                   (select id from medicine_categories where category_name = 'TRAUMATOLOGIST');
            insert into doctors (user_id, medicine_category_id)
            select (select id from users where email = 'doctor4@gmail.com'),
                   (select id from medicine_categories where category_name = 'TRAUMATOLOGIST');
            insert into doctors (user_id, medicine_category_id)
            select (select id from users where email = 'doctor5@gmail.com'),
                   (select id from medicine_categories where category_name = 'SURGEON');
            insert into doctors (user_id, medicine_category_id)
            select (select id from users where email = 'doctor6@gmail.com'),
                   (select id from medicine_categories where category_name = 'SURGEON');
            insert into doctors (user_id, medicine_category_id)
            select (select id from users where email = 'doctor7@gmail.com'),
                   (select id from medicine_categories where category_name = 'NEUROLOGISTS');
            insert into doctors (user_id, medicine_category_id)
            select (select id from users where email = 'doctor8@gmail.com'),
                   (select id from medicine_categories where category_name = 'NEUROLOGISTS');

   -- APPOINTMENTS --
            -- patient 1
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 1',
                (select id from diagnosis_types where type_name = 'TREATING'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient1@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'CONSULTATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2020-04-16 13:30',
                   false;
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 1',
                (select id from diagnosis_types where type_name = 'SOUND_HEALING'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient1@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'MEDICATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2020-04-16 13:30',
                   false;
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                  ('Description of complaints, symptoms, and course of illness Patient 1',
                  (select id from diagnosis_types where type_name = 'MEDITATION'))
                   returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient1@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2020-05-14 11:30',
                   false;
            --patient2
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 2',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient2@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2020-03-07 15:33',
                   false;
            --patient3
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 3',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient3@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2020-07-12 08:22',
                   false;
            --patient4
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 4',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient4@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2020-02-28 11:30',
                   false;
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 4',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient4@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2020-03-10 13:20',
                   false;
            --patient5
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 5',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient5@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor3@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'CONSULTATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-02-11 11:30',
                   false;
            --patient6
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 6',
                (select id from diagnosis_types where type_name = 'SOUND_HEALING'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient6@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor3@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'MEDICATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-10-17 16:41',
                   false;
            --patient7
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 7',
                (select id from diagnosis_types where type_name = 'CLINICAL_EXAMINATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient7@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor3@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'PROCEDURE'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-03-09 13:14',
                   false;
            --patient8
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 8',
                (select id from diagnosis_types where type_name = 'TREATING'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient8@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor3@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'CONSULTATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-04-27 09:14',
                   false;
            --patient9
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 9',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient9@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor3@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-05-02 11:30',
                   false;
            --patient10
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 10',
                (select id from diagnosis_types where type_name = 'TREATING'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient10@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor4@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'CONSULTATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-06-13 14:47',
                   false;
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 10',
                (select id from diagnosis_types where type_name = 'SOUND_HEALING'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient10@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor8@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'PROCEDURE'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-06-14 17:15',
                   false;
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 10',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient10@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor5@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'PROCEDURE'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-06-15 09:19',
                   false;
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 10',
                (select id from diagnosis_types where type_name = 'CLINICAL_EXAMINATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient10@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor7@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'MEDICATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-06-16 10:28',
                   false;
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 10',
                (select id from diagnosis_types where type_name = 'OPERATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient10@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor6@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-06-17 11:23',
                   false;
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 10',
                (select id from diagnosis_types where type_name = 'INTENSIVE_TREATING'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient10@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor3@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-06-18 12:36',
                   false;
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 10',
                (select id from diagnosis_types where type_name = 'TREATING'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient10@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor2@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'CONSULTATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-06-19 08:00',
                   false;
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 10',
                (select id from diagnosis_types where type_name = 'TREATING_IS_FINISHED'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient10@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor1@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'CONSULTATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-06-20 09:01',
                   false;
            --patient11
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 11',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient11@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor5@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-06-18 11:30',
                   false;
            --patient12
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 12',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient12@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor5@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'CONSULTATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-07-09 15:31',
                   false;
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 12',
                (select id from diagnosis_types where type_name = 'INTENSIVE_TREATING'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient12@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor5@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-07-20 11:59',
                   false;
            --patient13
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 13',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient13@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor5@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-08-17 09:26',
                   true;
            --patient14
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 14',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient14@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor6@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-09-08 10:53',
                   true;
            --patient15
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 15',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient15@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor6@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-10-03 18:40',
                   true;
            --patient16
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 16',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient16@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor2@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-10-30 16:11',
                   false;
            --patient17
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 17',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient17@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor2@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-11-14 12:24',
                   false;
            ----patient18
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 18',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient18@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor8@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2021-12-21 09:56',
                   false;
            --patient19
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 19',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient19@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor8@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2022-01-04 11:12',
                   false;
            --patient20
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 20',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient20@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor8@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2022-01-29 13:13',
                   false;
            --patient21
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 21',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient21@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor7@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2022-02-11 11:11',
                   false;
            --patient22--
            with diagnosis_insert as (
            insert
            into diagnoses (text_description, diagnosis_type_id)
            values
                ('Description of complaints, symptoms, and course of illness Patient 22',
                (select id from diagnosis_types where type_name = 'MEDITATION'))
                returning id as diagnosis_id)
            insert
            into appointments (patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date,
                               canceled)
            select (select patients.id from users patients where patients.email = 'patient22@gmail.com'),
                   (select doctors.id from users doctors where doctors.email = 'doctor7@gmail.com'),
                   (select apt.id from appointment_types apt where type_name = 'OPERATION'),
                   (select diagnosis_id from diagnosis_insert),
                   '2022-03-15 15:15',
                   false;
