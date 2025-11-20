import React, { useState, useEffect } from 'react';
import { deleteUser, getAllUsers, isAdminUser } from '../services/AuthService';
import { useNavigate } from 'react-router-dom';

const ListStaffComponent = () => {
	const [users, setUsers] = useState([])
	const isAdmin = isAdminUser()
	const [error, setError] = useState('');
	const [successMsg, setSuccessMsg] = useState('');
	const navigate = useNavigate();

	useEffect(() => {
		listUsers()
	}, [])

	function listUsers() {
		getAllUsers().then((response) => {
			// filter users whose roles include CUSTOMER
			const customerUsers = response.data.filter(user =>
				user.roles.some(role => role.name.includes('CUSTOMER'))
			);
			setUsers(customerUsers);
		}).catch(error => {
			console.error(error)
			setError('Failed to load users.');
		})
	}

	useEffect(() => {
		if (successMsg) {
			const timer = setTimeout(() => setSuccessMsg(''), 3000); // clears after 3s
			return () => clearTimeout(timer);
		}
	}, [successMsg]);

	useEffect(() => {
		if (error) {
			const timer = setTimeout(() => setError(''), 3000); // clears after 3s
			return () => clearTimeout(timer);
		}
	}, [error]);

	function deleteStaff(id) {
		const confirmDelete = window.confirm('Are you sure you want to delete this user?');
		if (!confirmDelete) return;

		deleteUser(id)
			.then((response) => {
				alert('User deleted successfully!');
				listUsers();
			})
			.catch((error) => {
				console.error(error);
				alert('This User may have already been deleted by another user.');
				listUsers();
			});
	}

	return (
		<div className="d-flex justify-content-center align-items-center vh-100" style={{ paddingTop: '40px' }}>
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
					<h2 className="fw-bold text-dark mb-0">Customers</h2>
					<p className="text-secondary mt-2">List of registered Customers</p>
					<p className="text-secondary mt-2">Click on Customer's username to delete</p>

				</div>

				<div className="card-body">
					<table className="table table-bordered align-middle text-center">
						<thead className="table-light">
							<tr>
								<th className="fs-5">Username</th>
								<th className="fs-5">Email</th>
								<th className="fs-5">Roles</th>
							</tr>
						</thead>
						<tbody>
							{users.length > 0 ? (
								users.map((user) => (
									<tr key={user.id}>
										<td>
											{isAdmin ? (
												<span
													style={{ cursor: 'pointer', color: 'black', textDecoration: 'none' }}
													onClick={() => navigate(`/edit-user/${user.id}`)}
													onMouseEnter={(e) => (e.target.style.color = 'blue')}
													onMouseLeave={(e) => (e.target.style.color = 'black')}
												>
													{user.username}
												</span>
											) : (
												user.username
											)}
										</td>

										<td>{user.email}</td>
										<td>
											{user.roles && user.roles.length > 0
												? user.roles
													.map(role => role.name.replace('ROLE_', '')) // removes "ROLE_"
													.join(', ')
												: '-'}
										</td>
									</tr>
								))
							) : (
								<tr>
									<td colSpan="4" className="text-center text-secondary">
										No users found
									</td>
								</tr>
							)}
						</tbody>
					</table>

					{error && (
						<div
							className="p-2 mb-2 text-white text-center"
							style={{ backgroundColor: '#CC0000', borderRadius: '8px' }}
						>
							{error}
						</div>
					)}

					{successMsg && (
						<div
							className="p-2 mb-2 text-white text-center"
							style={{ backgroundColor: '#28a745', borderRadius: '8px' }}
						>
							{successMsg}
						</div>
					)}
				</div>
			</div>
		</div>
	);
};

export default ListStaffComponent;