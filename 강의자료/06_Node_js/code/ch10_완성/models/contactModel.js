const db = require('../config/db')

const Contact = {
    getAll: async()=>{
        const [rows] = await db.query('select * from tbl_contact');
        return rows;
    },
    create: async(userData)=>{
        const [result] = await db.query('insert into tbl_contact set ?', userData);
        return result.insertId;
    },
findById: async (id) => {
    const [rows] = await db.query('SELECT * FROM tbl_contact WHERE id = ?', [id]);
    return rows[0]; // user 또는 undefined
  },

  update: async (id, userData) => {
    const [result] = await db.query('UPDATE tbl_contact SET ? WHERE id = ?', [userData, id]);
    return result.affectedRows;
  },

  delete: async (id) => {
    const [result] = await db.query('DELETE FROM tbl_contact WHERE id = ?', [id]);
    return result.affectedRows;
  }
}

module.exports = Contact;