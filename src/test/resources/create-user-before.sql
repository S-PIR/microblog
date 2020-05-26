delete from user_role;
delete from usr;

insert into usr(id, active, password, username) values
(1, true, '$2y$12$HDATaFVBUkiTZEu.TTVVHu20iR1gmFbdNQ5f8nqG4Qe6KL9UqrrZS', 'a'),
(2, true, '$2y$12$HDATaFVBUkiTZEu.TTVVHu20iR1gmFbdNQ5f8nqG4Qe6KL9UqrrZS', 'u');

insert into user_role(user_id, roles) values
(1, 'ROLE_USER'), (1, 'ROLE_ADMIN'),
(2, 'ROLE_USER');