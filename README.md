There are bugs - some fields content NaN.

Пример учебный. Заходим на сайт Tutu.ru и ищем авиабилеты на рейс Калуга - Прага (через браузер Chrome).

Когда писался этот тест, на сайте был баг - в таблице с ценами некоторые поля содержали "NaN" вместо "рейсов нет". 
Это видно в файле Screen_near_cities__.png. Сейчас баг исправлен.

Ведется видеозапись теста, а когда нужная таблица открыта, прокручиваем страницу, чтобы таблица была видна на экране,
и делаем скриншот. Цены в таблице проверяются, чтобы поля содержали цифры или "рейсов нет" и не содержали "NaN".
Результаты проверки записываются в текстовый файл.
