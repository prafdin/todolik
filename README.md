# TODOlik 

TODOlik (тудулик) - cli программа для работы с TODO заметками - их создание, удаление, обновление и просмотр.
Программа написана в учебных целях и не претендует на удобность и переиспользуемость.

## Основные возможности 

### Просмотр созданных заметок 
```bash
todolik list
```

### Создание новой заметки
```bash
todolik create "Купить продукты в магазине 11.02.2025" 
```

### Удаление заметки
```bash
todolik delete 1
```

### Обновление заметки
```bash
todolik update 1
```

## Установка 

### Ubuntu
Для сборки и установки todolik в систему необходимо иметь установленный docker 19.03 и выше, а также установленный BuildKit.  

Следующая команда запросит sudo пароль и установит todolik в систему:
```shell
make install
```

Следующая команда соберет проект и поместит результат в директорию `out/target`
```shell
make build
```

### Другие
Будет дополнено позже.
