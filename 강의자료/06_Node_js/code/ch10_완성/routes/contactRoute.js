
const express = require('express');
const router = express.Router();
const {
    getAllContacts,
    addContactForm,
    createContact,
    deleteContact,
    updateContact,
    getContact
} = require('../controllers/contactColtroller');

router
    .route('/')
    .get(getAllContacts);

router
    .route('/add')
    .get(addContactForm)
    .post(createContact);
router
    .route('/:id')
    .get(getContact)
    .delete(deleteContact)
    .put(updateContact);

module.exports=router;