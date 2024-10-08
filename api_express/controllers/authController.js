const axios = require('axios');

exports.login = (req, res) => {
  axios.post('http://localhost:8080/auth/login', req.body)
    .then(response => res.json(response.data))
    .catch(error => res.status(500).json({ error: error.message }));
};

exports.register = (req, res) => {
  axios.post('http://localhost:8080/auth/register', req.body)
    .then(response => res.json(response.data))
    .catch(error => res.status(500).json({ error: error.message }));
};
