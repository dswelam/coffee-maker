import React, { useEffect, useState } from 'react';
import { getTax, editTax } from '../services/AuthService';
import { useNavigate } from 'react-router-dom';

const TaxRateComponent = () => {
  const [taxRate, setTaxRate] = useState('');       // input value (starts blank)
  const [currentTax, setCurrentTax] = useState(null); // store current tax
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const navigate = useNavigate();

  useEffect(() => {
    getTax()
      .then(res => {
        setCurrentTax(res.data);  // set the current tax
      })
      .catch(err => {
        console.error(err);
        setError("Failed to load tax rate.");
      });
  }, []);
  
  useEffect(() => {
  	// prevent scrolling
  	document.body.style.overflow = 'hidden'

  	// cleanup when component unmounts
  	return () => {
  		document.body.style.overflow = 'auto'
  	}
  }, [])
  
  useEffect(() => {
    if (message) {
      const timer = setTimeout(() => setMessage(''), 3000); // disappears after 3s
      return () => clearTimeout(timer); // cleanup
    }
  }, [message]);

  useEffect(() => {
    if (error) {
      const timer = setTimeout(() => setError(''), 3000); // disappears after 3s
      return () => clearTimeout(timer); // cleanup
    }
  }, [error]);


  const handleSubmit = (e) => {
    e.preventDefault();

    if (taxRate === '' || isNaN(taxRate)) {
      setError("Enter a valid number.");
      return;
    }
	if (Number(taxRate) <= 0) {
	  setError("Tax rate cannot be negative.");
	  return;
	}

    editTax(Number(taxRate))
      .then(() => {
        setMessage("Tax rate updated!");
        setError("");
        setCurrentTax(Number(taxRate)); // update displayed current tax
        setTaxRate('');                // clear input box
      })
      .catch(err => {
        console.error(err);
        setError("Failed to update tax rate.");
      });
  };

  return (
	<div className="page-container d-flex justify-content-center align-items-start">
      <div className="card shadow-lg p-5" style={{ width: "30rem", backgroundColor: "#fff", borderRadius: "1rem", transform: 'scale(1.1)' }}>

        <h3 className="text-center mb-2">Update Tax Rate</h3>
        {currentTax !== null && (
          <p className="text-center text-secondary mb-4">
            Current Tax Rate: {currentTax}%
          </p>
        )}

        {error && <div className="alert alert-danger text-center">{error}</div>}
        {message && <div className="alert alert-success text-center">{message}</div>}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label fw-semibold">Tax Rate (%)</label>
            <input
              type="number"
			  step="any"          // allows decimals 
              className="form-control form-control-lg"
              placeholder="Enter new tax rate"  // show placeholder
              value={taxRate}
              onChange={e => setTaxRate(e.target.value)}
            />
          </div>

          <button
            type="submit"
            className="btn btn-danger btn-lg w-100 fw-bold"
            style={{ backgroundColor: "#CC0000" }}
          >
            Save
          </button>
        </form>

      </div>
    </div>
  );
};

export default TaxRateComponent;
