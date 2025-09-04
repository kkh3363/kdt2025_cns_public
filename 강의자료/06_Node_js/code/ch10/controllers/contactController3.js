const asyncHandler = require("express-async-handler");
//const path = require("path");

// @desc Get all contacts
// @route GET /contacts
const getAllContacts = asyncHandler ( async (req, res) => {
    //res.status(200).send("Contacts Page");
    //const filePath = path.join(__dirname, "../assets", "getAll.html");
    //res.sendFile(filePath);
    const contacts = [
    { _id:1, name: "John", email: "john@aaa.bbb", phone: "123456789" },
    { _id:2, name: "Jane", email: "jane@aaa.bbb", phone: "67891234" },
  ];
  res.render('index', { heading: 'User List', contacts });

});

const createContact = asyncHandler(async (req, res) => {
  const { name, email, phone } = req.body;
  if (!name || !email || !phone) {
    return res.status(400).send("필수값이 입력되지 않았습니다.");
  }
  //res.status(201).send("Create Contacts");
   res.redirect("/contacts");
});
// @desc Get contact
// @route GET /contacts/:id
const getContact = asyncHandler(async (req, res) => {
  //res.status(200).send(`View Contact for ID: ${req.params.id}`);
  const contact = { _id:1, name: "John", email: "john@aaa.bbb", phone: "123456789" };
  res.render("update", { contact: contact });
});

// @desc Update contact
// @route PUT /contacts/:id
const updateContact = asyncHandler(async (req, res) => {
  //res.status(200).send(`Update Contact for ID: ${req.params.id}`);
  
});

// @desc Delete contact
// @route DELETE /contacts/:id
const deleteContact = asyncHandler(async (req, res) => {
  res.status(200).send(`Delete Contact for ID: ${req.params.id}`);
});

// @desc View add contact form
// @route GET /contacts/add
const addContactForm = asyncHandler((req, res) => {
  console.log('------------');
  
  res.render("add");
});


module.exports = {
  getAllContacts,
  createContact,
  getContact,
  updateContact,
  deleteContact,
  addContactForm
};

