import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { createIngredient, updateIngredient } from '../services/IngredientService';
import { updateInventory } from '../services/InventoryService';

const IngredientComponent = () => {
	const [name, setName] = useState('');
	const [amount, setAmount] = useState('');
	const [errors, setErrors] = useState({});
	const { id } = useParams();
	const navigate = useNavigate();

	useEffect(() => {
		if (id) {
			updateIngredient(id)
				.then(res => {
					setName(res.data.name);
					setAmount(res.data.amount);
				})
				.catch(err => console.error(err));
		}
	}, [id]);

	const saveOrUpdateIngredient = (e) => {
		e.preventDefault();

		// Validation
		const newErrors = {};
		if (!name.trim()) newErrors.name = "Name is required.";
		if (!amount || isNaN(amount) || Number(amount) < 0)
			newErrors.amount = "Enter a valid non-negative number for amount.";

		setErrors(newErrors);
		if (Object.keys(newErrors).length > 0) return;

		const payload = {
			id: 1, // singleton inventory ID
			ingredients: {
				[name]: Number(amount)
			}
		};

		// Call backend to add ingredient
		updateInventory(payload)
			.then(() => navigate('/ingredients'))
			.catch(err => {
				console.error(err);
				setErrors({ general: "Something went wrong adding the ingredient." });
			});
	};

	const DeleteIngredient = () => {
		if (!id) return;
		deleteIngredient(id)
			.then(() => navigate('/ingredients'))
			.catch(err => console.error(err));
	};

	const getGeneralErrors = () =>
		errors.general && (
			<div className="p-3 mb-2 bg-danger text-white">
				{errors.general}
			</div>
		);

	const pageTitle = () => (
		<div style={{ position: 'relative' }}>
			<button
				type="button"
				className="btn btn-dark"
				onClick={() => navigate('/ingredients')}
				style={{ position: 'absolute', top: '10px', right: '10px', fontSize: '18px', color: '#FFFFFF' }}
			>
				← Back
			</button>
			<h2 className="text-center mt-4">{id ? 'Update Ingredient' : 'Add Ingredient'}</h2>
		</div>
	);

	return (
		<div className='container'>
			<br /><br />
			<div className='row'>
				<div className='card col-md-6 offset-md-3'>
					{pageTitle()}
					{getGeneralErrors()}
					<div className='card-body'>
						<form>
							<div className='form-group mb-2'>
								<label className='form-label'>Ingredient Name:</label>
								<input
									type='text'
									className='form-control'
									placeholder='Enter Ingredient Name'
									value={name}
									onChange={e => setName(e.target.value)}
								/>
								{errors.name && <div className="text-danger">{errors.name}</div>}
							</div>

							<div className='form-group mb-2'>
								<label className='form-label'>Amount:</label>
								<input
									type='number'
									className='form-control'
									placeholder='Enter Amount'
									value={amount}
									onChange={e => setAmount(e.target.value)}
								/>
								{errors.amount && <div className="text-danger">{errors.amount}</div>}
							</div>

							<button
								type='submit'
								className='btn btn-success me-2'
								onClick={saveOrUpdateIngredient}
							>
								Submit
							</button>

							<button
								type='button'
								className='btn btn-danger'
								onClick={DeleteIngredient}
							>
								Delete
							</button>

						</form>
					</div>
				</div>
			</div>
		</div>
	);
};

export default IngredientComponent;
