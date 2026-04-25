INSERT INTO bank.users (username, email, password, created_by, updated_by)
VALUES (
  'admin',
  'admin@bank.com',
  '$2a$10$hashedpassword...', -- bcrypt
  'system',
  'system'
)
ON CONFLICT (email) DO NOTHING;