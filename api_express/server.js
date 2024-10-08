const express = require('express');
const bodyParser = require('body-parser');

// Importer les routes
const authRoutes = require('./routes/authRoutes');
 
// Ajouter d'autres routes ici
const app = express();
const port = 3000;

// Middleware pour parser les requêtes JSON
app.use(bodyParser.json());

// Routes
app.use('/api/auth', authRoutes);
 
 

// Démarrer le serveur
app.listen(port, () => {
  console.log(`API Node.js en écoute sur le port ${port}`);
});
