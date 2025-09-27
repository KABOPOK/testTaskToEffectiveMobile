# 🏦 Bank Card Management System

Система управления банковскими картами с REST API, реализованная на Spring Boot с использованием JWT аутентификации и ролевой авторизации.

## 🚀 Особенности

- **Безопасность**: JWT аутентификация, ролевая авторизация (ADMIN/USER)
- **Шифрование**: Номера карт и пароли зашифрованы в базе данных
- **Маскирование**: Пользователи видят только последние 4 цифры номера карты
- **API документация**: OpenAPI 3.0 спецификация
- **Контейнеризация**: Docker Compose для развертывания

## 📋 Функциональность

### 👤 Пользователь (USER)
- Просмотр своих карт (пагинация)
- Получение информации о конкретной карте
- Просмотр баланса карты
- Запрос на блокировку карты ("TO_BLOCK")
- Переводы между своими картами
- Поиск карт по номеру

### 👑 Администратор 
(ADMIN создаётся сразу login/password : admin/admin)
- Все возможности пользователя
- Создание новых карт
- Просмотр всех карт системы (в незашифрованном виде) (пагинация)
- Блокировка/разблокировка карт
- Удаление карт
- Управление пользователями(CRUD + блокировка)
- Просмотр карт со статусом ("TO_BLOCK") (пагинация)

## 🛠 Технический стек

- **Backend**: Java 17, Spring Boot 
- **Security**: Spring Security, JWT
- **Database**: PostgreSQL, Spring Data JPA
- **Migration**: Liquibase
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Containerization**: Docker, Docker Compose
- **Testing**: JUnit, Mockito
- **Build**: Maven

## 🚀 Запуск проекта

### Режим продакшн 
```bash
mvn clean package
docker compose up
```

### Режим локальной отладки
```bash
cd local
docker compose up
```

### Порты (Режим продакшн)
- **API**: `8086`
- **PostgreSQL**: `5411`
- **Swagger UI**: `http://localhost:8086/swagger-ui/index.html`
### Порты (Режим локальной отладки)
- **API**: `8084`
- **PostgreSQL**: `5422`
- **Swagger UI**: `http://localhost:8084/swagger-ui/index.html`

## 📚 API Endpoints

### 🔐 Аутентификация
```
POST /api/public/user/register    # Регистрация
POST /api/public/user/auth        # Аутентификация
```

### 💳 Пользовательские операции с картами
```
GET  /api/user/get_all_cards                    # Все карты пользователя
GET  /api/user/card/get_cards_by_number         # Поиск карт по номеру
GET  /api/user/card/{id}                        # Карта по ID
GET  /api/user/card/balance/{id}                # Баланс карты
PUT  /api/user/card/request_to_block/{id}       # Запрос блокировки
PUT  /api/user/card/transfer                    # Перевод между картами
```

### 🛡 Административные операции с картами
```
GET    /api/admin/card/get_all                  # Все карты системы
GET    /api/admin/card/get_to_block             # Карты к блокировке
POST   /api/admin/card/create                   # Создать карту
GET    /api/admin/card/get/{id}                 # Карта по ID
PUT    /api/admin/card/update/{id}              # Обновить карту
PUT    /api/admin/card/block/{id}               # Заблокировать карту
DELETE /api/admin/card/delete/{id}              # Удалить карту
```

### 👥 Управление пользователями (ADMIN)
```
GET    /api/admin/user/get_all_users            # Все пользователи
GET    /api/admin/user/get/{id}                 # Пользователь по ID
PUT    /api/admin/user/update/{id}              # Обновить пользователя
PUT    /api/admin/user/block/{id}               # Заблокировать пользователя
DELETE /api/admin/user/delete/{id}              # Удалить пользователя
```

## 🧪 Тестирование

```bash
# Запуск всех тестов
mvn test
```

## ⚠️ Статусы карт

- **ACTIVE**: Активная карта, доступны все операции
- **BLOCKED**: Заблокированная карта, операции недоступны
- **EXPIRED**: Истек срок действия
- **TO_BLOCK**: Пользователь запросил блокировку (ожидает действий админа)

## 🐛 Обработка ошибок

```json
{
  "errors": [
    {
      "title": "CARD_NOT_FOUND",
      "description": "Card with id 123e4567-e89b-12d3-a456-426614174000 not found"
    }
  ]
}
```