import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { isAdminUser } from '../services/AuthService'
import { getAllItems, deleteItemById } from '../services/ItemService'


const ListItemsComponent = () => {

	const [items, setItems] = useState([])

	const navigate = useNavigate()

	const isAdmin = isAdminUser()

	useEffect(() => {
		listItems()
	}, [])

	function listItems() {
		getAllItems().then((response) => {
			setItems(response.data)
		}).catch(error => {
			console.error(error)
		})
	}

	function addNewItem() {
		navigate('/add-item')
	}

	function updateItem(id) {
		console.log(id)
		navigate(`/update-item/${id}`)
	}

	function deleteItem(id) {
	    const confirmDelete = window.confirm('Are you sure you want to delete this item?');
	    if (!confirmDelete) return;

	    deleteItemById(id)
	        .then((response) => {
	            alert('Item deleted successfully!');
	            listItems(); 
	        })
	        .catch((error) => {
	            console.error(error);
	            alert('This item may have already been deleted by another user.');
	            listItems(); 
	        });
	}


	return (
		<div className='container'>
			<br /> <br />
			<h2 className='text-center'>Items</h2>
			<h6 className='text-center'>Click on Item Name to Update Or Delete</h6>
			{
				isAdmin &&
				<button className='btn btn-primary mb-2' onClick={addNewItem}>Add Item</button>
			}
			<div>
				<table className='table table-bordered table-striped' style={{ textAlign: "left", tableLayout: 'fixed', width: '100%' }}>
					<thead className='table-dark'>
						<tr>
							<th style={{ width: '100px' }}>Name</th>
							<th style={{ width: '50px' }}>Price</th>
							<th style={{ width: '100px' }}>Description</th>
							<th style={{ width: '300px' }}>Ingredients</th>
						</tr>
					</thead>
					<tbody>
						{
							items.map((item) =>
								<tr key={item.id}>
									<td>
										{isAdmin && (
											<span
												style={{ color: 'black', textDecoration: 'none', cursor: 'pointer' }}
												onClick={() => updateItem(item.id)}
												onMouseEnter={(e) => (e.target.style.color = 'blue')}
												onMouseLeave={(e) => (e.target.style.color = 'black')}
											>

												{item.name} </span>)}{!isAdmin && item.name} </td>
									<td>{item.price}</td>
									<td style={{ width: '200px', wordWrap: 'break-word', overflowWrap: 'break-word' }}>{item.description}</td>
									<td>
									{item.ingredients ? (
									  <div 
									    style={{ 
									      display: 'grid', 
									      gridTemplateColumns: 'repeat(3, 1fr)', // 3 bullet points each row
									      gap: '5px'
									    }}
									  >
									    {Object.keys(item.ingredients).map((ing, idx) => (
									      <span key={idx}>• {ing}</span>
									    ))}
									  </div>
									) : 'No ingredients'}
									</td>
								</tr>
							)
						}
					</tbody>
				</table>
			</div>
		</div>
	)
}

export default ListItemsComponent