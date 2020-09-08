-- Decoded password = "fakePassword"
INSERT INTO users (id, username, password, avatar, email, verified) VALUES(1, 'fakeUser', '$2a$10$j9.NesK.eKNiae8yWL9nqu3M1Q06NhQZr.JC/O/h.m6wyBKkV8BJi', 'default.png', 'fakeEmail@fake.com', 1);
INSERT INTO verification_tokens (id, expiry_date, token, user_id) VALUES (1, '2020-06-05 12:00:00', 'cbdf035e-e2ce-11ea-87d0-0242ac130003', 1);