## 1. 필요한 모듈
1. mysql2
2. express
3. express-session
4. session-file-store
5. body-parser

## 2. db 설정
```
drop table if exists users;

CREATE TABLE users (
  id int NOT NULL AUTO_INCREMENT,
  username varchar(50) NOT NULL,
  password varchar(255) NOT NULL,
  PRIMARY KEY(id)
) ;
```
