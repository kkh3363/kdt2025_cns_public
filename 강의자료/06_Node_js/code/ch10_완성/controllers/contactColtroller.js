const asyncHandler = require('express-async-handler');
const Contact = require('../models/contactModel');
// @desc Get all contacts
// @route GET /contacts
const getAllContacts = asyncHandler ( async (req, res) => {
    //res.status(200).send("Contacts Page");
    const contacts = await Contact.getAll();
    //res.json(contacts);
   
    res.render('index', { heading: 'Contact List', contacts} );
});

const addContactForm = asyncHandler ( async (req, res) => {
   res.render('add');
});

const createContact = asyncHandler ( async (req, res) => {
   const {name, email, phone} = req.body;
   if ( !name || !email || !phone){
    return res.status(400).send("필수값이 입력되지 않았습니다.");
   }
   const id= await Contact.create( req.body );
   res.redirect('/contacts');
});
// @desc Delete contact
// @route DELETE /contacts/:id
const deleteContact = asyncHandler ( async (req, res) => {
   //res.status(200).send(`Contact delete : ${req.params.id} `);
   const contactId = req.params.id;
   if ( !contactId )
    return reres.status(400).send('잘못된 ID 입니다.');

   await Contact.delete(contactId );
   res.redirect('/contacts');
});

// @desc Update contact
// @route PUT /contacts/:id
const updateContact = asyncHandler ( async (req, res) => {
    const contactID = req.params.id;
    const {name, email, phone} = req.body;
   if ( !name || !email || !phone){
    return res.status(400).send("필수값이 입력되지 않았습니다.");
   }
   const id= await Contact.update( contactID, req.body );
   res.redirect('/contacts');
});

// @desc get contact
// @route GET /contacts/:id
const getContact = asyncHandler ( async (req, res) => {
    const contact = await Contact.findById( req.params.id );
    console.log('-------' + contact);
    
    res.render('update', { contact: contact } );
    //res.status(200).send(`Contact delete : ${req.params.id} `);
});

module.exports={
    getAllContacts,
    addContactForm,
    createContact,
    deleteContact,
    updateContact,
    getContact
};