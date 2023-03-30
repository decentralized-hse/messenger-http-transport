# Транспорт поверх HTTP [SendBin protocol]

## Постановка задачи:
Данный проект реализует транспорт сообщений для мессенджера (клиент-клиент) описанный в [ideas.md#C](https://github.com/decentralized-hse/Cirriculum/blob/main/ideas.md#c-%D0%BA%D0%BB%D0%B0%D1%81%D1%82%D0%B5%D1%80-%D0%BC%D0%B5%D1%81%D1%81%D0%B5%D0%BD%D0%B4%D0%B6%D0%B5%D1%80%D1%8B-%D0%B8-%D1%87%D0%B0%D1%82%D1%8B).

## Реализация

Транспорт поверх HTTP будет реализован путем публикации паст в ленту заданного пользователя (для каждого пользователя – своя лента) на [pastebin.com](http://pastebin.com) с помощью их API. 

Для отправки сообщения используется функция  [sendMessages](https://github.com/decentralized-hse/messenger-http-transport/blob/54b7828fdf569773248335d224fa70cd335a95dc/lib/src/main/kotlin/com/github/decentralized_hse/messenger_http_transport/sendbin/Sendbin.kt#L43), для принятия сообщений используется функция [listen](https://github.com/decentralized-hse/messenger-http-transport/blob/54b7828fdf569773248335d224fa70cd335a95dc/lib/src/main/kotlin/com/github/decentralized_hse/messenger_http_transport/sendbin/Sendbin.kt#L61).

## Как запустить?
Проект может использоваться вызовом отдельных функций, указанных выше. 

В случае демонстрации работоспособности без связки с мессенджером запустить проект можно с помощью UI среды разработки или командной строки.

**Отправка сообщений:**

```
/opt/intellij-idea-community/jbr/bin/java-javaagent:/opt/intellij-idea-community/lib/idea_rt.jar=36263:/opt/intellij-idea-community/bin -Dfile.encoding=UTF-8 -classpath /tmp/classpath1097058776.jar com.github.decentralized_hse.messenger_http_transport.sender.MainKt --dev-key <your_dev_key> --from <your_sender_name> --user-key <pastebin_user_key>
```

**Прием сообщений:**

```
/opt/intellij-idea-community/jbr/bin/java-javaagent:/opt/intellij-idea-community/lib/idea_rt.jar=36263:/opt/intellij-idea-community/bin -Dfile.encoding=UTF-8 -classpath /tmp/classpath1097058776.jar com.github.decentralized_hse.messenger_http_transport.listener.MainKt --dev-key <your_dev_key> --from <sender_name> --user-key <pastebin_user_key>
```

### Ключи
dev-key –  API ключ pastebin
user-key – генерируется пользователем с помощью pastebin при "регистрации", затем передается любым доступным путем другим пользователям, которые впоследствии вводят данный ключ как один из аргументов вызываемой функции.

## Демо
Для демонстрации работоспособности проекта мы записали видео с передачей сообщений -- [ссылка](https://youtu.be/_MHFiWHWUb0).
