import React from 'react'
import { useEffect, useState } from 'react'
import { getItemById, saveItem, updateItem, deleteItemById } from '../services/ItemService'
import { useNavigate, useParams } from 'react-router-dom'

const TodoComponent = () => {

    const [name, setName] = useState('')
    const [description, setDescription] = useState('')
	const [price, setPrice] = useState('')
    const { id } = useParams()
	{/* Jared */}
	const [errors, setErrors] = useState({})

    const navigate = useNavigate()

    useEffect(() => {
        if(id) {
            getItemById(id).then((response) => {
                console.log(response.data)
                setName(response.data.name)
                setDescription(response.data.description)
				setPrice(response.data.price)
            }).catch(error => {
                console.error(error)
            })
        }
    }, [id])

    function saveOrUpdateItem(e) {
        e.preventDefault()
        const item = {name, description, price}
        console.log(item)
		
		const newErrors = {};
		{/* JaredH error checking for inputs*/}
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
				
				{/*JaredH Error printing for duplicate name*/}
				if (error.response?.status === 401) {
				  setErrors({ name: "An item with that name already exists." });
				} else {
				  setErrors({ general: "Something went wrong saving the item." });
				}
            })
        }
    }
	
	
	{/* Added - JaredH, delete button logic for item update page  */}
	function deleteItem(e) {
	    e.preventDefault()
	    if(!id) return;

		deleteItemById(id)
			.then((response) => {
				console.log(response.data);
				navigate('/items');
			})
			.catch((error) => {
				console.error(error);
				
			});
	}

	 function getGeneralErrors() {
	     if (errors.general) {
	         return <div className="p-3 mb-2 bg-danger text-white">{errors.general}</div>
	     }
	 }


	{/* Edited - JaredH, added Back Button  */}
    function pageTitle() {
        if (id) {
            return 			<div style={{ position: 'relative' }}>
			  <button
			    type="button"
			    className="btn btn-dark"
			    onClick={() => navigate('/items')}
			    style={{
			      position: 'absolute',
			      top: '10px',
			      right: '10px',
			      textDecoration: 'none',
			      fontSize: '18px',
			      color: '#FFFFFF'
			    }}
			  >
			    ← Back
			  </button>
			  <h2 className="text-center mt-4">Update Item</h2>
			</div>

			
        } else {
            return 			<div style={{ position: 'relative' }}>
						  <button
						    type="button"
						    className="btn btn-dark"
						    onClick={() => navigate('/items')}
						    style={{
						      position: 'absolute',
						      top: '10px',
						      right: '10px',
						      textDecoration: 'none',
						      fontSize: '18px',
						      color: '#FFFFFF'
						    }}
						  >
						    ← Back
						  </button>
						  <h2 className="text-center mt-4">Add Item</h2>
						</div>
        }
    }

  return (
    <div className='container'>
        <br /> <br />
        <div className='row'>
            <div className='card col-md-6 offset-md-3 offset-md-3'>
                { pageTitle() }
				{ getGeneralErrors() }

                <div className='card-body'>
                    <form>
					
                        <div className='form-group mb-2'>
                            <label className='form-label'>Item Name:</label>
                            <input 
                                type='text'
                                className='form-control'
                                placeholder='Enter Item Name'
                                name='name'
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                            >
                            </input>
							{errors.name && <div className="text-danger">{errors.name}</div>}

                        </div>

                        <div className='form-group mb-2'>
                            <label className='form-label'>Item Description:</label>
                            <input 
                                type='text'
                                className='form-control'
                                placeholder='Enter Item Description'
                                name='description'
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}				
                            >
                            </input>
							{errors.description && <div className="text-danger">{errors.description}</div>}

                        </div>

                        <div className='form-group mb-2'>
                            <label className='form-label'>Item Price:</label>
							<input 
                                type='text'
                                className='form-control'
                                placeholder='Enter Item Price'
                                name='price'
                                value={price}
                                onChange={(e) => setPrice(e.target.value)}
                            >
                            </input>
							{errors.price && <div className="text-danger">{errors.price}</div>}
                        </div>						
			
                        <button type='submit' className='btn btn-success me-5' onClick={(e) => saveOrUpdateItem(e)}>Submit</button>
						<button type='button' className='btn btn-danger' onClick={(e) => deleteItem(e)} >Delete</button>

                    </form>
                </div>
            </div>
        </div>
    </div>
  )
}

export default TodoComponent