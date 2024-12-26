# Payment Gateway Service

ApiPay: A payment service that integrates with multiple payment providers to process credit card transactions.

## Getting Started

### Prerequisites
- Java 17+
- Maven or Gradle
- Port 9999 available for mock providers

### Running the Application
1. Clone the repository
2. Run the application:
```bash
./gradlew bootRun
```
or execute main function at ApiPayApp.kt

3. The application will be available at http://localhost:8080

4. Endpoints:

### POST /payments
#### Request examples:
```json
{
  "amount": 900,
  "currency": "BRL",
  "description": "payment order #1234",
  "paymentMethod": {
    "type": "card",
    "card": {
      "number": "4111111111111111",
      "holderName": "John Doe",
      "cvv": "123",
      "expirationDate": "11/2025",
      "installments": 3
    }
  }
}
```

#### Response examples:
```json
{
  "transactionId": "3af5f7fa-cc91-4af4-b0cd-7d8da6e2d5d0",
  "provider": "FIRST_PROVIDER",
  "message": "Pagamento processado com sucesso"
}
```

### POST /refunds
#### Request
```json
{
  "transactionId": "3af5f7fa-cc91-4af4-b0cd-7d8da6e2d5d0",
  "amount": 900
}
```

#### Response:
```json
{
  "transactionId": "3af5f7fa-cc91-4af4-b0cd-7d8da6e2d5d0",
  "provider": "FIRST_PROVIDER",
  "message": "Pagamento estornado com sucesso"
}
```

## Test Scenarios

The mock server provides different responses based on the payment description, allowing simulation of various scenarios:

### 1. First Provider Failure
To simulate a failure in the first payment provider, include this text in the payment description:
```json
{
  "amount": 900,
  "currency": "BRL",
  "description": "First provider will fail",
  "paymentMethod": {
    "type": "card",
    "card": {
      "number": "4111111111111111",
      "holderName": "John Doe",
      "cvv": "123",
      "expirationDate": "11/2025",
      "installments": 3
    }
  }
}
```

### 2. Both Providers Failure
To simulate a failure in both payment providers, include this text in the payment description:
```json
{
  "amount": 900,
  "currency": "BRL",
  "description": "Every provider will fail",
  "paymentMethod": {
    "type": "card",
    "card": {
      "number": "4111111111111111",
      "holderName": "John Doe",
      "cvv": "123",
      "expirationDate": "11/2025",
      "installments": 3
    }
  }
}
```

### To run tests locally
```bash
./gradlew test
```
or execute tests classes at test folder: CreatePaymentApplicationTest and RefundPaymentApplicationTest
