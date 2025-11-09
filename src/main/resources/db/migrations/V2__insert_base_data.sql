-- V2__insert_base_data.sql
-- Base data for memberships and products

INSERT INTO memberships (type, org_name, description) VALUES
                                                          ('Loyalty', 'Air Miles', 'Air Miles reward program'),
                                                          ('Association', 'CAA', 'Canadian Automobile Association membership'),
                                                          ('Wholesale', 'Costco', 'Costco membership'),
                                                          ('Credit Card', 'Visa', 'Visa card membership offers'),
                                                          ('Credit Card', 'Amex', 'American Express cardmember benefits'),
                                                          ('Entertainment', 'Scene+', 'Scene+ rewards for movies and dining'),
                                                          ('Frequent Flyer', 'Aeroplan', 'Aeroplan / Air Canada loyalty program')
ON CONFLICT DO NOTHING;

INSERT INTO products (name, description, company) VALUES
                                                      ('Flight', 'Airline flight booking', 'Airline/OTA'),
                                                      ('Hotel', 'Hotel reservation/booking', 'Hotel chain'),
                                                      ('Movie ticket', 'Cinema admission', 'Cinemas/Scene+ partners'),
                                                      ('Car rental', 'Rental vehicle booking', 'Rental company'),
                                                      ('Groceries', 'Grocery purchases', 'Retailers'),
                                                      ('Dining', 'Restaurant / dining offers', 'Restaurants'),
                                                      ('Electronics purchase', 'Discount on electronics', 'Retailer')
ON CONFLICT DO NOTHING;
