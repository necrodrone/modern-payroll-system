-- V1.6__alter_daily_rate_nullable.sql

-- This migration script alters the 'daily_rate' column of the 'employee' table
-- to be nullable.

ALTER TABLE employee ALTER COLUMN daily_rate DROP NOT NULL;
