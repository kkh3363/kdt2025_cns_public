
const express = require('express');
const cookieParser = require("cookie-parser");
const checkLogin = require("../middlewares/checkLogin");

const router = express.Router();
const {
    getAllContacts,
    addContactForm,
    createContact,
    deleteContact,
    updateContact,
    getContact
} = require('../controllers/contactColtroller');

router.use(cookieParser());

router
    .route('/')
    //.get(getAllContacts);
    .get(checkLogin, getAllContacts);

router
    .route('/add')
//    .get(addContactForm)
//    .post(createContact);
    .get(checkLogin, addContactForm)
    .post(checkLogin, createContact);

router
    .route('/:id')
//  .get(getContact)
//  .delete(deleteContact)
//  .put(updateContact);
    .get(checkLogin, getContact)
    .put(checkLogin, updateContact)
    .delete(checkLogin, deleteContact);

module.exports=router;