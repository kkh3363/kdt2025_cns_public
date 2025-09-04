const getAllContacts = asyncHandler ( async (req, res) => {
    //res.status(200).send("Contacts Page");
    //const filePath = path.join(__dirname, "../assets", "getAll.html");
    //res.sendFile(filePath);
    const contacts = [
    { name: "John", email: "john@aaa.bbb", phone: "123456789" },
    { name: "Jane", email: "jane@aaa.bbb", phone: "67891234" },
  ];
  res.render('index', { heading: 'User List', contacts });

});
