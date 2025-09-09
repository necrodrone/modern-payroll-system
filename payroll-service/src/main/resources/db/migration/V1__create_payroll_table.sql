-- Create payroll table
CREATE TABLE payroll (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    pay_period_start_date DATE NOT NULL,
    pay_period_end_date DATE NOT NULL,
    gross_pay NUMERIC(15,2) NOT NULL,
    net_pay NUMERIC(15,2) NOT NULL,
    total_taxes NUMERIC(15,2) NOT NULL,
    total_deductions NUMERIC(15,2) NOT NULL,
    total_hours_worked INT NOT NULL
);

-- Optional: create index for faster queries by employee_id
CREATE INDEX idx_payroll_employee_id ON payroll(employee_id);
