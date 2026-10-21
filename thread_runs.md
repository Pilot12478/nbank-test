# Тесты по потокам

Прогон: 31 тест, все зелёные, `Total time` 20.6 с, параллельно (`fixed.parallelism=5`), бэкенд `nbank:self-transfer-fix`.
Внутри потока строки идут в порядке завершения тестов. `[n]` — номер набора параметров в параметризованном тесте.

| Поток | Тип | Класс | Тест | мс |
|---|---|---|---|---|
| worker-1 | UI | UserNameUITest | shouldBeNegativeChangeNameTest | 7 205 |
| worker-2 | API | DepositTest | shouldNotAllowDeposit[1] | 2 496 |
| worker-2 | API | DepositTest | shouldNotAllowDeposit[2] | 555 |
| worker-2 | API | DepositTest | shouldNotAllowDeposit[3] | 853 |
| worker-2 | UI | TransferUITest | shouldBeNegativeWithInvalidAccountReceiver | 7 660 |
| worker-3 | API | TransferTest | shouldNotAllowTransferIfSenderAndReceiverAccountSame | 2 566 |
| worker-3 | API | TransferTest | shouldNotAllowTransferToNotExistAccount | 764 |
| worker-3 | API | TransferTest | shouldNotAllowTransferTest[1] | 1 431 |
| worker-3 | API | TransferTest | shouldNotAllowTransferTest[2] | 1 454 |
| worker-4 | API | UserNameTest | successChangeNameTest | 2 424 |
| worker-4 | API | UserNameTest | negativeChangeNameTest | 548 |
| worker-4 | UI | UserNameUITest | shouldBeSuccessChangeNameTest | 5 918 |
| worker-5 | UI | DepositUITest | shouldNotAllowDeposit[2] | 7 339 |
| worker-6 | UI | TransferUITest | shouldBeNegativeTransfer[2] | 7 553 |
| worker-7 | API | DepositTest | verifyTopUpSuccess[3] | 924 |
| worker-7 | API | DepositTest | shouldNotAllowDepositAccountThatNotExist | 919 |
| worker-7 | API | TransferTest | shouldNotAllowTransferTest[3] | 1 399 |
| worker-7 | UI | TransferUITest | shouldBeNegativeTransfer[1] | 9 564 |
| worker-8 | UI | DepositUITest | depositShouldBeSuccessTest | 6 492 |
| worker-8 | UI | TransferUITest | shouldBeNegativeTransfer[3] | 7 767 |
| worker-9 | API | DepositTest | verifyTopUpSuccess[1] | 750 |
| worker-9 | API | DepositTest | verifyTopUpSuccess[2] | 693 |
| worker-9 | UI | TransferUITest | shouldBeNegativeWithAccSenderEqualsAccReceiver | 8 605 |
| worker-10 | API | TransferTest | checkTransferToAlienAccount | 1 675 |
| worker-10 | API | TransferTest | checkMeToMeSuccessTransfer[3] | 1 486 |
| worker-10 | API | TransferTest | checkMeToMeSuccessTransfer[2] | 1 215 |
| worker-11 | API | TransferTest | shouldNotAllowTransferWhenBalanceHasNotEnoughMoney | 1 510 |
| worker-11 | UI | DepositUITest | shouldNotAllowDeposit[1] | 6 931 |
| worker-12 | API | TransferTest | checkMeToMeSuccessTransfer[1] | 1 400 |
| worker-12 | UI | TransferUITest | shouldBeSuccessTransferToAnotherUserAccountWithMinParameters | 8 444 |
| worker-13 | UI | TransferUITest | shouldBeSuccessTransferMeToMeTestWithMaxParameters | 7 345 |
