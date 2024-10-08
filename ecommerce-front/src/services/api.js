import axios from 'axios';

const API_URL = 'http://localhost:8080/api';  // URL de l'API backend

// Authentification
// export const login = async (username, password) => {
//   const response = await axios.post("http://localhost:8080/api/authentification/login", { username, password },
//     {
//       headers: {
//         'Content-Type': 'application/json'
//       }
//     });
//   localStorage.setItem('token', response.data.token);  // Stocker le token JWT
//   return response.data.token;
// };
 

// Authentification avec Fetch
export const login = async (username, password) => {
  try {
    let headers = new Headers();

    headers.append('Content-Type', 'application/json');
    headers.append('Accept', 'application/json');

    const response = await fetch(`${API_URL}/authentification/login`, {
    
      method: 'POST',
      headers: headers,
      body: JSON.stringify({ username, password }),  // Convertir les données en JSON
    });

    if (!response.ok) {
      throw new Error(`Erreur HTTP ! Statut: ${response.status}`);
    }

    const data = await response.json();  // Obtenir la réponse JSON
    localStorage.setItem('token', data.token);  // Stocker le token JWT
    return data.token;
  } catch (error) {
    console.error('Erreur lors de la connexion', error);
    throw error;
  }
};


export const signup = async (username, email, password) => {
  const response = await axios.post(`${API_URL}/auth/signup`, { username, email, password });
  return response.data;
};

// Produits
export const getProducts = async () => {
  const token = localStorage.getItem('token');
  const response = await axios.get(`${API_URL}/products`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return response.data;  // Retourner les produits
};

export const getProductDetails = async (productId) => {
  const token = localStorage.getItem('token');
  const response = await axios.get(`${API_URL}/products/${productId}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return response.data;  // Retourner les détails du produit
};

// Panier
export const addToCart = async (productId, quantity) => {
  const token = localStorage.getItem('token');
  const response = await axios.post(`${API_URL}/cart/add`, { productId, quantity }, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return response.data;  // Ajouter au panier
};
