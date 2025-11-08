import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { isAdminUser } from '../services/AuthService'
import { getInventory } from '../services/InventoryService';

const ListIngredientsComponent = () => {
	const navigate = useNavigate();
	const [ingredients, setIngredients] = useState([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState('');

	useEffect(() => {
		getInventory()
			.then(res => {
				// res.data.ingredients is an object like { "Sugar": 5, "Coffee": 10 }
				const ingredientArray = Object.entries(res.data.ingredients || {}).map(
					([name, amount]) => ({ name, amount })
				);
				setIngredients(ingredientArray);
				setLoading(false);
			})
			.catch(err => {
				console.error(err);
				setError('Failed to load inventory.');
				setLoading(false);
			});
	}, []);


	function addNewIngredient() {
		navigate('/add-ingredient')
	}

	function updateIngredient(name) {
		navigate(`/update-ingredient/${name}`);
	}


	if (loading) return <p>Loading inventory...</p>;
	if (error) return <p className="text-danger">{error}</p>;

	return (
		<div className="container" style={{ paddingTop: '40px' }}>
			<br /> <br />
			<h2>Inventory</h2>
			<h6 className='text-center'>Click on Ingredient Name to Update Or Delete</h6>

			{
				//isAdmin && 
				<button className='btn btn-primary mb-2' onClick={addNewIngredient}>Add Ingredient</button>
			}

			<table className="table table-bordered table-striped" style={{ textAlign: "left" }}>
				<thead className="table-dark">
					<tr>
						<th>Name</th>
						<th>Amount</th>
					</tr>
				</thead>
				<tbody>
					{ingredients.length === 0 ? (
						<tr>
							<td colSpan="2" className="text-center">No ingredients in inventory</td>
						</tr>
					) : (
						ingredients.map((ing, idx) => (
							<tr key={idx}>
								<td>			  {//isAdmin && 
									(
										<span
											style={{ color: 'black', textDecoration: 'none', cursor: 'pointer', fontSize: '1.5rem' }}
											onClick={() => updateIngredient(ing.name)}
											onMouseEnter={(e) => (e.target.style.color = 'blue')}
											onMouseLeave={(e) => (e.target.style.color = 'black')}
										>

											{ing.name} </span>)}</td>
								<td
								style={{ fontSize: '1.5rem' }}>
								{ing.amount}</td>
							</tr>
						))
					)}
				</tbody>
			</table>
		</div>
	);
};

export default ListIngredientsComponent;
