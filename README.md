# Modern Payroll System

This project is a microservices-based payroll system built with Spring Boot, PostgreSQL, RabbitMQ, and Docker.

# API Documentation
http://localhost:8080/swagger-ui/index.html for Auth-service
http://localhost:8081/api/swagger-ui/index.html for Employee-service
http://localhost:8082/api/swagger-ui/index.html for Attendance-service
http://localhost:8083/api/swagger-ui/index.html for Leave-service

# TODO

🔹 1. Service List & Responsibility
auth-service: Login, roles, JWT tokens. - ✅

employee-service: Personal data, employment status. - ✅

attendance-service: Time logs, Shifts, Employee shifts. - ✅

leave-service: Leaves, balances, requests, approvals. - (Leaves - ✅)

payroll-service: Salary computation, deductions, bonuses.

payslip-service: Generates payslips and sends notifications.

tax-service: Tax rules and computations (BIR, SSS, etc).

notification-service: Emails(RabbitMQ) and SMS(Twilio). - ✅

scheduler-service: Cron jobs for payroll generation.

🔹 2. Tech per Service
Component	        Stack
Language	        Java (Spring Boot)
DB	                PostgreSQL
Message Queue	    RabbitMQ or Apache Kafka
Containerization	Docker
Orchestration	    Kubernetes
CI/CD	            GitHub Actions or GitLab CI
API Gateway	        Spring Cloud Gateway or Kong
Auth	            JWT with Spring Security

🔹 3. Common Features
✅ RESTful APIs

✅ Central config (Spring Cloud Config)

✅ Eureka service discovery (or Consul)

✅ Distributed tracing (with Sleuth + Zipkin)

✅ Centralized logging (ELK stack)

