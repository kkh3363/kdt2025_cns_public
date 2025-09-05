const express = require("express");
const app = express();
const path=require('path');

const port = 3000;

app.get("/", (req, res) => {
  const filePath = path.join (__dirname , '/assets', "getAll.html");
  res.sendFile(filePath);
});


app.listen(port, () => {
  console.log(`${port}번 포트에서 서버 실행 중`);
});