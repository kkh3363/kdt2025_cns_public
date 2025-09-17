const express = require('express');
const app = express();
const path = require('path');

const MongoClient = require('mongodb').MongoClient;

let db;
const db_uri = 'mongodb://localhost:27017';  // compass로 접속할 때 쓰이는 uri와 같다.
MongoClient.connect(db_uri, (error, client) => {
    if (error) {
        return console.log(error);
    } else {
        db = client.db('mysql');  // DB_NAME에 연결할 DB 이름을 넣는다.
        app.listen(8080, () => {

            // DB 연동 테스트
            db.collection('test').find().toArray().then((result)=>{
                console.log(result);
            })
            // 'test'라는 collection에서 find()를 통해 모든 데이터를 가져오는 코드

            console.log('server on');
        })
    }
})

app.use( '/', express.static( path.join(__dirname, 'app/dist') ));
app.get('/', (req, res)=>{
    res.sendFile(path.join(__dirname, 'app/dist/index.html'));
})