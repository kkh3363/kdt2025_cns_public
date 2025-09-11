const db = require('../config/db');

const User = {
  getAll: async()=>{
        const [rows] = await db.query('select * from tbl_user');
        return rows;
    },
    create: async(userData)=>{
        const [result] = await db.query('insert into tbl_user set ?', userData);
        return result.insertId;
    },
    findByUserName: async (username) => {
      const [rows] = await db.query('SELECT * FROM tbl_user WHERE username = ?', [username]);
      
      return rows[0]; // user 또는 undefined
  },
};

module.exports = User;