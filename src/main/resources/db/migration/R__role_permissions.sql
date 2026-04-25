INSERT INTO bank.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM bank.roles r
JOIN bank.permissions p ON p.name IN ('READ_USER', 'WRITE_USER')
WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;