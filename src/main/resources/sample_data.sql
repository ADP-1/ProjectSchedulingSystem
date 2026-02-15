INSERT INTO projects (title, deadline, revenue, status) VALUES
                                                            ('E-commerce Website UI Design', 3, 15000.00, 'PENDING'),
                                                            ('Mobile App Development', 5, 25000.00, 'PENDING'),
                                                            ('Database Migration', 2, 10000.00, 'PENDING'),
                                                            ('API Integration', 4, 18000.00, 'PENDING'),
                                                            ('Security Audit', 1, 8000.00, 'PENDING'),
                                                            ('Cloud Deployment', 3, 20000.00, 'PENDING'),
                                                            ('Performance Testing', 5, 12000.00, 'PENDING'),
                                                            ('Bug Fixes', 2, 5000.00, 'PENDING');

-- Verify data
SELECT * FROM projects ORDER BY revenue DESC;