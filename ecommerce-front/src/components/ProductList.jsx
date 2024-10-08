import React, { useEffect, useState } from 'react';
import { getProducts } from '../services/api';

const ProductList = () => {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    const fetchProducts = async () => {
      const productsData = await getProducts();
      setProducts(productsData);
    };
    fetchProducts();
  }, []);

  return (
    <div className="container mx-auto p-8">
      <h1 className="text-3xl font-bold mb-8">Nos Produits</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        {products.map((product) => (
          <div key={product.id} className="bg-white p-4 rounded-lg shadow-md">
            <h2 className="text-xl font-bold">{product.name}</h2>
            <p className="text-gray-700">{product.description}</p>
            <p className="text-gray-900 font-bold">{product.price} €</p>
            <button className="mt-4 bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600">
              Ajouter au panier
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ProductList;
