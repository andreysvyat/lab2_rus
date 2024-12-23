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