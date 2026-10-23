-- TicketFlow demo data
-- Run once on a freshly migrated (empty) database. Password for every demo user: Demo1234
--
-- PowerShell:
--   Get-Content scripts\demo-data.sql | docker exec -i ticketflow-postgres psql -U postgres -d ticketflow

INSERT INTO users (active, created_at, email, full_name, password, role, username) VALUES
  (true, now(), 'admin1@example.com',  'Alex Morgan',   '$2a$10$W8SvNnbpXmv75gs2s5wPcuxy9LEzo2soJeH8cEqs6QGTSywMf54Ny', 'ADMIN',    'admin1'),
  (true, now(), 'agent1@example.com',  'Sam Carter',    '$2a$10$W8SvNnbpXmv75gs2s5wPcuxy9LEzo2soJeH8cEqs6QGTSywMf54Ny', 'AGENT',    'agent1'),
  (true, now(), 'agent2@example.com',  'Taylor Brooks', '$2a$10$W8SvNnbpXmv75gs2s5wPcuxy9LEzo2soJeH8cEqs6QGTSywMf54Ny', 'AGENT',    'agent2'),
  (true, now(), 'marco@example.com',   'Marco Diaz',    '$2a$10$W8SvNnbpXmv75gs2s5wPcuxy9LEzo2soJeH8cEqs6QGTSywMf54Ny', 'CUSTOMER', 'marco'),
  (true, now(), 'lucia@example.com',   'Lucia Torres',  '$2a$10$W8SvNnbpXmv75gs2s5wPcuxy9LEzo2soJeH8cEqs6QGTSywMf54Ny', 'CUSTOMER', 'lucia');

-- SLA deadlines follow the priority: URGENT 4h, HIGH 8h, MEDIUM 24h, LOW 72h.
INSERT INTO tickets (title, description, priority, category, status, sla_breached, sla_deadline,
                     created_at, updated_at, resolved_at, closed_at, created_by_id, assigned_to_id) VALUES
  -- Overdue on purpose: the SLA scheduler flags it (and publishes the Kafka event) within a minute
  ('VPN keeps disconnecting every few minutes',
   'Connection drops while working from home. Tried reconnecting and rebooting the router.',
   'URGENT', 'network', 'IN_PROGRESS', false, now() - interval '2 hours',
   now() - interval '6 hours', now() - interval '3 hours', NULL, NULL,
   (SELECT id FROM users WHERE username = 'marco'), (SELECT id FROM users WHERE username = 'agent1')),

  ('Cannot access company email',
   'Outlook shows an authentication error since this morning.',
   'HIGH', 'email', 'OPEN', false, now() + interval '7 hours',
   now() - interval '1 hour', now() - interval '1 hour', NULL, NULL,
   (SELECT id FROM users WHERE username = 'lucia'), (SELECT id FROM users WHERE username = 'agent2')),

  ('Printer on 2nd floor not working',
   'Paper jam light stays on even after clearing the tray.',
   'MEDIUM', 'hardware', 'RESOLVED', false, now() + interval '14 hours',
   now() - interval '10 hours', now() - interval '2 hours', now() - interval '2 hours', NULL,
   (SELECT id FROM users WHERE username = 'marco'), (SELECT id FROM users WHERE username = 'agent1')),

  ('Request access to the shared drive',
   'Need read and write access to the Finance folder for the quarterly report.',
   'LOW', 'access', 'REOPENED', false, now() + interval '42 hours',
   now() - interval '30 hours', now() - interval '1 hour', now() - interval '20 hours', NULL,
   (SELECT id FROM users WHERE username = 'lucia'), (SELECT id FROM users WHERE username = 'agent2')),

  ('Monitor flickering on desk 14',
   'Replaced the cable, still flickers at 60 Hz.',
   'MEDIUM', 'hardware', 'CLOSED', false, now() - interval '48 hours',
   now() - interval '72 hours', now() - interval '47 hours', now() - interval '52 hours', now() - interval '47 hours',
   (SELECT id FROM users WHERE username = 'marco'), (SELECT id FROM users WHERE username = 'agent1')),

  -- Closed after its deadline: shows "Closed after SLA deadline"
  ('Laptop battery drains in two hours',
   'Battery drops from 100% to 0% in about two hours of normal use.',
   'HIGH', 'hardware', 'CLOSED', true, now() - interval '112 hours',
   now() - interval '120 hours', now() - interval '96 hours', now() - interval '100 hours', now() - interval '96 hours',
   (SELECT id FROM users WHERE username = 'lucia'), (SELECT id FROM users WHERE username = 'agent2')),

  ('Install design software on new workstation',
   'New hire starts Monday and needs the design toolkit installed.',
   'LOW', 'software', 'OPEN', false, now() + interval '67 hours',
   now() - interval '5 hours', now() - interval '5 hours', NULL, NULL,
   (SELECT id FROM users WHERE username = 'marco'), (SELECT id FROM users WHERE username = 'agent1')),

  ('Production database is unreachable from the office network',
   'Reports fail with connection timeouts since 09:40. VPN users are not affected.',
   'URGENT', 'network', 'IN_PROGRESS', false, now() + interval '3 hours',
   now() - interval '1 hour', now() - interval '30 minutes', NULL, NULL,
   (SELECT id FROM users WHERE username = 'lucia'), (SELECT id FROM users WHERE username = 'agent2'));

INSERT INTO ticket_comments (ticket_id, author_id, message, internal, created_at) VALUES
  ((SELECT id FROM tickets WHERE title = 'VPN keeps disconnecting every few minutes'),
   (SELECT id FROM users WHERE username = 'marco'),
   'Still disconnecting after restarting the router.', false, now() - interval '5 hours'),
  ((SELECT id FROM tickets WHERE title = 'VPN keeps disconnecting every few minutes'),
   (SELECT id FROM users WHERE username = 'agent1'),
   'Thanks, we are checking the VPN gateway logs and will update you shortly.', false, now() - interval '4 hours'),
  ((SELECT id FROM tickets WHERE title = 'VPN keeps disconnecting every few minutes'),
   (SELECT id FROM users WHERE username = 'agent1'),
   'Gateway shows repeated session timeouts for this user. Escalate to the network team if it persists.', true, now() - interval '3 hours'),
  ((SELECT id FROM tickets WHERE title = 'Printer on 2nd floor not working'),
   (SELECT id FROM users WHERE username = 'agent1'),
   'Replaced the paper tray sensor. Please confirm that it works.', false, now() - interval '2 hours');
