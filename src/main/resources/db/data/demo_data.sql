-- demo_data.sql
-- Demo/staging data with bcrypt-encoded passwords

INSERT INTO accounts (username, password) VALUES
                                              ('alice', '$2a$10$EF/QrAQDImosXH3hjDMVc.MdNtbTYa77woTLEgO5Uxk6MMiQcouu6'), -- password1
                                              ('bob', '$2a$10$Ko2dahujHjmOAxBYUAr0QORBapobnfeBg0J4u2RPku9Uk9dJRHTKi'), -- password2
                                              ('charlie', '$2a$10$PEqqVhseiVDJEm5C0md4y.DDFS.d4mQp98q8jDebFr2LB0gyfi202'), -- password3
                                              ('dana', '$2a$10$6oCbDQ9/qWcOsV9Ngb6qSukNBjNcXkN7x/gpnzrgnXs3xNEStcsyS'), -- password4
                                              ('evan', '$2a$10$UwBguP9NV/tPOvEu14Fjauu/VLJ078dy6N8BqZBMq9vvp2dMQsZZO')  -- password5
ON CONFLICT DO NOTHING;

INSERT INTO account_memberships (account_id, membership_id) VALUES
                                                                (1,1),(1,4),(1,6),
                                                                (2,2),(2,3),
                                                                (3,1),(3,5),
                                                                (4,3),(4,6),
                                                                (5,7)
ON CONFLICT DO NOTHING;

INSERT INTO perks (membership_id, product_id, benefit, expiry_date, region, creator_id) VALUES
                                                                                            (1, 5, '10% off flights with Air Miles', '2025-12-31 23:59:59', 'Canada', 1),
                                                                                            (2, 7, '15% off selected car rentals for CAA members', '2025-12-31 23:59:59', 'North America', 2),
                                                                                            (3, 4, '5% cashback on groceries at Costco', '2025-11-02 23:59:59', 'Canada', 3),
                                                                                            (4, 7, '$10 off dining with Visa', '2025-11-04 23:59:59', 'Global', 1),
                                                                                            (5, 6, 'Room upgrade at partner hotels for Amex', '2025-11-06 23:59:59', 'Global', 3),
                                                                                            (6, 1, '2-for-1 Tuesdays at participating cinemas (Scene+)', '2025-11-08 23:59:59', 'Canada', 4),
                                                                                            (7, 5, 'Earn 200 bonus Aeroplan points on select flights', '2025-11-10 23:59:59', 'Global', 5)
ON CONFLICT DO NOTHING;

INSERT INTO perk_upvotes (perk_id, account_id) VALUES
                                                   (1,1),(1,2),(1,3),(1,5),
                                                   (2,2),(2,3),(2,4),
                                                   (3,1),(3,4),
                                                   (4,1),(4,4),
                                                   (6,2),(6,4),(6,5)
ON CONFLICT DO NOTHING;

INSERT INTO perk_downvotes (perk_id, account_id) VALUES
                                                     (3,2),
                                                     (4,3),
                                                     (6,1)
ON CONFLICT DO NOTHING;
