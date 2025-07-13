INSERT INTO public."user" (id, username, currency, firstname, surname, photo, photo_small, full_name)
VALUES ('a9165b45-1111-47d6-ac50-43611d624421', 'alice', 'RUB', null, null, null, null, null),
       ('a9165b45-2222-47d6-ac50-43611d624421', 'bob', 'RUB', null, null, null, null, null);

INSERT INTO friendship (requester_id, addressee_id, status, created_date)
VALUES ('a9165b45-1111-47d6-ac50-43611d624421', 'a9165b45-2222-47d6-ac50-43611d624421', 'PENDING', now());