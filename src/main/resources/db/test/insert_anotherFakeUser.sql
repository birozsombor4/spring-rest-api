-- Decoded password = "fakePassword"
INSERT INTO users (id, username, password, avatar, email, verified) VALUES(2, 'fakeUser2', '$2a$10$j9.NesK.eKNiae8yWL9nqu3M1Q06NhQZr.JC/O/h.m6wyBKkV8BJi', 'default.png', 'fakeEmail@fake.com', 1);
INSERT INTO verification_tokens (id, expiry_date, token, user_id) VALUES (2, '2020-06-05 12:00:00', '589c5730-3bcb-4eb9-a971-4446f688ab9d', 2);
