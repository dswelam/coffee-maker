import React from 'react'
import { useEffect, useState } from 'react'
import { getItemById, saveItem, updateItem, deleteItemById } from '../services/ItemService'
import { useNavigate, useParams } from 'react-router-dom'
import { getInventory } from '../services/InventoryService';


const ItemComponent = () => {

	const [name, setName] = useState('')
	const [description, setDescription] = useState('')
	const [price, setPrice] = useState('')
	const [allIngredients, setAllIngredients] = useState([]);
	const [ingredientAmounts, setIngredientAmounts] = useState({});
	const [deleteMessage, setDeleteMessage] = useState('');
	const [errors, setErrors] = useState({})

	const { id } = useParams()
	const navigate = useNavigate()

	useEffect(() => {
		const fetchIngredients = async () => {

			try {
				const res = await getInventory();
				let ingredientNames = [];

				if (Array.isArray(res.data.ingredients)) {
					// API returns an array of objects
					ingredientNames = res.data.ingredients.map(i => i.name);
				} else if (typeof res.data.ingredients === 'object' && res.data.ingredients !== null) {
					// API returns an object { "Sugar": 5, "Coffee": 10 }
					ingredientNames = Object.keys(res.data.ingredients);
				}

				setAllIngredients(ingredientNames);
			} catch (err) {
				console.error(err);
			}
		};

		fetchIngredients();

		if (id) {
			getItemById(id).then((response) => {
				console.log(response.data)
				setName(response.data.name)
				setDescription(response.data.description)
				setPrice(response.data.price)
				setIngredientAmounts(response.data.ingredients || {});
			}).catch(error => {
				console.error(error)
			})
		}
	}, [id])

	function saveOrUpdateItem(e) {
		e.preventDefault()
		const item = {
			name,
			description,
			price,
			ingredients: ingredientAmounts
		}
		console.log(item)

		const newErrors = {};
		{/* JaredH error checking for inputs*/ }
		if (!name.trim()) newErrors.name = "Name is required.";
		if (!description.trim()) newErrors.description = "Description is required.";
		if (!price || isNaN(price) || Number(price) <= 0)
			newErrors.price = "Enter a valid positive number for price.";
		// if error, stop 
		setErrors(newErrors);
		if (Object.keys(newErrors).length > 0) return;

		if (id) {
			updateItem(id, item).then((response) => {
				console.log(response.data)
				navigate('/items')
			}).catch(error => {
				console.error(error)
			})
		} else {
			saveItem(item).then((response) => {
				console.log(response.data)
				navigate('/items')
			}).catch(error => {
				console.error(error)

				{/*JaredH Error printing for duplicate name*/ }
				if (error.response?.status === 401) {
					setErrors({ name: "An item with that name already exists." });
				} else {
					setErrors({ general: "Something went wrong saving the item." });
				}
			})
		}
	}

	{/* Added - JaredH, delete button logic for item update page  */ }
	function deleteItem() {
		const confirmDelete = window.confirm('Are you sure you want to delete this item?');
		if (!confirmDelete) return;

		deleteItemById(id)
			.then(() => {
				setDeleteMessage('Item deleted successfully!');
				setTimeout(() => navigate('/items'), 2500);
			})
			.catch((error) => {
				console.error(error);
				setDeleteMessage('This item may have already been deleted by another user.');
				setTimeout(() => navigate('/items'), 2500);
			});
	}

	{/* Edited - JaredH, added Back Button  */ }
	const pageTitle = () => (
		<div className="text-center mb-4" style={{ paddingTop: '40px' }}>
			<button
				type="button"
				className="btn btn-dark"
				onClick={() => navigate('/items')}
				style={{ position: 'absolute', top: '10px', right: '10px', fontSize: '18px', color: '#FFFFFF' }}
			>
				← Back
			</button>
			<h2 className="fw-bold">{id ? 'Update Item' : 'Add Item'}</h2>
		</div>
	)

	return (
		<div className="d-flex justify-content-center align-items-start" style={{ paddingTop: '40px' }}>
		  <div className="card shadow-lg p-5" style={{ width: '45rem', backgroundColor: '#fff', borderRadius: '1rem', transform: 'scale(0.9)' }}>
				{pageTitle()}

				{errors.general && <div className="alert alert-danger text-center">{errors.general}</div>}
				{deleteMessage && <div className="alert alert-info text-center">{deleteMessage}</div>}

				<form>
					<div className="mb-3">
						<label className="form-label fw-semibold">Item Name</label>
						<input type="text" className="form-control form-control-lg" placeholder="Enter item name" value={name} onChange={e => setName(e.target.value)} />
						{errors.name && <div className="text-danger">{errors.name}</div>}
					</div>

					<div className="mb-3">
						<label className="form-label fw-semibold">Item Description</label>
						<input type="text" className="form-control form-control-lg" placeholder="Enter description" value={description} onChange={e => setDescription(e.target.value)} />
						{errors.description && <div className="text-danger">{errors.description}</div>}
					</div>

					<div className="mb-3">
						<label className="form-label fw-semibold">Item Price</label>
						<input type="number" className="form-control form-control-lg" placeholder="Enter price" value={price} onChange={e => setPrice(e.target.value)} />
						{errors.price && <div className="text-danger">{errors.price}</div>}
					</div>

					<div className="mb-3">
						<label className="form-label fw-semibold">Ingredients</label>
						<div style={{ maxHeight: '200px', overflowY: 'auto', border: '1px solid #ccc', padding: '8px' }}>
						  {allIngredients.map(ing => (
						    <div key={ing} className="d-flex align-items-center mb-2">
						      <span style={{ width: "150px" }}>{ing}</span>

						      <input
						        type="number"
						        min="0"
						        className="form-control"
						        style={{ width: "80px" }}
						        value={ingredientAmounts[ing] || ""}
						        onChange={e => {
						          const val = e.target.value;
						          setIngredientAmounts(prev => ({
						            ...prev,
						            [ing]: val === "" ? "" : Number(val)
						          }));
						        }}
						      />
						    </div>
						  ))}
						</div>

					</div>

					<div className={`d-flex mt-4 ${id ? 'justify-content-between' : 'justify-content-center'}`}>
						<button
							type="submit"
							className={`btn btn-lg ${id ? 'w-50 me-2' : 'w-50'} fw-bold`}
							onClick={saveOrUpdateItem}
							style={{ backgroundColor: '#28a745', color: 'white', border: 'none' }} // green
						>
							Submit
						</button>

						{id && (
							<button
								type="button"
								className="btn btn-lg w-50 fw-bold"
								onClick={deleteItem}
								style={{ backgroundColor: '#CC0000', color: 'white', border: 'none' }} // black
							>
								Delete
							</button>
						)}
					</div>
				</form>
			</div>
		</div>
	)
}

export default ItemComponent