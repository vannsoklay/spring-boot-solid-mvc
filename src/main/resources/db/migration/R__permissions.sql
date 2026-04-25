INSERT INTO bank.permissions (name, created_by, updated_by)
VALUES
  ('READ_USER', 'system', 'system'),
  ('WRITE_USER', 'system', 'system')
ON CONFLICT (name) DO NOTHING;