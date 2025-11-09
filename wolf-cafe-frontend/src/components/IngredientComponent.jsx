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
	
	useEffect(() => {
		// prevent scrolling
		document.body.style.overflow = 'hidden'

		// cleanup when component unmounts
		return () => {
			document.body.style.overflow = 'auto'
		}
	}, [])

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
			<div
				className='d-flex justify-content-center align-items-center vh-100'
				style={{ paddingTop: '5px' }}
			>
				<div
					className='card shadow-lg p-5'
					style={{
						width: '45rem',
						transform: 'scale(1.05)',
						backgroundColor: '#fff',
						borderRadius: '1rem'
					}}
				>
					<div className='card-header text-center border-0 mb-4 bg-white'>
						<h2 className='fw-bold mb-0 text-dark'>
							{id ? 'Update Ingredient' : 'Add Ingredient'}
						</h2>
						<p className='text-secondary mt-2'>
							Fill in the details below to {id ? 'update' : 'add'} your ingredient
						</p>
					</div>

					<div className='card-body'>
						<form>
							<div className='mb-4'>
								<label className='form-label fs-5 fw-semibold text-dark'>
									Ingredient Name
								</label>
								<input
									type='text'
									className='form-control form-control-lg'
									placeholder='Enter ingredient name'
									value={name}
									onChange={e => setName(e.target.value)}
								/>
								{errors.name && (
									<div className='text-danger mt-2 fs-6'>{errors.name}</div>
								)}
							</div>

							<div className='mb-4'>
								<label className='form-label fs-5 fw-semibold text-dark'>
									Amount
								</label>
								<input
									type='number'
									className='form-control form-control-lg'
									placeholder='Enter amount'
									value={amount}
									onChange={e => setAmount(e.target.value)}
								/>
								{errors.amount && (
									<div className='text-danger mt-2 fs-6'>{errors.amount}</div>
								)}
							</div>

							<div className='d-flex gap-3'>
								<button
									type='submit'
									className='btn btn-success btn-lg w-100 fw-bold'
									onClick={saveOrUpdateIngredient}
								>
									Submit
								</button>
								{id && (
									<button
										type='button'
										className='btn btn-danger btn-lg w-100 fw-bold'
										onClick={DeleteIngredient}
									>
										Delete
									</button>
								)}
							</div>

							{errors.general && (
								<div
									className='alert alert-danger mt-4 text-center fs-5 py-3'
									role='alert'
								>
									{errors.general}
								</div>
							)}
						</form>
					</div>

					<div className='text-center mt-4'>
						<button
							type='button'
							className='btn btn-dark fw-semibold'
							onClick={() => navigate('/ingredients')}
						>
							← Back
						</button>
					</div>
				</div>

				<div
					style={{
						position: 'absolute',
						bottom: '1rem',
						width: '100%',
						textAlign: 'center'
					}}
				>
					<span>WolfCafe © 2025</span>
				</div>
			</div>
		);
	};


export default IngredientComponent;
