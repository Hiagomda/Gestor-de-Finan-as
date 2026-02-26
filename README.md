# FinanceApp

FinanceApp is a Spring Boot API for personal finance management with JWT authentication.

## Endpoints

- `POST /auth/register`
- `POST /auth/login`
- `GET /api/transactions`
- `POST /api/transactions`
- `PUT /api/transactions/{id}`
- `DELETE /api/transactions/{id}`
- `GET /api/reports?year=YYYY&month=MM`

## Notes

- H2 is configured by default. PostgreSQL settings are in `application.properties` and can be enabled by uncommenting.
- Update `jwt.secret` with a strong random value.
