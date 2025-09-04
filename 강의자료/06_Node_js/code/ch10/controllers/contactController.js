const asyncHandler = require("express-async-handler");
// @desc Get all contacts
// @route GET /contacts
const getAllContacts = asyncHandler ( async (req, res) => {
    res.status(200).send("Contacts Page");
});

const createContact = asyncHandler(async (req, res) => {
  const { name, email, phone } = req.body;
  if (!name || !email || !phone) {
    return res.status(400).send("필수값이 입력되지 않았습니다.");
  }
  res.status(201).send("Create Contacts");
});
// @desc Get contact
// @route GET /contacts/:id
const getContact = asyncHandler(async (req, res) => {
  res.status(200).send(`View Contact for ID: ${req.params.id}`);
});

// @desc Update contact
// @route PUT /contacts/:id
const updateContact = asyncHandler(async (req, res) => {
  res.status(200).send(`Update Contact for ID: ${req.params.id}`);
});

// @desc Delete contact
// @route DELETE /contacts/:id
const deleteContact = asyncHandler(async (req, res) => {
  res.status(200).send(`Delete Contact for ID: ${req.params.id}`);
});


module.exports = {
  getAllContacts,
  createContact,
  getContact,
  updateContact,
  deleteContact,
};

