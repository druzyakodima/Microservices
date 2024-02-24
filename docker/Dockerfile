#Это для того чтобы docker-compose собирал таблицы
FROM postgres:15
ADD ./init.sql /docker-entrypoint-initdb.d/1-init.sql
ADD ./data.sql /docker-entrypoint-initdb.d/2-data.sql