import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { isAdminUser, isStaffUser } from '../services/AuthService'
import { getAllItems, deleteItemById } from '../services/ItemService'


const ListItemsComponent = () => {

	const [items, setItems] = useState([])
	const navigate = useNavigate()
	const isAdmin = isAdminUser()
	const isStaff = isStaffUser()

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

	return (
		<div className="page-container d-flex justify-content-center align-items-start">
			<div
				className="card shadow-lg p-5"
				style={{
					width: '75rem',
					transform: 'scale(1)',
					backgroundColor: '#fff',
					borderRadius: '1rem',
					maxHeight: '200vh',
					overflowY: 'auto',
				}}
			>
				<div className="card-header text-center border-0 mb-4 bg-white">
					<h2 className="fw-bold text-dark mb-0">Items</h2>
					<p className="text-secondary mt-2">Click on Item Name to Update or Delete</p>
					{(isAdmin || isStaff) && (
						<button className="btn btn-success mt-3 fw-bold" onClick={addNewItem}>
							Add Item
						</button>
					)}
				</div>

				<div className="card-body">
					<table className="table table-bordered align-middle text-center">
						<thead className="table-light">
							<tr>
								<th className="fs-5">Name</th>
								<th className="fs-5">Price</th>
								<th className="fs-5">Description</th>
								<th className="fs-5">Ingredients</th>
							</tr>
						</thead>
						<tbody>
							{items.map((item) => (
								<tr key={item.id}>
									<td className="fw-semibold">
										{isAdmin || isStaff ? (
											<span
												style={{ cursor: 'pointer', color: 'black', textDecoration: 'none' }}
												onClick={() => updateItem(item.id)}
												onMouseEnter={(e) => (e.target.style.color = 'blue')}
												onMouseLeave={(e) => (e.target.style.color = 'black')}
											>
												{item.name}
											</span>
										) : (
											item.name
										)}
									</td>
									<td>{item.price}</td>
									<td style={{ wordWrap: 'break-word', overflowWrap: 'break-word' }}>{item.description}</td>
									<td>
										{item.ingredients ? (
											<div
												style={{
													display: 'grid',
													gridTemplateColumns: 'repeat(3, 1fr)',
													gap: '5px',
												}}
											>
												{Object.keys(item.ingredients).map((ing, idx) => (
													<span key={idx}>
														• {ing} ({item.ingredients[ing]})
													</span>
												))}
											</div>
										) : (
											'No ingredients'
										)}
									</td>
								</tr>
							))}
						</tbody>
					</table>
				</div>
			</div>
		</div>
	)
}
export default ListItemsComponent