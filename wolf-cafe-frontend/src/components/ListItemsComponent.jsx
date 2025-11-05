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
		console.log(id)
		deleteItemById(id).then((response) => {
			listItems()
		}).catch(error => {
			console.error(error)
		})
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
				<table className='table table-bordered table-striped' style={{ textAlign: "left" }}>
					<thead className='table-dark'>
						<tr>
							<th>Name</th>
							<th>Price</th>
							<th>Description</th>
							<th>Ingredients</th>
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
									<td>{item.description}</td>
									<td>
										{item.ingredients
											? Object.keys(item.ingredients).join(', ')
											: 'No ingredients'}
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