import React from 'react';
import { Link } from 'react-router-dom';

const Navbar = () => {
  return (
    <nav className="bg-blue-600 p-4">
      <div className="container mx-auto flex justify-between items-center">
        <h1 className="text-white text-2xl font-bold">
          <Link to="/">E-Commerce</Link>
        </h1>
        <div className="flex space-x-4">
          <Link to="/products" className="text-white hover:text-gray-200">Produits</Link>
          <Link to="/cart" className="text-white hover:text-gray-200">Panier</Link>
          <Link to="/login" className="text-white hover:text-gray-200">Connexion</Link>
          <Link to="/signup" className="text-white hover:text-gray-200">Inscription</Link>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
