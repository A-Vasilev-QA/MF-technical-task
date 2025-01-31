# Техническое задание

1. Необходимо изучить API и описать все возможные тест кейсы (и/или чек листы) для проверки
2. Приоритизировать их
3. Автоматизировать наиболее приоритетные тест кейсы из первой части задания, выложить код автотестов и результаты первой части задания в github

## Тест-кейсы и чек-листы

### Priority 1
-   **Успешное создание пользователя с валидными данными.**
-   **Успешное получение списка пользователей.**
-   **Создание пользователя с уже существующим username.**
-   **Создание пользователя с уже существующим email.**

### Priority 2
-   **Создание пользователя с отсутствующим обязательным полем.**
-   **Создание пользователя с пустым значением обязательного поля.**
-   **Создание пользователя c некорректным форматом email.**
-   **Получение пользователя по /get?id** - не описано в спецификации, иначе приоритет был бы выше

### Priority 3
- **Получение пользователя по /get?username** - не описано в спецификации, иначе приоритет был бы выше
- **Получение пользователя по /get?email** - не описано в спецификации, иначе приоритет был бы выше
- **Создание пользователя c username очень большой длины (256 символов)**
- **Создание пользователя c password очень большой длины (256 символов)**

### Priority 4
- **Создание пользователя с неописанными параметрами в form-data**
- **Получение пользователя по /get?password**
- **Получение пользователя по /get?[параметр] - где параметр не существует**
- **Получение пользователя по /get?[параметр] - где параметр существует, но имеет невалидное значение**
- **Запрос на создание пользователя с некорректным content-type (Например, JSON)** - кстати, сработает

### Дополнительно
- Поскольку при запросе /get?[параметр=значение] формируется SQL запрос, нужно тестирование на возможность SQL injection

## Автоматизированные кейсы

#### Примечания
- Тесты выполняются в несколько потоков
- Тесты писались полностью независимыми
- Предполагалось, что мы не сможем создать тестовые данные иначе, чем в самом тесте

### 1. Успешное создание пользователя с валидными данными
**Preconditions:**
- API доступен

**Step 1**
- Отправить POST запрос с валидными username, email и password в form-param

**Expected result 1**
- Вернётся ответ с кодом 200, success:true и message: "User Successfully created"
- В ответ будет содержаться id и данные пользователя, а также не пустые поля "created_at" и "updated_at"

**Step 2**
- Отправить GET запрос с id пользователя ("/get?id=")
- Сопоставить полученные в ответе данные с данными отправленными в POST запросе

**Expected result 2**
- Вернётся ответ с кодом 200, данные ответа совпадут с данными из POST запроса/ответа на него

> При выполнении теста находится опечатка: "User **Successully** created"

### 2. Успешное получение списка пользователей.
**Preconditions:**
- API доступен

**Step 1**
- 5 раз отправить POST запрос с валидными username, email и password в form-param

**Expected result 1**
- 5 раз вернётся ответ с кодом 200

**Step 2**
- Отправить GET запрос без параметров

**Expected result 2**
- Вернётся ответ с кодом 200 и массивом пользователей
- Пользователей в ответе будет минимум 5
- Все 5 созданных пользователей будут в списке

### 3. Создание пользователя с уже существующим username.
**Preconditions:**
- API доступен

**Step 1**
- Отправить POST запрос с валидными username, email и password в form-param

**Expected result 1**
- Вернётся ответ с кодом 200

**Step 2**
- Отправить POST запрос с валидными email и password и username из первого запроса в form-param

**Expected result 2**
- Вернётся ответ с кодом 400
- Ответ будет содержать success: false и "message":"This username is taken. Try another."

### 4. Создание пользователя с уже существующим email.
**Preconditions:**
- API доступен

**Step 1**
- Отправить POST запрос с валидными username, email и password в form-param

**Expected result 1**
- Вернётся ответ с кодом 200

**Step 2**
- Отправить POST запрос с валидными username и password и email из первого запроса в form-param

**Expected result 2**
- Вернётся ответ с кодом 400
- Ответ будет содержать success: false и "message": "Email already exists"

### 5. Создание пользователя c некорректным форматом email.
> Не приоритетно, но хотелось включить падающий кейс

**Preconditions:**
- API доступен

**Step 1**
- Отправить POST запрос с валидными username и password и email неправильного формата (без "@" и домена) в form-param

**Expected result 1**
- Вернётся ответ с кодом 400
- Ответ будет содержать success: false и сообщение об ошибке


## Структура проекта
~~~
MF-technical-taks/
│
├── src/
│   ├── main/             # Основной код проекта (пустой в данном случае)
│   ├── test/
│       └── java/qa/avasilev/
│           ├── helpers/
│           │   └── CustomAllureListener.java
│           ├── models/  # DTO и модели ответа
│           │   ├── CreateUserNegativeResponseModel.java
│           │   ├── CreateUserPositiveResponseModel.java
│           │   ├── CreateUserRequestModel.java
│           │   └── GetUserResponseModel.java
│           ├── specs/   # Настройка спецификаций RestAssured
│           │   └── UserApiSpec.java
│           └── tests/   # Тестовые классы
│               ├── TestBase.java
│               ├── TestData.java
│               └── UserApiTests.java
│       └── resources/
│           ├── tpl/     # Шаблоны запросов и ответов
│           │   ├── request.ftl
│           │   └── response.ftl
│           └── junit-platform.properties # Настройки параллельного запуска
├── .gitignore            # Список файлов и папок для игнорирования в Git
└──  build.gradle         # Основной файл Gradle
~~~

## Используемые технологии

-   **Java**: Основной язык для написания тестов.
-   **JUnit 5**: Фреймворк для модульного тестирования.
-   **RestAssured**: Библиотека для тестирования REST API.
-   **Allure**: Инструмент для создания красивых отчетов о тестировании.
-   **Faker**: Генерация тестовых данных.
-   **AssertJ**: Расширенная библиотека для ассертов.

## Инструкции по запуску

### Шаги для запуска тестов

-   Убедитесь, что у вас установлена Java
-   Убедитесь, что у вас установлен Gradle
-   Склонируйте репозиторий:
~~~
	git clone <repository-url>
	cd MF-technical-taks
~~~
-   Выполните команду для запуска тестов:
~~~
	gradle clean test
~~~

### Генерация отчетов Allure

- После выполнения тестов выполните команду для подготовки отчетов:
~~~
    gradle allureReport
~~~
- Для просмотра отчета запустите:
~~~
    gradle allureServe
~~~
Отчет откроется в Вашем браузере.
