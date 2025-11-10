-- V2__insert_base_data.sql
-- Base data for memberships and products

INSERT INTO memberships (type, org_name, description) VALUES
                                                          ('Loyalty Program', 'Shoppers Drug Mart', 'Shoppers Optimum Rewards Member'),
                                                          ('Association', 'CAA', 'CAA Membership benefits and roadside assistance'),
                                                          ('Wholesale Club', 'Costco', 'Costco Executive Membership'),
                                                          ('Credit Card', 'Visa', 'Visa Infinite Card Member'),
                                                          ('Credit Card', 'American Express', 'Amex Gold Card Benefits'),
                                                          ('Entertainment', 'Cineplex', 'Scene+ Rewards Program Member'),
                                                          ('Frequent Flyer', 'Air Canada', 'Aeroplan Loyalty Member')
ON CONFLICT DO NOTHING;

INSERT INTO products (name, description, company) VALUES
                                                      ('KitKat', 'Chocolate wafer bar', 'Nestlé'),
                                                      ('iPhone 15', 'Latest Apple smartphone', 'Apple'),
                                                      ('Samsung QLED TV', '65-inch 4K Smart TV', 'Samsung'),
                                                      ('Coca-Cola 12-pack', '12-pack of Coca-Cola cans', 'Coca-Cola'),
                                                      ('Air Canada Flight', 'Airline flight ticket', 'Air Canada'),
                                                      ('Hilton Hotel Stay', '1-night stay at a Hilton hotel', 'Hilton Hotels'),
                                                      ('Uber Ride', 'Rideshare service booking', 'Uber Technologies')
ON CONFLICT DO NOTHING;