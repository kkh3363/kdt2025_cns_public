const express = require("express");
const ejs = require('ejs');

const app = express();
const port = 3000;

app.set('view engine','ejs');
app.set('views', './views');
app.use(express.static("./assets"))

app.get("/", (req, res) => {
   const contacts = [
    { name: "John", email: "john@aaa.bbb", phone: "123456789" },
    { name: "Jane", email: "jane@aaa.bbb", phone: "67891234" },
  ];
  res.render('index',{ heading: "User List", contacts });
});


app.listen(port, () => {
  console.log(`${port}번 포트에서 서버 실행 중`);
});