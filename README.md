user

```
admin
```

password

```
jkhprtrnwxwtyftvxbjplbljcsjlycgayjekqpuhclykhgrntropngzwkwzr
```

Перед запуском нужно будет очистить старые волюмы, что бы не мешали устаревшие базы данных

```bash
docker volume rm $(docker volume ls --format "{{.ID}}")
```

Эта команда удалит все волюмы на Linux машине где была запущена

Для запуска нужно собрать проект из корня мавном, а затем сбилдить и поднять образы докер композом
```bash
mvn clean package -DskipTests
docker compose up -d
```
`-DskipTests` флаг, который игнорирует тесты, можно запустить с тестами, они должны пройти все

Вторая команда собирает и поднимает все образы

Для демонтсрации можно использовать Postman сделать там запросы в соответствии с входными моделями на контроллерах

Что бы получить токен авторизации нужно залогиниться под админом, он сразу с правами супервизора, что бы добавлять пользователей