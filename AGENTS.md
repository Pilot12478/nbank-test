# AGENTS.md

Руководство для контрибьютеров и AI-агентов по репозиторию `nbank-test` — проект API-автотестов
банковского приложения nbank (REST Assured + JUnit 5, сборка Maven, Java 25).

## Структура проекта

```
pom.xml                      Единственный Maven-модуль (org.example:nbank-test)
requests/*.http              Ручные HTTP-сценарии (IntelliJ HTTP Client), по итерациям
src/main/java/models/        DTO запросов/ответов, все наследуют BaseModel
src/main/java/requests/      Обёртки над HTTP-вызовами
  └ skelethon/               Ядро: Endpoint (enum), HttpRequest (базовый класс),
                             interfaces/CrudEnpointInterface,
                             requesters/CrudRequester, requesters/ValidatedCrudRequester
src/main/java/specs/         RequestSpecs (unAuth/adminAuth/userAuth), ResponseSpecs (ok/created/badRequest/…)
src/test/java/iteration2/    Тесты: BaseTest + DepositTest, TransferTest, UserNameTest
src/test/java/utils/         HelperForIteration2 (шаги), TestDataGenerator, AccountInfo
```

Базовый URI API захардкожен в `RequestSpecs.defaultReq()`: `http://localhost:4111/api/v1`.
Перед запуском тестов приложение nbank должно быть поднято локально на этом порту.

## Команды

| Команда | Что делает |
|---|---|
| `./mvnw clean test` | Полная сборка и прогон всех тестов |
| `./mvnw -Dtest=UserNameTest test` | Прогон одного тест-класса |
| `./mvnw -Dtest=UserNameTest#successChangeNameTest test` | Прогон одного теста |
| `./mvnw clean compile` | Только компиляция `src/main` |

В IDE предпочтительно запускать через run-конфигурации Maven/JUnit.

## Стиль кода и именование

- Отступ — 4 пробела, кодировка UTF-8, стандартные Java-конвенции именования.
- Модели: Lombok `@Data @AllArgsConstructor @NoArgsConstructor @Builder`, наследование от `BaseModel`.
  Имена: `<Действие><Сущность>Model{Request|Response}` (напр. `UpdateUserNameModelRequest`).
- Новый endpoint добавляется константой в `Endpoint` (url + request-модель + response-модель),
  отдельные классы-реквестеры писать не нужно.
- Запросы выполнять через `ValidatedCrudRequester<T>` (когда нужен типизированный ответ)
  или `CrudRequester` (когда проверяется сырой ответ/негативный сценарий).
- Статус-коды и авторизацию задавать только спеками из `ResponseSpecs` / `RequestSpecs`,
  не дублировать `expectStatusCode` в тестах.
- Тестовые данные генерировать через `TestDataGenerator`, хардкод логинов/паролей запрещён.

## Тесты

- JUnit 5 + AssertJ `SoftAssertions`: поле `softly` приходит из `BaseTest`, `assertAll()` вызывается в `@AfterEach`.
- Классы: `<Фича>Test`, методы: `successXxxTest` / `negativeXxxTest`, обязателен `@DisplayName` на русском.
- Подготовка данных — в `@BeforeEach` через `HelperForIteration2.createUserAndAccount()`,
  очистка — в `@AfterEach` через `deleteUser(accountInfo)`. Тесты должны быть независимыми.
- Переиспользуемые шаги выносить в `HelperForIteration2` парами `...Positive` / `...Negative`.

## Коммиты и пулл-реквесты

- История использует короткие сообщения в нижнем регистре на английском, часто с указанием этапа:
  `api test cases design`, `junior-auto-test-creation fix`, `auto test middle commit 3`.
  Придерживайтесь этого стиля: одна строка, повелительное/описательное краткое содержание.
- Работа ведётся в ветках вида `Auto-Test-Junior#1`, `Auto-Test-Middle#1`, `api-test-cases`
  с последующим PR в `main` (merge через GitHub).
- В PR указывайте: что покрыто тестами, ссылку на задачу/итерацию и результат прогона `mvn test`
  (лог или скриншот). Конфликты решайте явным коммитом с пояснением, какую версию оставили.

## Заметки для агентов

- Не коммитьте `target/` и содержимое `.idea/` (см. `.gitignore`).
- Тесты сетевые: без запущенного nbank на `localhost:4111` они падают — это не дефект кода.
- Опечатка `skelethon` в имени пакета намеренно закреплена в коде; не переименовывайте её точечно.
