const getAllContacts = asyncHandler(async (req, res) => {

  const users = [
    { name: "John", email: "john@aaa.bbb", phone: "123456789" },
    { name: "Jane", email: "jane@aaa.bbb", phone: "67891234" },
  ];
  res.render(“getAll”, { heading: “User List” , users});
  //res.render(“index”, { heading: “User List” , users});
});
