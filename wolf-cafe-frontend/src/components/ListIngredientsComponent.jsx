import React, { useState, useEffect } from 'react';
import { getInventory, updateInventory } from '../services/InventoryService';

const ListIngredientsComponent = () => {

	const [inventory, setInventory] = useState({});
	const [toAdd, setToAdd] = useState({});
	const [newIngredient, setNewIngredient] = useState('');
	const [newAmount, setNewAmount] = useState('');
	const [error, setError] = useState('');
	const [successMsg, setSuccessMsg] = useState('');

	// fetch current inventory
	const fetchInventory = () => {
		getInventory()
			.then(res => setInventory(res.data.ingredients || {}))
			.catch(err => console.error(err));
	};

	useEffect(() => {
		fetchInventory();
	}, []);

	// auto-clear messages
	useEffect(() => {
		if (successMsg) {
			const timer = setTimeout(() => setSuccessMsg(''), 3000);
			return () => clearTimeout(timer);
		}
	}, [successMsg]);

	useEffect(() => {
		if (error) {
			const timer = setTimeout(() => setError(''), 3000);
			return () => clearTimeout(timer);
		}
	}, [error]);

	// Handle changing amounts in table inputs
	const addAmounts = (name, value) => {
		if (isNaN(value)) return;
		setToAdd(prev => ({ ...prev, [name]: value }));
	};

	// Add new ingredient row
	const addIngredient = () => {
		const name = newIngredient.trim();
		const amountNum = Number(newAmount);

		if (!name || isNaN(amountNum) || amountNum < 0) {
			setError('Please enter a valid name and non-negative number.');
			setSuccessMsg('');
			return;
		}

		const payload = {
			ingredients: { [name]: amountNum } // only this ingredient
		};

		updateInventory(payload)
			.then(() => {
				// add to inventory
				setInventory(prev => ({ ...prev, [name]: prev[name] || 0 }));
				setToAdd(prev => ({ ...prev, [name]: 0 })); // reset input after saving
				setNewIngredient('');
				setNewAmount('');
				setSuccessMsg(`Ingredient "${name}" added!`);
				setError('');
				fetchInventory(); // refresh from backend 
			})
			.catch(err => {
				console.error(err);
				setError('Failed to add ingredient.');
			});
	};


	// submit all additions
	const submitAllChanges = () => {
		const payload = { ingredients: {} };

		Object.entries(toAdd).forEach(([name, addValue]) => {
			const num = Number(addValue);
			if (!isNaN(num) && num > 0) payload.ingredients[name] = num; // only send amount to add
		});

		if (Object.keys(payload.ingredients).length === 0) {
			setError('No changes to submit.');
			setSuccessMsg('');
			return;
		}

		updateInventory(payload)
			.then(() => {
				setSuccessMsg('Inventory successfully updated!');
				setError('');
				setToAdd({});
				fetchInventory(); // refresh from backend
			})
			.catch(err => {
				console.error(err);
				setError('Failed to update inventory.');
			});
	};

	// table rows
	const tableRows = () => {
		return Object.entries(inventory).map(([name, amount]) => {
			const addValue = Number(toAdd[name] || 0);
			return (
				<tr key={name}>
					<td className="fw-semibold">{name}</td>
					<td>{amount}</td>
					<td>
						<input
							type="number"
							className="form-control text-center"
							style={{ width: '7rem', margin: 'auto' }}
							value={toAdd[name] || ''}
							onChange={e => addAmounts(name, e.target.value)}
							placeholder="0"
						/>
					</td>
					<td className="fw-bold">{amount + addValue}</td>
				</tr>
			);
		});
	};

	return (
		<div className="d-flex justify-content-center align-items-center vh-20" style={{ paddingTop: '40px' }}>
			<div
				className="card shadow-lg p-5"
				style={{ width: '75rem', transform: 'scale(0.9)', backgroundColor: '#fff', borderRadius: '1rem', maxHeight: '200vh', overflowY: 'auto' }}
			>
				<div className="card-header text-center border-0 mb-4 bg-white">
					<h2 className="fw-bold text-dark mb-0">Inventory</h2>
					<p className="text-secondary mt-2">Add or restock ingredients below</p>
				</div>

				<div className="card-body">
					<table className="table table-bordered align-middle text-center">
						<thead className="table-light">
							<tr>
								<th className="fs-5">Ingredient</th>
								<th className="fs-5">Current Amount</th>
								<th className="fs-5">Amount to Add</th>
								<th className="fs-5">New Total</th>
							</tr>
						</thead>
						<tbody>
							{tableRows()}

							{/* New ingredient input row */}
							<tr>
								<td>
									<input
										type="text"
										className="form-control text-center"
										placeholder="New ingredient"
										value={newIngredient}
										onChange={e => setNewIngredient(e.target.value)}
									/>
								</td>
								<td></td>
								<td>
									<div className="d-flex justify-content-center">
										<input
											type="number"
											className="form-control text-center me-2"
											style={{ width: '7rem' }}
											placeholder="Amount"
											value={newAmount}
											onChange={e => setNewAmount(e.target.value)}
										/>
										<button className="btn btn-success fw-bold" onClick={addIngredient}>
											+
										</button>
									</div>
								</td>
								<td className="fw-bold">{newAmount ? Number(newAmount) : ''}</td>
							</tr>
						</tbody>
					</table>

					{error && <div className="p-2 mb-2 text-white text-center" style={{ backgroundColor: '#CC0000', borderRadius: '8px' }}>{error}</div>}
					{successMsg && <div className="p-2 mb-2 text-white text-center" style={{ backgroundColor: '#28a745', borderRadius: '8px' }}>{successMsg}</div>}

					<div className="text-center mt-4">
						<button
							className="btn btn-lg w-50 fw-bold"
							onClick={submitAllChanges}
							style={{ backgroundColor: '#000000', color: 'white', border: 'none' }}
						>
							Submit All
						</button>
					</div>
				</div>
			</div>
		</div>
	);
};

export default ListIngredientsComponent;
