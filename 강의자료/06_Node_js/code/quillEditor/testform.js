const express = require('express');
const multer  = require('multer');
const path = require('path');
const bodyParser = require('body-parser');
const editController = require('./controllers/editController');

const form_data = multer();


const app = express();
const port = 3000;

app.set("view engine", "ejs");
app.set("views", "./views");

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));
//app.use(form_data.array());



app.get('/edit', (req, res) => {
    res.render('edit');
});
app.post('/edit', editController.createEdit);

app.listen(port, () => {
  console.log(`서버가 http://localhost:${port} 에서 실행 중입니다.`);
});