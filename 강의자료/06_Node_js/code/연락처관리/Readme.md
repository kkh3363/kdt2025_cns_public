

```
"dependencies": {
    "bcrypt": "^6.0.0",
    "cookie-parser": "^1.4.7",
    "dotenv": "^17.2.2",
    "ejs": "^3.1.10",
    "express": "^5.1.0",
    "express-async-handler": "^1.2.0",
    "express-session": "^1.18.2",
    "jsonwebtoken": "^9.0.2",
    "method-override": "^3.0.0",
    "mysql2": "^3.14.4"
  }
```


```
create user nodeuser@localhost identified by '1234';
create database my_db;
grant all privileges on my_db.* to nodeuser@localhost with grant option;
commit;

use my_db;

drop table if exists tbl_contact ;
CREATE TABLE  tbl_contact (
  id int not null auto_increment,
  name VARCHAR(20) NOT NULL,
  email VARCHAR(20) NOT NULL,
  phone  VARCHAR(20) NOT NULL,
  PRIMARY KEY (id)
);

select * from tbl_contact;
delete from tbl_contact;
insert into tbl_contact (name, email, phone) values 
	('홍길동','hong@gmail.com', '010-1234-5678')
    , ('김청양','kim@gmail.com', '010-1234-9999')
    ,('이부여','lee@gmail.com', '010-1234-6688');
drop table if exists tbl_user;

create table tbl_user (
	id int auto_increment not null ,
    username varchar(20) not null unique,
    password varchar(250) not null,
    primary key(id));
    
    select * from tbl_user;
```
