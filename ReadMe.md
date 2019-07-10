Bank Account Pay Project

The goal of this mini project is to write a simple micro web service to mimic a "Bank Account". Through this web service, one can query about the balance, deposit money, and withdraw money. Just like any Bank, there are restrictions on how many transactions/amounts it can handle.

To run project input: sbt run.

You can execute next commands:
- Balance request
```
curl localhost:9000/balance

Response example:
Your balance is: $0
```
- Deposit:
```
curl -H "Content-Type: text/json" -X PUT -d "{\"amount\":5}" http://localhost:9000/deposit

Response example:
Successful deposit: $5
```
- Withdrawal:
```
curl -H "Content-Type: text/json" -X PUT -d "{\"amount\":5}" http://localhost:9000/withdrawal

Response example:
Successful withdrawal: $5
```
Possible erros:
```
- Exceeded Maximum Withdrawal Per Day.
- Exceeded Maximum Withdrawal Per Transaction.
- Exceeded Maximum Withdrawal Frequency Per Day.
- Exceeded Account Balance.
- Exceeded Maximum Deposit Per Day.
- Exceeded Maximum Deposit Per Transaction.
- Exceeded Maximum Deposit Frequency Per Day.
```