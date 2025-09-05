const mysql = require('mysql2/promise');

const dbPool = mysql.createPool({
  host: 'localhost',
  user: 'nodeuser',
  password: '1234',
  database: 'my_db'
});

module.exports = dbPool;