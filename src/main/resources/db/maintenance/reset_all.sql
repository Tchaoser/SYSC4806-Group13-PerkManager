-- reset_all.sql
-- Clear all data, keep schema, and reset ID sequences.
TRUNCATE TABLE
    perk_upvotes,
    perk_downvotes,
    account_memberships,
    perks,
    products,
    memberships,
    accounts
    RESTART IDENTITY CASCADE;
