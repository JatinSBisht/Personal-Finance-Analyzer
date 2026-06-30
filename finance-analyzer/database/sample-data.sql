insert into categories (name, type) values
('Salary', 'INCOME'),
('Freelance', 'INCOME'),
('Investments', 'INCOME'),
('Food', 'EXPENSE'),
('Rent', 'EXPENSE'),
('Transport', 'EXPENSE'),
('Shopping', 'EXPENSE'),
('Utilities', 'EXPENSE'),
('Health', 'EXPENSE'),
('Education', 'EXPENSE')
on conflict do nothing;
