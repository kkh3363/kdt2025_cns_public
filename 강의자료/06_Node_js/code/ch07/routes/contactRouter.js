const express = require('express');
const router = express.Router();

//module.exports = router
router 
    .get('/contacts',(req,res) => {
        res.status(200).send('contacts view');
    })
    .post('/contacts',(req,res) => {
        res.status(200).send('contacts create...');
    })
    .get('/contacts/:id',(req,res) => {
        res.status(200).send(`view contact for ID: ${req.params.id}`);
    })
    .delete("/contacts/:id", (req, res) => {
    res.status(200).send(`Delete Contact for ID: ${req.params.id}`);
    });

module.exports = router
