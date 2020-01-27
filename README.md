Финальный проект по курсу "ООП"

Разработка чата с графическим интерфейсом.
 
Серверный компонент обрабатывает все входящие команды. 
Он содержит следующее: 
1. Необходимую функциональность для работы с сетью. 
2. Диспетчер потоков для отображения всех клиентов в сети. 
3. Десериализацию и обработку пакетов, приходящих от клиентов. 

Клиентский компонент представляет собой графический интерфейс для управления чатом. 
Содержит следующее: 
1. Необходимую функциональность для работы с сетью. 
2. Поддержку кнопок для команд клиента. 
3. Отображение результатов выполнения команды. 

Таким образом, картина следующая: 
1. Клиент подключается к серверу, открывается окно. 
Сервер даёт клиенту имя по умолчанию. 

2. В данном окне отображаются все пользователи, находящиеся в сети. 

3. При нажатии на конкретного пользователя, открывается чат с конкретным пользователем. 
4. Появляется поле для ввода сообщения и кнопка send. 
5. При нажатии на кнопку send, формируется пакет SendPacket, в котором содержится поле user, 
в котором указан получатель, пакет отправляется на сервер, сервер десериализует данные, получает имя получателя и сообщение отображается в гуи получателя. 

Кнопки (команды): 
1. Кнопка send. Отправляет письмо конкретному получателю. 
2. Кнопка incoming. Показ входящий сообщений. 
3. Кнопка delete. Удаление переписки, помещение её в корзину. 
4. Кнопка cancel. Возвращение сообщений из корзины в папку входящие. 
5. Кнопка friends. Показ всех пользователей в сети. 
6. Кнопка exit. Для выхода из сети. 
7. Кнопка rename. Для смены имени пользователя. 
8. Кнопка group. Для создания группового чата. 
9. Кнопка attach. Для прикрепления и отправления файла.
