сервис с авторизацией, пользователями, хранением данных в БД.
Реализована возможность перевода средств со счета на счет, возможность пополнения баланса, создание нового аккаунта.
Для того чтобы все заработало:
поднять базу данных из корневой директории командой 'psql -U postgres'
из корневой директории выполнить
./gradlew compileJava
./gradlew run
