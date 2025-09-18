# Modern Payroll System

This project is a microservices-based payroll system built with Spring Boot, PostgreSQL, RabbitMQ, and Docker.

# API Documentation
http://localhost:8080/swagger-ui/index.html for Auth-service
http://localhost:8081/api/swagger-ui/index.html for Employee-service
http://localhost:8082/api/swagger-ui/index.html for Attendance-service
http://localhost:8083/api/swagger-ui/index.html for Leave-service
port:8084 for notification-service
http://localhost:8085/api/swagger-ui/index.html for holiday-service
http://localhost:8086/api/swagger-ui/index.html for payroll-service
http://localhost:8087/api/swagger-ui/index.html for tax-service

# TODO

🔹 1. Service List & Responsibility
auth-service: Login, roles, JWT tokens. - ✅

employee-service: Personal data, employment status. - ✅

attendance-service: Time logs, Shifts, Employee shifts. - ✅

leave-service: Leaves, balances, requests, approvals. - ✅

holiday-service: Holidays, Suspensions, Non-working Days. - ✅ 

payroll-service: Salary computation, deductions, bonuses. - ✅ (no overtimes yet)

payslip-service: Generates payslips and sends notifications.

overtime-service: Handle Overtimes.

tax-service: Tax rules and computations . - ✅

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

