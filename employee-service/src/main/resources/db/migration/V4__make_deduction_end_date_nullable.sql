-- V1.4__make_deduction_end_date_nullable.sql

-- This migration script alters the 'end_date' column of the 'deduction' table
-- to be nullable, allowing for deductions with no specified end date.

ALTER TABLE deduction ALTER COLUMN end_date DROP NOT NULL;
